import { ComponentFixture, TestBed } from '@angular/core/testing';
import { signal } from '@angular/core';
import { OrganizerHome } from './organizer';
import { OrganizerStateService } from './organizer-state.service';
import { InboxItem, ProjectSummary, WorkItem } from './organizer.models';

describe('OrganizerHome', () => {
  let fixture: ComponentFixture<OrganizerHome>;
  const state = {
    projects: signal([]),
    activeProjects: signal([]),
    inboxItems: signal<InboxItem[]>([]),
    workItems: signal<WorkItem[]>([]),
    activeWork: signal<WorkItem[]>([]),
    completedWork: signal<WorkItem[]>([]),
    inProgressWork: signal<WorkItem[]>([]),
    todoWork: signal<WorkItem[]>([]),
    projectSummaries: signal<ProjectSummary[]>([]),
    attentions: signal([]),
    devlogProjects: signal([]),
    isLoading: signal(false),
    actionInProgress: signal<string | null>(null),
    error: signal<string | null>(null),
    attentionError: signal<string | null>(null),
    attentionLoading: signal(false),
    loadAll: vi.fn(() => Promise.resolve()),
    refreshAttention: vi.fn(() => Promise.resolve()),
    loadDevlogProjects: vi.fn(() => Promise.resolve()),
    capture: vi.fn(() => Promise.resolve(true)),
    createProject: vi.fn(() => Promise.resolve(true)),
    setError: vi.fn(),
    associateInboxItem: vi.fn(() => Promise.resolve(true)),
    associateWorkItem: vi.fn(() => Promise.resolve(true)),
    promoteInboxItem: vi.fn(() => Promise.resolve(true)),
    dismissInboxItem: vi.fn(() => Promise.resolve(true)),
    startWorkItem: vi.fn(() => Promise.resolve(true)),
    completeWorkItem: vi.fn(() => Promise.resolve(true)),
    archiveProject: vi.fn(() => Promise.resolve(true)),
    linkDevlogProject: vi.fn(() => Promise.resolve(true)),
    unlinkDevlogProject: vi.fn(() => Promise.resolve(true)),
    acknowledgeAttention: vi.fn(() => Promise.resolve(true)),
    dismissAttention: vi.fn(() => Promise.resolve(true)),
  };

  beforeEach(async () => {
    state.inboxItems.set([]);
    state.workItems.set([]);
    state.activeWork.set([]);
    state.completedWork.set([]);
    state.inProgressWork.set([]);
    state.todoWork.set([]);
    state.projectSummaries.set([]);
    await TestBed.configureTestingModule({
      imports: [OrganizerHome],
      providers: [{ provide: OrganizerStateService, useValue: state }],
    }).compileComponents();

    fixture = TestBed.createComponent(OrganizerHome);
    fixture.detectChanges();
  });

  it('loads the Organizer state and renders the three home areas', () => {
    expect(state.loadAll).toHaveBeenCalled();
    const text = fixture.nativeElement.textContent;
    expect(text).toContain('Continue');
    expect(text).toContain('Work');
    expect(text).toContain('Inbox');
    expect(text).toContain('Projects');
    expect(text).toContain('No work yet');
  });

  it('renders Quick Capture as a compact single-line interaction', () => {
    const element: HTMLElement = fixture.nativeElement;

    expect(element.querySelector('input[aria-label="Capture content"]')).not.toBeNull();
    expect(element.querySelector('textarea')).toBeNull();
    expect(element.querySelector('.capture-panel')).not.toBeNull();
  });

  it('presents in-progress and TODO work in their respective Home areas', () => {
    const inProgress: WorkItem = {
      id: 'work-1', title: 'Continue API work', description: null, projectId: null,
      status: 'IN_PROGRESS', createdAt: '', updatedAt: '',
    };
    const todo: WorkItem = {
      id: 'work-2', title: 'Review API contract', description: null, projectId: null,
      status: 'TODO', createdAt: '', updatedAt: '',
    };
    state.workItems.set([inProgress, todo]);
    state.activeWork.set([inProgress, todo]);
    state.inProgressWork.set([inProgress]);
    state.todoWork.set([todo]);
    fixture.detectChanges();

    const text = fixture.nativeElement.textContent;
    expect(text).toContain('Continue API work');
    expect(text).toContain('Review API contract');
    expect(text).toContain('IN PROGRESS');
    expect(text).toContain('TODO');
  });

  it('delegates capture to the server state service and clears the input', async () => {
    const component = fixture.componentInstance;
    component.captureContent = 'Capture this';
    await component.capture();

    expect(state.capture).toHaveBeenCalledWith('Capture this', null);
    expect(component.captureContent).toBe('');
  });
});
