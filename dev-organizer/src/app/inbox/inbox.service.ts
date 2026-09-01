import {effect, Service, signal} from '@angular/core';
import {CreateWorkspaceItem, WorkspaceItem} from '../workspace-item/workspace-item.models';

@Service()
export class InboxService {

  private readonly itemState= signal<WorkspaceItem[]>([])
  public readonly items=this.itemState.asReadonly()
  constructor(){
    this.mockData()
    this.loadItems();
    effect(()=>{

      this.saveItems();

    })
  }

  public addItem(item:CreateWorkspaceItem){
    const listId : Array<number> = this.itemState().map(item => item.id);
    const nextNumber:number = listId.length > 0 ? Math.max(...listId) +1  : 1;
   const newItem : WorkspaceItem = {
     id: nextNumber,
     title:item.title,
     description:item.description,
     type:item.type,
     status:"INBOX",
     createdAt: new Date(),
   }

    this.itemState.update(items => [...items,newItem]);
  }

  public deleteItem(id:number){
    this.itemState.update(itemList => itemList.filter(item => item.id !== id));
  }

  public accept(id:number){
    this.itemState.update(items=>items.map(
      item=> {
        if (item.id === id && item.status ==="INBOX"){
          return {...item,status:"TODO"};
        }
        return item;
      })
    )
  }

  public start(id:number):void{
    this.itemState.update(items => items.map(
      item => {
        if (item.status === 'TODO' && item.type ==='TASK' && item.id === id){
          return {...item, status:'IN_PROGRESS'}
        }
        return item;
      }
    ))
  }

  public done(id:number):void{
    this.itemState.update(itemList => itemList.map(
      item => {
        if (item.id === id && item.status ==='IN_PROGRESS' && item.type ==='TASK'){
          return {...item, status:'DONE'}

        }
        return item;
      }
    ))
  }



  private loadItems():void{
    let jsonList : string | null = localStorage.getItem('workspace-items');
    if (jsonList != null){
      let items : WorkspaceItem[] = JSON.parse(jsonList);
      items = items.map((item: WorkspaceItem) => {
        return {
          ...item,
          createdAt: new Date(item.createdAt)
        };
      });

      this.itemState.set(items);


    }



  }
 private saveItems(){


    let jsonList=JSON.stringify(this.itemState());
    localStorage.setItem('workspace-items',jsonList);
  }

 private mockData():void{
    this.itemState.set([

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


}
