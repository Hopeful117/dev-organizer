import { Routes } from '@angular/router';
import { OrganizerHome } from './organizer/organizer';
import { ProjectWorkspace } from './project-workspace/project-workspace';

export const routes: Routes = [
  {
    path: '',
    component: OrganizerHome,
    pathMatch: 'full'
  },
  {
    path: 'projects/:projectId',
    component: ProjectWorkspace
  }
];
