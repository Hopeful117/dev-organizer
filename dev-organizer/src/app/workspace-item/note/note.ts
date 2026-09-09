import {Component, inject} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {map, Observable} from 'rxjs';
import {AsyncPipe} from '@angular/common';
import {InboxService} from '../../inbox/inbox.service';
import {WorkspaceItem} from '../workspace-item.models';



@Component({
  imports: [
    AsyncPipe
  ],
  selector: 'app-note',
  styleUrl: './note.css',
  templateUrl: './note.html',
})
export class Note {
  inboxService: InboxService=inject(InboxService);
  activatedRoute: ActivatedRoute=inject(ActivatedRoute);

  noteId$:Observable<NoteState>=this.activatedRoute.paramMap.pipe(

    map(params => {
      const id = params.get('id');
      if ( id === '' || id === null) {
        return null;
      }
      const numericId = Number(id);
      if (Number.isNaN(numericId) || !Number.isInteger(numericId) || numericId <= 0) {
        return null;
      }
      else{
        return numericId;
      }
    }),
    map((id): NoteState => {
      if (id === null) {
        return { status: 'invalid-id' };
      }

      const note = this.inboxService.getItem(id);

      if (note === undefined) {
        return { status: 'not-found' };
      }

      return {
        status: 'found',
        note
      };
    })


    )

}
export type NoteState= {status:'invalid-id'} | {status:'not-found'} | {status:'found',note:WorkspaceItem}

