import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { firstValueFrom, forkJoin } from 'rxjs';
import { InboxItem, Project, WorkItem } from './organizer.models';
import { OrganizerApiClient } from './organizer.api';

@Injectable({ providedIn: 'root' })
export class OrganizerStateService {
  private readonly api = inject(OrganizerApiClient);

  readonly projects = signal<Project[]>([]);
  readonly inboxItems = signal<InboxItem[]>([]);
  readonly workItems = signal<WorkItem[]>([]);
  readonly activeProjects = computed(() => this.projects().filter(project => project.status === 'ACTIVE'));
  readonly activeWork = computed(() => this.workItems().filter(item => item.status !== 'DONE'));
  readonly completedWork = computed(() => this.workItems().filter(item => item.status === 'DONE'));
  readonly isLoading = signal(false);
  readonly error = signal<string | null>(null);

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
    try {
      const item = await firstValueFrom(this.api.capture(content, projectId));
      this.inboxItems.update(items => [item, ...items]);
      this.error.set(null);
      return true;
    } catch (error) {
      this.setError(error);
      return false;
    }
  }

  async dismissInboxItem(id: string): Promise<boolean> {
    try {
      await firstValueFrom(this.api.dismissInboxItem(id));
      this.inboxItems.update(items => items.filter(item => item.id !== id));
      this.error.set(null);
      return true;
    } catch (error) {
      this.setError(error);
      return false;
    }
  }

  async associateInboxItem(id: string, projectId: string | null): Promise<boolean> {
    try {
      const item = await firstValueFrom(this.api.associateInboxItem(id, projectId));
      this.inboxItems.update(items => items.map(existing => existing.id === id ? item : existing));
      this.error.set(null);
      return true;
    } catch (error) {
      this.setError(error);
      return false;
    }
  }

  async promoteInboxItem(id: string): Promise<boolean> {
    try {
      const workItem = await firstValueFrom(this.api.promoteInboxItem(id));
      this.inboxItems.update(items => items.filter(item => item.id !== id));
      this.workItems.update(items => [workItem, ...items]);
      this.error.set(null);
      return true;
    } catch (error) {
      this.setError(error);
      return false;
    }
  }

  async createProject(name: string): Promise<boolean> {
    try {
      const project = await firstValueFrom(this.api.createProject(name));
      this.projects.update(projects => [...projects, project]);
      this.error.set(null);
      return true;
    } catch (error) {
      this.setError(error);
      return false;
    }
  }

  async archiveProject(id: string): Promise<boolean> {
    try {
      await firstValueFrom(this.api.archiveProject(id));
      this.projects.update(projects => projects.filter(project => project.id !== id));
      this.error.set(null);
      return true;
    } catch (error) {
      this.setError(error);
      return false;
    }
  }

  async associateWorkItem(id: string, projectId: string | null): Promise<boolean> {
    try {
      const item = await firstValueFrom(this.api.associateWorkItem(id, projectId));
      this.workItems.update(items => items.map(existing => existing.id === id ? item : existing));
      this.error.set(null);
      return true;
    } catch (error) {
      this.setError(error);
      return false;
    }
  }

  async startWorkItem(id: string): Promise<boolean> {
    return this.updateWorkItem(() => this.api.startWorkItem(id), id);
  }

  async completeWorkItem(id: string): Promise<boolean> {
    return this.updateWorkItem(() => this.api.completeWorkItem(id), id);
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

  private async updateWorkItem(request: () => ReturnType<OrganizerApiClient['startWorkItem']>, id: string): Promise<boolean> {
    try {
      const item = await firstValueFrom(request());
      this.workItems.update(items => items.map(existing => existing.id === id ? item : existing));
      this.error.set(null);
      return true;
    } catch (error) {
      this.setError(error);
      return false;
    }
  }
}
