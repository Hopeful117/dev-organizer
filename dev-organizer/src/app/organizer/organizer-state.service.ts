import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { firstValueFrom, forkJoin, Observable } from 'rxjs';
import { AttentionItem, DevlogProjectOption, InboxItem, Project, ProjectSummary, WorkItem } from './organizer.models';
import { OrganizerApiClient } from './organizer.api';

@Injectable({ providedIn: 'root' })
export class OrganizerStateService {
  private readonly api = inject(OrganizerApiClient);

  readonly projects = signal<Project[]>([]);
  readonly inboxItems = signal<InboxItem[]>([]);
  readonly workItems = signal<WorkItem[]>([]);
  readonly attentions = signal<AttentionItem[]>([]);
  readonly devlogProjects = signal<DevlogProjectOption[]>([]);
  readonly activeProjects = computed(() => this.projects().filter(project => project.status === 'ACTIVE'));
  readonly inProgressWork = computed(() => this.workItems().filter(item => item.status === 'IN_PROGRESS'));
  readonly todoWork = computed(() => this.workItems().filter(item => item.status === 'TODO'));
  readonly activeWork = computed(() => this.workItems().filter(item => item.status !== 'DONE'));
  readonly completedWork = computed(() => this.workItems().filter(item => item.status === 'DONE'));
  readonly projectSummaries = computed<ProjectSummary[]>(() => this.activeProjects().map(project => ({
    project,
    activeWorkCount: this.activeWork().filter(item => item.projectId === project.id).length,
    inboxCount: this.inboxItems().filter(item => item.projectId === project.id).length,
  })));
  readonly isLoading = signal(false);
  readonly actionInProgress = signal<string | null>(null);
  readonly error = signal<string | null>(null);
  readonly attentionError = signal<string | null>(null);
  readonly attentionLoading = signal(false);

  async loadAll(): Promise<void> {
    this.isLoading.set(true);
    this.error.set(null);

    try {
      const state = await firstValueFrom(forkJoin({
        projects: this.api.listProjects(),
        inboxItems: this.api.listCapturedInbox(),
        workItems: this.api.listWork(),
      }));
      this.projects.set(state.projects);
      this.inboxItems.set(state.inboxItems);
      this.workItems.set(state.workItems);
    } catch (error) {
      this.setError(error);
    } finally {
      this.isLoading.set(false);
    }
  }

  async capture(content: string, projectId: string | null): Promise<boolean> {
    return this.runAction('capture', () => this.api.capture(content, projectId), item => {
      this.inboxItems.update(items => [item, ...items]);
    });
  }

  async dismissInboxItem(id: string): Promise<boolean> {
    return this.runAction('dismiss', () => this.api.dismissInboxItem(id), () => {
      this.inboxItems.update(items => items.filter(item => item.id !== id));
    });
  }

  async associateInboxItem(id: string, projectId: string | null): Promise<boolean> {
    return this.runAction('associate-inbox', () => this.api.associateInboxItem(id, projectId), item => {
      this.inboxItems.update(items => items.map(existing => existing.id === id ? item : existing));
    });
  }

  async promoteInboxItem(id: string): Promise<boolean> {
    return this.runAction('promote', () => this.api.promoteInboxItem(id), workItem => {
      this.inboxItems.update(items => items.filter(item => item.id !== id));
      this.workItems.update(items => [workItem, ...items]);
    });
  }

  async createProject(name: string): Promise<boolean> {
    return this.runAction('create-project', () => this.api.createProject(name), project => {
      this.projects.update(projects => [...projects, project]);
    });
  }

  async archiveProject(id: string): Promise<boolean> {
    return this.runAction('archive-project', () => this.api.archiveProject(id), () => {
      this.projects.update(projects => projects.filter(project => project.id !== id));
    });
  }

  async associateWorkItem(id: string, projectId: string | null): Promise<boolean> {
    return this.runAction('associate-work', () => this.api.associateWorkItem(id, projectId), item => {
      this.workItems.update(items => items.map(existing => existing.id === id ? item : existing));
    });
  }

  async startWorkItem(id: string): Promise<boolean> {
    return this.updateWorkItem('start', () => this.api.startWorkItem(id), id);
  }

  async completeWorkItem(id: string): Promise<boolean> {
    return this.updateWorkItem('complete', () => this.api.completeWorkItem(id), id);
  }

  async loadDevlogProjects(): Promise<void> {
    try {
      this.devlogProjects.set(await firstValueFrom(this.api.listDevlogProjects()));
    } catch {
      this.devlogProjects.set([]);
    }
  }

  async refreshAttention(): Promise<void> {
    this.attentionLoading.set(true);
    this.attentionError.set(null);
    try {
      const result = await firstValueFrom(this.api.refreshAttention());
      this.attentions.set(result.items);
      if (result.errors.length > 0) {
        this.attentionError.set('DevLog is unavailable for one or more linked projects.');
      }
    } catch {
      this.attentionError.set('DevLog attention could not be refreshed.');
    } finally {
      this.attentionLoading.set(false);
    }
  }

  async linkDevlogProject(projectId: string, devlogProjectId: string, devlogProjectSlug: string): Promise<boolean> {
    return this.runAction('link-devlog', () => this.api.linkDevlogProject(projectId, devlogProjectId, devlogProjectSlug), project => {
      this.projects.update(projects => projects.map(existing => existing.id === project.id ? project : existing));
    });
  }

  async unlinkDevlogProject(projectId: string): Promise<boolean> {
    return this.runAction('unlink-devlog', () => this.api.unlinkDevlogProject(projectId), project => {
      this.projects.update(projects => projects.map(existing => existing.id === project.id ? project : existing));
    });
  }

  async acknowledgeAttention(id: string): Promise<boolean> {
    return this.runAction('acknowledge-attention', () => this.api.acknowledgeAttention(id), () => {
      this.attentions.update(items => items.filter(item => item.id !== id));
    });
  }

  async dismissAttention(id: string): Promise<boolean> {
    return this.runAction('dismiss-attention', () => this.api.dismissAttention(id), () => {
      this.attentions.update(items => items.filter(item => item.id !== id));
    });
  }

  setError(error: unknown): void {
    if (error instanceof HttpErrorResponse && error.error?.message) {
      this.error.set(error.error.message);
      return;
    }

    if (error instanceof Error && error.message) {
      this.error.set(error.message);
      return;
    }

    this.error.set('The Organizer API request failed.');
  }

  private async updateWorkItem(action: string, request: () => ReturnType<OrganizerApiClient['startWorkItem']>, id: string): Promise<boolean> {
    return this.runAction(action, request, item => {
      this.workItems.update(items => items.map(existing => existing.id === id ? item : existing));
    });
  }

  private async runAction<T>(action: string, request: () => Observable<T>, apply: (value: T) => void): Promise<boolean> {
    if (this.actionInProgress() !== null) {
      return false;
    }

    this.actionInProgress.set(action);
    try {
      const value = await firstValueFrom(request());
      apply(value);
      this.error.set(null);
      return true;
    } catch (error) {
      this.setError(error);
      return false;
    } finally {
      this.actionInProgress.set(null);
    }
  }
}
