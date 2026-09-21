import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AttentionItem, AttentionRefreshResponse, DevlogProjectOption, InboxItem, Project, WorkItem } from './organizer.models';

interface ProjectCreateRequest {
  name: string;
}

interface InboxCaptureRequest {
  content: string;
  projectId: string | null;
}

interface ProjectAssociationRequest {
  projectId: string | null;
}

interface WorkCreateRequest {
  title: string;
  description: string | null;
  projectId: string | null;
}

@Injectable({ providedIn: 'root' })
export class OrganizerApiClient {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1';

  createProject(name: string): Observable<Project> {
    return this.http.post<Project>(`${this.baseUrl}/projects`, { name } satisfies ProjectCreateRequest);
  }

  listProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.baseUrl}/projects`);
  }

  listDevlogProjects(): Observable<DevlogProjectOption[]> {
    return this.http.get<DevlogProjectOption[]>(`${this.baseUrl}/integrations/devlog/projects`);
  }

  linkDevlogProject(projectId: string, devlogProjectId: string, devlogProjectSlug: string): Observable<Project> {
    return this.http.put<Project>(`${this.baseUrl}/projects/${projectId}/devlog`, {
      devlogProjectId,
      devlogProjectSlug,
    });
  }

  unlinkDevlogProject(projectId: string): Observable<Project> {
    return this.http.delete<Project>(`${this.baseUrl}/projects/${projectId}/devlog`);
  }

  archiveProject(id: string): Observable<Project> {
    return this.http.post<Project>(`${this.baseUrl}/projects/${id}/archive`, {});
  }

  capture(content: string, projectId: string | null): Observable<InboxItem> {
    const request: InboxCaptureRequest = { content, projectId };
    return this.http.post<InboxItem>(`${this.baseUrl}/inbox`, request);
  }

  listCapturedInbox(): Observable<InboxItem[]> {
    return this.http.get<InboxItem[]>(`${this.baseUrl}/inbox`);
  }

  dismissInboxItem(id: string): Observable<InboxItem> {
    return this.http.post<InboxItem>(`${this.baseUrl}/inbox/${id}/dismiss`, {});
  }

  associateInboxItem(id: string, projectId: string | null): Observable<InboxItem> {
    const request: ProjectAssociationRequest = { projectId };
    return this.http.put<InboxItem>(`${this.baseUrl}/inbox/${id}/project`, request);
  }

  promoteInboxItem(id: string): Observable<WorkItem> {
    return this.http.post<WorkItem>(`${this.baseUrl}/inbox/${id}/promote`, {});
  }

  createWork(title: string, description: string | null, projectId: string | null): Observable<WorkItem> {
    const request: WorkCreateRequest = { title, description, projectId };
    return this.http.post<WorkItem>(`${this.baseUrl}/work`, request);
  }

  listWork(): Observable<WorkItem[]> {
    return this.http.get<WorkItem[]>(`${this.baseUrl}/work`);
  }

  associateWorkItem(id: string, projectId: string | null): Observable<WorkItem> {
    const request: ProjectAssociationRequest = { projectId };
    return this.http.put<WorkItem>(`${this.baseUrl}/work/${id}/project`, request);
  }

  startWorkItem(id: string): Observable<WorkItem> {
    return this.http.post<WorkItem>(`${this.baseUrl}/work/${id}/start`, {});
  }

  completeWorkItem(id: string): Observable<WorkItem> {
    return this.http.post<WorkItem>(`${this.baseUrl}/work/${id}/complete`, {});
  }

  refreshAttention(): Observable<AttentionRefreshResponse> {
    return this.http.post<AttentionRefreshResponse>(`${this.baseUrl}/attention/refresh`, {});
  }

  acknowledgeAttention(id: string): Observable<AttentionItem> {
    return this.http.post<AttentionItem>(`${this.baseUrl}/attention/${id}/acknowledge`, {});
  }

  dismissAttention(id: string): Observable<AttentionItem> {
    return this.http.post<AttentionItem>(`${this.baseUrl}/attention/${id}/dismiss`, {});
  }
}
