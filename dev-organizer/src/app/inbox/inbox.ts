import { Component } from '@angular/core';
import {ItemType, WorkspaceItem} from '../workspace-item/workspace-item.models';
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
  newItemTitle:string='';
  newItemDescription:string='';
  newItemType:ItemType='IDEA'
  itemList:Array<WorkspaceItem>=[
  {
    id : 1,
    title:"My Inbox",
    description:"Building a workspace Inbox",
    type:"IDEA",
    status:"INBOX",
    createdAt: new Date()
  },
  {
    id:2,
    title:"Learning Angular & Typescript",
    description:"Improve my understanding of Angular & Typescript",
    type:"TASK",
    status:"INBOX",
    createdAt: new Date()

  }
  ]
  onAccept(id:number):void{
    const item = this.itemList.find(item => item.id === id);

    if (item?.status ==='INBOX'){
      item.status="TODO"
    }


  }
  onDeleteItem(id:number):void{
    this.itemList=this.itemList.filter(item => item.id !== id);
  }

  onStart(id:number):void{
    const item = this.itemList.find(item => item.id === id);
    if (item?.status === 'TODO' &&  item?.type === 'TASK'){
      item.status="IN_PROGRESS";

    }

  }
  onDone(id:number):void{
    const item = this.itemList.find(item => item.id === id);
    if (item?.status === 'IN_PROGRESS' &&  item?.type === 'TASK'){
      item.status="DONE";
    }
  }


  addItem ():void{
    const listId : Array<number> = this.itemList.map(item => item.id);
    const nextNumber:number = listId.length > 0 ? Math.max(...listId) +1  : 1;
    if (this.newItemTitle.trim() === '') {
      alert("title is empty");
      return;

    }
   const newItem : WorkspaceItem= {
     id:nextNumber,
     title:this.newItemTitle.trim(),
     description:this.newItemDescription.trim(),
     type:this.newItemType,
     status:"INBOX",
     createdAt: new Date()
   }

   this.itemList.push(newItem);
    this.cleanUp();


  }
  cleanUp():void{
    this.newItemTitle = '';
    this.newItemDescription = '';
    this.newItemType='IDEA';



  }
}
