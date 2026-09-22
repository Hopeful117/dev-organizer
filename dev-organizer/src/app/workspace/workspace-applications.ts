import { environment } from '../../environments/environment';

export const workspaceApplications = [
  { id: 'ORGANIZER', label: 'Organizer', destination: '/' },
  { id: 'DEVLOG', label: 'DevLog', destination: `${environment.workspaceBaseUrl}/projects` },
] as const;
