import { Component } from '@angular/core';
import {WorkspaceItem} from '../workspace-item/workspace-item.models';
import {WorkspaceItemComponent} from '../workspace-item/workspace-item';
import {FormsModule} from '@angular/forms';


@Component({
  imports: [
    WorkspaceItemComponent,FormsModule
  ],
  selector: 'app-inbox',
  styleUrl: './inbox.css',
  templateUrl: './inbox.html',
})
export class Inbox {
  title: string = 'Inbox';
  itemList:Array<WorkspaceItem>=[
  {
    id : 1,
    title:"My Inbox",
    description:"Building a workspace Inbox",
    type:"IDEA",
    status:"IN_PROGRESS",
    createdAt: new Date()
  },
  {
    id:2,
    title:"Learning Angular & Typescript",
    description:"Improve my understanding of Angular & Typescript",
    type:"TASK",
    status:"IN_PROGRESS",
    createdAt: new Date()

  }
  ]
  onMarkDone(id:number){
    const item = this.itemList.find(item => item.id === id);
    if (item != null) {
      item.status="DONE";

    }

  }
  onDeleteItem(id:number){
    this.itemList=this.itemList.filter(item => item.id !== id);
  }
  newItemTitle:string='';
 
  addItem (){
    const listId : Array<number> = this.itemList.map(item => item.id);
    const nextNumber:number = listId.length > 0 ? Math.max(...listId) +1  : 1;
    if (this.newItemTitle.trim() === '') {
      alert("title is empty");
      return;

    }
   const newItem : WorkspaceItem= {
     id:nextNumber,
     title:this.newItemTitle,
     description:"",
     type:"IDEA",
     status:"IN_PROGRESS",
     createdAt: new Date()
   }

   this.itemList.push(newItem);
   this.newItemTitle = '';


  }
}
