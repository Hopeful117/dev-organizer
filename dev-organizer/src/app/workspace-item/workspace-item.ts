import {Component,input,output} from '@angular/core';
import {ItemStatus, WorkspaceItem} from './workspace-item.models';
import {DatePipe} from '@angular/common';


@Component({
  imports: [DatePipe],
  selector: 'app-workspace-item',
  styleUrl: './workspace-item.css',
  templateUrl: './workspace-item.html',
})
export class WorkspaceItemComponent {
  item=input.required<WorkspaceItem>();
  accept=output<number>();
  deleteItem=output<number>();
  start=output<number>();
  done=output<number>();



}
