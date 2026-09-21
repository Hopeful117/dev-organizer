import { Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrganizerStateService } from './organizer-state.service';

@Component({
  selector: 'app-organizer',
  imports: [DatePipe, FormsModule],
  templateUrl: './organizer.html',
  styleUrl: './organizer.css',
})
export class OrganizerHome implements OnInit {
  readonly state = inject(OrganizerStateService);
  captureContent = '';
  captureProjectId = '';
  projectName = '';
  readonly inboxProjectSelection: Record<string, string> = {};
  readonly workProjectSelection: Record<string, string> = {};

  ngOnInit(): void {
    void this.state.loadAll();
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
}
