import {Component, computed, effect, signal} from '@angular/core';
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
  newItemType:ItemType='IDEA';
  itemList = signal<Array<WorkspaceItem>>([]);
  selectType= signal<ItemType | 'ALL'>('ALL')
  filteredItems=computed(()=>{
    if (this.selectType() ==='ALL'){
      return (this.itemList())
    }
    return this.itemList().filter(item => item.type === this.selectType())
  })

  constructor() {
    this.mockData();
    this.loadItems();

    effect(()=>{

     this.saveItems()

    })
  }


  putSelectedType(value:ItemType | 'ALL'){
    this.selectType.set(value);

  }
  mockData():void{
    this.itemList.set([

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
      )

  }
saveItems(){


   let jsonList=JSON.stringify(this.itemList());
   localStorage.setItem('workspace-items',jsonList);
}
loadItems():void{
    let jsonList : string | null = localStorage.getItem('workspace-items');
    if (jsonList != null){
      let items : WorkspaceItem[] = JSON.parse(jsonList);
      items = items.map((item: WorkspaceItem) => {
        return {
          ...item,
          createdAt: new Date(item.createdAt)
        };
      });

    this.itemList.set(items);


    }



}



  onAccept(id:number){

  this.itemList.update(items=>items.map(
    item=> {
      if (item.id === id && item.status ==="INBOX"){
        return {...item,status:"TODO"};
      }
      return item;
    })
  )


  }
  onDeleteItem(id:number):void{


   this.itemList.update(itemList => itemList.filter(item => item.id !== id));
  }

  onStart(id:number):void{

    this.itemList.update(items => items.map(
      item => {
        if (item.status === 'TODO' && item.type ==='TASK' && item.id === id){
          return {...item, status:'IN_PROGRESS'}
        }
        return item;
      }
    ))

  }
  onDone(id:number):void{

    this.itemList.update(itemList => itemList.map(
      item => {
        if (item.id === id && item.status ==='IN_PROGRESS' && item.type ==='TASK'){
          return {...item, status:'DONE'}

        }
        return item;
      }
    ))
  }


  addItem ():void{
    const listId : Array<number> = this.itemList().map(item => item.id);
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

   this.itemList.update(items=>[...items,newItem])
    this.cleanUp();


  }
  cleanUp():void{
    this.newItemTitle = '';
    this.newItemDescription = '';
    this.newItemType='IDEA';



  }


}
