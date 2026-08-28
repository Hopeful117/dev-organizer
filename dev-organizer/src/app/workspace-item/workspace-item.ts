import {Component,input,output} from '@angular/core';
import {ItemStatus, WorkspaceItem} from './workspace-item.models';


@Component({
  imports: [],
  selector: 'app-workspace-item',
  styleUrl: './workspace-item.css',
  templateUrl: './workspace-item.html',
})
export class WorkspaceItemComponent {
  item=input.required<WorkspaceItem>();
  markDone=output<number>();
  deleteItem=output<number>();



}
