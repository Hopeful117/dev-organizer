import { ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkspaceItemComponent } from './workspace-item';

describe('WorkspaceItem', () => {
  let component: WorkspaceItemComponent;
  let fixture: ComponentFixture<WorkspaceItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WorkspaceItemComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(WorkspaceItemComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
