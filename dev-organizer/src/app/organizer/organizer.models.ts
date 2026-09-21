export type ProjectStatus = 'ACTIVE' | 'ARCHIVED';
export type InboxItemStatus = 'CAPTURED' | 'DISMISSED' | 'PROMOTED';
export type WorkItemStatus = 'TODO' | 'IN_PROGRESS' | 'DONE';

export interface Project {
  id: string;
  name: string;
  status: ProjectStatus;
  devlogProject: DevlogProjectReference | null;
  createdAt: string;
  updatedAt: string;
}

export interface DevlogProjectReference {
  id: string;
  slug: string;
}

export interface DevlogProjectOption extends DevlogProjectReference {
  name: string;
  status: string;
}

export type AttentionState = 'OPEN' | 'ACKNOWLEDGED';

export interface AttentionItem {
  id: string;
  projectId: string;
  source: 'DEVLOG';
  reason: string;
  guidance: string;
  observedAt: string;
  state: AttentionState;
  sourceReference: string;
  createdAt: string;
  updatedAt: string;
}

export interface AttentionRefreshResponse {
  items: AttentionItem[];
  errors: Array<{ projectId: string; message: string }>;
}

export interface InboxItem {
  id: string;
  content: string;
  projectId: string | null;
  status: InboxItemStatus;
  createdAt: string;
}

export interface WorkItem {
  id: string;
  title: string;
  description: string | null;
  projectId: string | null;
  status: WorkItemStatus;
  createdAt: string;
  updatedAt: string;
}

export interface ProjectSummary {
  project: Project;
  activeWorkCount: number;
  inboxCount: number;
}
