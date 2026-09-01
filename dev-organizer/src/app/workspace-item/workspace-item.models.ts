export interface WorkspaceItem {
  id: number;
  title: string;
  description: string;
  type: ItemType;
  status: ItemStatus;
  createdAt: Date;
}
export interface CreateWorkspaceItem {
  title: string;
  description: string;
  type: ItemType;
}

export type ItemType= 'TASK' | 'NOTE' | 'IDEA';
export type ItemStatus ='INBOX' | 'TODO' | 'IN_PROGRESS' |'DONE';

