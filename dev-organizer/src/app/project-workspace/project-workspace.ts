import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { OrganizerStateService } from '../organizer/organizer-state.service';
import { WorkspaceNavigationService } from '../workspace/workspace-navigation.service';

@Component({
  selector: 'app-project-workspace',
  imports: [DatePipe, FormsModule, RouterLink],
  templateUrl: './project-workspace.html',
  styleUrl: './project-workspace.css',
})
export class ProjectWorkspace implements OnInit {
  readonly state = inject(OrganizerStateService);
  private readonly workspaceNavigation = inject(WorkspaceNavigationService);
  private readonly route = inject(ActivatedRoute);
  readonly projectId = signal('');
  readonly captureContent = signal('');
  devlogProjectSelection = '';

  readonly project = computed(
    () => this.state.projects().find((project) => project.id === this.projectId()) ?? null,
  );
  readonly projectWork = computed(() =>
    this.state.workItems().filter((item) => item.projectId === this.projectId()),
  );
  readonly projectTodoWork = computed(() =>
    this.projectWork().filter((item) => item.status === 'TODO'),
  );
  readonly projectInProgressWork = computed(() =>
    this.projectWork().filter((item) => item.status === 'IN_PROGRESS'),
  );
  readonly projectCompletedWork = computed(() =>
    this.projectWork().filter((item) => item.status === 'DONE'),
  );
  readonly projectInbox = computed(() =>
    this.state.inboxItems().filter((item) => item.projectId === this.projectId()),
  );
  readonly projectAttention = computed(() =>
    this.state.attentions().filter((item) => item.projectId === this.projectId()),
  );

  ngOnInit(): void {
    this.projectId.set(this.route.snapshot.paramMap.get('projectId') ?? '');
    void this.state.loadWorkspace();
  }

  async capture(): Promise<void> {
    const content = this.captureContent().trim();
    if (!content) {
      this.state.setError('Capture content is required.');
      return;
    }
    if (await this.state.capture(content, this.projectId())) {
      this.captureContent.set('');
    }
  }

  async linkDevlogProject(): Promise<void> {
    const project = this.project();
    const devlogProject = this.state
      .devlogProjects()
      .find((item) => item.id === this.devlogProjectSelection);
    if (!project || !devlogProject) {
      this.state.setError('Select a DevLog project before linking.');
      return;
    }
    if (await this.state.linkDevlogProject(project.id, devlogProject.id, devlogProject.slug)) {
      this.devlogProjectSelection = '';
    }
  }

  async unlinkDevlogProject(): Promise<void> {
    const project = this.project();
    if (project) await this.state.unlinkDevlogProject(project.id);
  }

  openDevlogProject(): void {
    const devlogProject = this.project()?.devlogProject;
    if (devlogProject) {
      this.workspaceNavigation.navigateToProjectContext(devlogProject.slug);
    }
  }

  guidanceLabel(guidance: string): string {
    return guidance === 'REFRESH_RECOMMENDED' ? 'Refresh recommended' : guidance;
  }

  inspectAttention(sourceReference: string): void {
    this.workspaceNavigation.navigateToResource(sourceReference);
  }
}
