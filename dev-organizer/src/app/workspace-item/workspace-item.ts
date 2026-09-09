import {Component, inject, input, output} from '@angular/core';
import {ItemStatus, WorkspaceItem} from './workspace-item.models';
import {AsyncPipe, DatePipe} from '@angular/common';
import {InboxService} from '../inbox/inbox.service';
import {ActivatedRoute} from '@angular/router';
import {map, Observable} from 'rxjs';


@Component({
  imports: [DatePipe],
  selector: 'app-workspace-item',
  styleUrl: './workspace-item.css',
  templateUrl: './workspace-item.html',
})
export class WorkspaceItemComponent {
  item = input.required<WorkspaceItem>();
  accept = output<number>();
  deleteItem = output<number>();
  start = output<number>();
  done = output<number>();


}



