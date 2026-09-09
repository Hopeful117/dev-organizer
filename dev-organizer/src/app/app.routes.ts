import { Routes } from '@angular/router';
import {Note} from './workspace-item/note/note';

export const routes: Routes = [

  {
    path: 'notes/:id',
    component: Note
  }
];
