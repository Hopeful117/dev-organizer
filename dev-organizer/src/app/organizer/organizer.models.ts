export type ProjectStatus = 'ACTIVE' | 'ARCHIVED';
export type InboxItemStatus = 'CAPTURED' | 'DISMISSED' | 'PROMOTED';
export type WorkItemStatus = 'TODO' | 'IN_PROGRESS' | 'DONE';

export interface Project {
  id: string;
  name: string;
  status: ProjectStatus;
  createdAt: string;
  updatedAt: string;
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
