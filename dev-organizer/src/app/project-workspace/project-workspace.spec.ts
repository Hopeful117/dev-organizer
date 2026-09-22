import { ComponentFixture, TestBed } from '@angular/core/testing';
import { signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProjectWorkspace } from './project-workspace';
import { InboxItem, Project, WorkItem } from '../organizer/organizer.models';
import { OrganizerStateService } from '../organizer/organizer-state.service';
import { WorkspaceNavigationService } from '../workspace/workspace-navigation.service';

describe('ProjectWorkspace', () => {
  let fixture: ComponentFixture<ProjectWorkspace>;
  const project: Project = {
    id: 'project-1', name: 'Launch Organizer 1.0', status: 'ACTIVE',
    devlogProject: { id: 'devlog-1', slug: 'devlog-ai' }, createdAt: '', updatedAt: '',
  };
  const work: WorkItem[] = [
    { id: 'work-1', title: 'Ship workspace', description: null, projectId: 'project-1', status: 'IN_PROGRESS', createdAt: '', updatedAt: '' },
    { id: 'work-2', title: 'Unrelated work', description: null, projectId: 'project-2', status: 'TODO', createdAt: '', updatedAt: '' },
  ];
  const inbox: InboxItem[] = [
    { id: 'inbox-1', content: 'Review launch notes', projectId: 'project-1', status: 'CAPTURED', createdAt: '' },
    { id: 'inbox-2', content: 'Other project note', projectId: 'project-2', status: 'CAPTURED', createdAt: '' },
  ];
  const state = {
    projects: signal<Project[]>([project]),
    inboxItems: signal<InboxItem[]>(inbox),
    workItems: signal<WorkItem[]>(work),
    attentions: signal([{ id: 'attention-1', projectId: 'project-1', source: 'DEVLOG', reason: 'Freshness needs review', guidance: 'REFRESH_RECOMMENDED', observedAt: '', state: 'OPEN', sourceReference: 'devlog://projects/devlog-ai/freshness', createdAt: '', updatedAt: '' }]),
    devlogProjects: signal([{ id: 'devlog-1', slug: 'devlog-ai', name: 'devlog-ai', status: 'ACTIVE' }]),
    isLoading: signal(false),
    actionInProgress: signal<string | null>(null),
    error: signal<string | null>(null),
    attentionError: signal<string | null>(null),
    loadWorkspace: vi.fn(() => Promise.resolve()),
    capture: vi.fn(() => Promise.resolve(true)),
    promoteInboxItem: vi.fn((_id: string) => Promise.resolve(true)),
    dismissInboxItem: vi.fn((_id: string) => Promise.resolve(true)),
    startWorkItem: vi.fn((_id: string) => Promise.resolve(true)),
    completeWorkItem: vi.fn((_id: string) => Promise.resolve(true)),
    linkDevlogProject: vi.fn(() => Promise.resolve(true)),
    unlinkDevlogProject: vi.fn(() => Promise.resolve(true)),
    acknowledgeAttention: vi.fn(() => Promise.resolve(true)),
    dismissAttention: vi.fn(() => Promise.resolve(true)),
    setError: vi.fn(),
  };
  const workspaceNavigation = {
    navigateToProjectContext: vi.fn(),
  };

  beforeEach(async () => {
    state.projects.set([project]);
    state.inboxItems.set(inbox);
    state.workItems.set(work);
    state.attentions.set([{ id: 'attention-1', projectId: 'project-1', source: 'DEVLOG', reason: 'Freshness needs review', guidance: 'REFRESH_RECOMMENDED', observedAt: '', state: 'OPEN', sourceReference: 'devlog://projects/devlog-ai/freshness', createdAt: '', updatedAt: '' }]);
    await TestBed.configureTestingModule({
      imports: [ProjectWorkspace],
      providers: [
        { provide: OrganizerStateService, useValue: state },
        { provide: WorkspaceNavigationService, useValue: workspaceNavigation },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => 'project-1' } } } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ProjectWorkspace);
    fixture.detectChanges();
  });

  it('loads the direct route and identifies the selected project', () => {
    expect(state.loadWorkspace).toHaveBeenCalled();
    expect(fixture.nativeElement.textContent).toContain('Launch Organizer 1.0');
    expect(fixture.nativeElement.textContent).toContain('Connected to devlog-ai');
    expect(fixture.nativeElement.textContent).toContain('Open in DevLog');
  });

  it('opens the linked project using its persisted DevLog slug', () => {
    fixture.componentInstance.openDevlogProject();

    expect(workspaceNavigation.navigateToProjectContext).toHaveBeenCalledWith('devlog-ai');
  });

  it('does not render contextual navigation for an unlinked project', () => {
    state.projects.set([{ ...project, devlogProject: null }]);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).not.toContain('Open in DevLog');
  });

  it('filters Work, Inbox, and Attention to the selected project', () => {
    const text = fixture.nativeElement.textContent;
    expect(text).toContain('Ship workspace');
    expect(text).not.toContain('Unrelated work');
    expect(text).toContain('Review launch notes');
    expect(text).not.toContain('Other project note');
    expect(text).toContain('Freshness needs review');
  });

  it('delegates project capture and existing lifecycle actions', async () => {
    const component = fixture.componentInstance;
    component.captureContent.set('Project note');
    await component.capture();
    await state.startWorkItem('work-2');
    await state.promoteInboxItem('inbox-1');

    expect(state.capture).toHaveBeenCalledWith('Project note', 'project-1');
    expect(state.startWorkItem).toHaveBeenCalledWith('work-2');
    expect(state.promoteInboxItem).toHaveBeenCalledWith('inbox-1');
  });

  it('renders a coherent not-found state for an unknown route project', async () => {
    const unknownRoute = { snapshot: { paramMap: { get: () => 'missing-project' } } };
    TestBed.resetTestingModule();
    await TestBed.configureTestingModule({
      imports: [ProjectWorkspace],
      providers: [
        { provide: OrganizerStateService, useValue: state },
        { provide: WorkspaceNavigationService, useValue: workspaceNavigation },
        { provide: ActivatedRoute, useValue: unknownRoute },
      ],
    }).compileComponents();
    const unknownFixture = TestBed.createComponent(ProjectWorkspace);
    unknownFixture.detectChanges();

    expect(unknownFixture.nativeElement.textContent).toContain('PROJECT NOT FOUND');
    expect(unknownFixture.nativeElement.textContent).toContain('Return to Organizer');
  });
});
