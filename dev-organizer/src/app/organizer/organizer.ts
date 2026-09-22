import { Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { OrganizerStateService } from './organizer-state.service';
import { WorkspaceNavigationService } from '../workspace/workspace-navigation.service';

@Component({
  selector: 'app-organizer',
  imports: [DatePipe, FormsModule, RouterLink],
  templateUrl: './organizer.html',
  styleUrl: './organizer.css',
})
export class OrganizerHome implements OnInit {
  readonly state = inject(OrganizerStateService);
  private readonly workspaceNavigation = inject(WorkspaceNavigationService);
  captureContent = '';
  captureProjectId = '';
  projectName = '';
  readonly inboxProjectSelection: Record<string, string> = {};
  readonly workProjectSelection: Record<string, string> = {};
  readonly devlogProjectSelection: Record<string, string> = {};

  ngOnInit(): void {
    void this.state.loadAll();
    void this.state.refreshAttention();
    void this.state.loadDevlogProjects();
  }

  async capture(): Promise<void> {
    const content = this.captureContent.trim();
    if (!content) {
      this.state.setError('Capture content is required.');
      return;
    }

    const captured = await this.state.capture(content, this.captureProjectId || null);
    if (captured) {
      this.captureContent = '';
      this.captureProjectId = '';
    }
  }

  async createProject(): Promise<void> {
    const name = this.projectName.trim();
    if (!name) {
      this.state.setError('Project name is required.');
      return;
    }

    if (await this.state.createProject(name)) {
      this.projectName = '';
    }
  }

  async setInboxProject(id: string, projectId: string): Promise<void> {
    this.inboxProjectSelection[id] = projectId;
    await this.state.associateInboxItem(id, projectId || null);
  }

  async setWorkProject(id: string, projectId: string): Promise<void> {
    this.workProjectSelection[id] = projectId;
    await this.state.associateWorkItem(id, projectId || null);
  }

  async linkDevlogProject(projectId: string): Promise<void> {
    const devlogProjectId = this.devlogProjectSelection[projectId];
    const devlogProject = this.state
      .devlogProjects()
      .find((project) => project.id === devlogProjectId);
    if (!devlogProject) {
      this.state.setError('Select a DevLog project before linking.');
      return;
    }
    if (await this.state.linkDevlogProject(projectId, devlogProject.id, devlogProject.slug)) {
      delete this.devlogProjectSelection[projectId];
    }
  }

  async unlinkDevlogProject(projectId: string): Promise<void> {
    await this.state.unlinkDevlogProject(projectId);
  }

  projectLabel(projectId: string): string {
    return (
      this.state.projects().find((project) => project.id === projectId)?.name ?? 'Linked project'
    );
  }

  guidanceLabel(guidance: string): string {
    return guidance === 'REFRESH_RECOMMENDED' ? 'Refresh recommended' : guidance;
  }

  inspectAttention(sourceReference: string): void {
    this.workspaceNavigation.navigateToResource(sourceReference);
  }
}
