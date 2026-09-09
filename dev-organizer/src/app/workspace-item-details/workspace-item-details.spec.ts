import { ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkspaceItemDetails } from './workspace-item-details';

describe('WorkspaceItemDetails', () => {
  let component: WorkspaceItemDetails;
  let fixture: ComponentFixture<WorkspaceItemDetails>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WorkspaceItemDetails],
    }).compileComponents();

    fixture = TestBed.createComponent(WorkspaceItemDetails);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
