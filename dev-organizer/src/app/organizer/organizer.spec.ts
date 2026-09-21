import { ComponentFixture, TestBed } from '@angular/core/testing';
import { signal } from '@angular/core';
import { OrganizerHome } from './organizer';
import { OrganizerStateService } from './organizer-state.service';

describe('OrganizerHome', () => {
  let fixture: ComponentFixture<OrganizerHome>;
  const state = {
    projects: signal([]),
    activeProjects: signal([]),
    inboxItems: signal([]),
    workItems: signal([]),
    activeWork: signal([]),
    completedWork: signal([]),
    isLoading: signal(false),
    error: signal<string | null>(null),
    loadAll: vi.fn(() => Promise.resolve()),
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
  };

  beforeEach(async () => {
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
    expect(text).toContain('Active Work');
    expect(text).toContain('Inbox');
    expect(text).toContain('Projects');
  });

  it('delegates capture to the server state service and clears the input', async () => {
    const component = fixture.componentInstance;
    component.captureContent = 'Capture this';
    await component.capture();

    expect(state.capture).toHaveBeenCalledWith('Capture this', null);
    expect(component.captureContent).toBe('');
  });
});
