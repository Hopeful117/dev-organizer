import {Component, computed, inject, Signal} from '@angular/core';
import {InboxService} from '../inbox/inbox.service';
import {ActivatedRoute} from '@angular/router';
import {map, Observable} from 'rxjs';
import {WorkspaceItem} from '../workspace-item/workspace-item.models';
import {AsyncPipe, DatePipe} from '@angular/common';
import { toSignal } from '@angular/core/rxjs-interop';
@Component({
  imports: [

    DatePipe
  ],
  selector: 'app-workspace-item-details',
  styleUrl: './workspace-item-details.css',
  templateUrl: './workspace-item-details.html',
})
export class WorkspaceItemDetails {

  inboxService: InboxService=inject(InboxService);
  activatedRoute: ActivatedRoute=inject(ActivatedRoute);
  routeId = toSignal(
    this.activatedRoute.paramMap.pipe(
      map(params => {
        const id = params.get('id');

        if (id === '' || id === null) {
          return null;
        }

        const numericId = Number(id);

        if (
          Number.isNaN(numericId) ||
          !Number.isInteger(numericId) ||
          numericId <= 0
        ) {
          return null;
        }

        return numericId;
      })
    )
  );
  itemState = computed<ItemState>(() => {
    const id = this.routeId();

    if (id === null || id === undefined) {
      return { status: 'invalid-id' };
    }

    const item = this.inboxService.itemState()
      .find(item => item.id === id);

    if (!item) {
      return { status: 'not-found' };
    }

    return {
      status: 'found',
      item
    };
  });


  onAccept(id:number){

    this.inboxService.accept(id);


  }
  onDeleteItem(id:number):void{
    this.inboxService.deleteItem(id)


  }

  onStart(id:number):void{

    this.inboxService.start(id)

  }
  onDone(id:number):void{

    this.inboxService.done(id)
  }

}
export type ItemState = {status:'invalid-id'} | {status:'not-found'} | {status:'found',item:WorkspaceItem};

