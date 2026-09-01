import {Component, computed, effect, signal,inject} from '@angular/core';
import {CreateWorkspaceItem, ItemType, WorkspaceItem} from '../workspace-item/workspace-item.models';
import {WorkspaceItemComponent} from '../workspace-item/workspace-item';
import {FormsModule} from '@angular/forms';
import {InboxService} from './inbox.service';




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
  newItemType:ItemType='IDEA';
  readonly inboxService= inject(InboxService);
  readonly itemList = this.inboxService.items
  selectType= signal<ItemType | 'ALL'>('ALL')
  filteredItems=computed(()=>{
    if (this.selectType() ==='ALL'){
      return (this.itemList())
    }
    return this.itemList().filter(item => item.type === this.selectType())
  })



  putSelectedType(value:ItemType | 'ALL'){
    this.selectType.set(value);

  }


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


  addItem ():void{
    if (this.newItemTitle.trim() === '') {
      alert("title is empty");
      return;

    }
   const newItem : CreateWorkspaceItem = {
     title:this.newItemTitle.trim(),
     description:this.newItemDescription.trim(),
     type:this.newItemType
   }

    this.inboxService.addItem(newItem)
    this.cleanUp();


  }
  cleanUp():void{
    this.newItemTitle = '';
    this.newItemDescription = '';
    this.newItemType='IDEA';



  }


}
