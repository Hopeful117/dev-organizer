export interface WorkspaceItem {
  id: number;
  title: string;
  description: string;
  type: ItemType;
  status: ItemStatus;
  createdAt: Date;
}

export type ItemType= 'TASK' | 'NOTE' | 'IDEA';
export type ItemStatus ='INBOX' | 'TODO' | 'IN_PROGRESS' |'DONE';

