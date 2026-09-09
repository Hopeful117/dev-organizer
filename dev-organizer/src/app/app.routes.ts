import { Routes } from '@angular/router';
import {Inbox} from './inbox/inbox';
import {WorkspaceItemComponent} from './workspace-item/workspace-item';
import {WorkspaceItemDetails} from './workspace-item-details/workspace-item-details';

export const routes: Routes = [

  {
    path: 'items/:id',
    component: WorkspaceItemDetails,
  },

  {
    path: '',
    component: Inbox
  }
];
