import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { OrganizerStateService } from './organizer-state.service';

describe('OrganizerStateService', () => {
  let state: OrganizerStateService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    state = TestBed.inject(OrganizerStateService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('loads the server state into signals', async () => {
    const load = state.loadAll();

    http.expectOne('/api/v1/projects').flush([
      { id: 'project-1', name: 'Organizer', status: 'ACTIVE', createdAt: '', updatedAt: '' },
    ]);
    http.expectOne('/api/v1/inbox').flush([
      { id: 'inbox-1', content: 'Capture', projectId: null, status: 'CAPTURED', createdAt: '' },
    ]);
    http.expectOne('/api/v1/work').flush([
      { id: 'work-1', title: 'Work', description: null, projectId: null, status: 'TODO', createdAt: '', updatedAt: '' },
    ]);

    await load;

    expect(state.projects()).toHaveLength(1);
    expect(state.inboxItems()).toHaveLength(1);
    expect(state.activeWork()).toHaveLength(1);
  });

  it('removes an Inbox item and adds promoted Work from the server response', async () => {
    state.inboxItems.set([
      { id: 'inbox-1', content: 'Promote me', projectId: null, status: 'CAPTURED', createdAt: '' },
    ]);

    const promote = state.promoteInboxItem('inbox-1');
    const request = http.expectOne('/api/v1/inbox/inbox-1/promote');
    request.flush({ id: 'work-1', title: 'Promote me', description: null, projectId: null, status: 'TODO', createdAt: '', updatedAt: '' });

    expect(await promote).toBe(true);
    expect(state.inboxItems()).toHaveLength(0);
    expect(state.workItems()[0].id).toBe('work-1');
  });

  it('updates Work lifecycle from server responses', async () => {
    state.workItems.set([
      { id: 'work-1', title: 'Work', description: null, projectId: null, status: 'TODO', createdAt: '', updatedAt: '' },
    ]);

    const start = state.startWorkItem('work-1');
    http.expectOne('/api/v1/work/work-1/start').flush({
      id: 'work-1', title: 'Work', description: null, projectId: null, status: 'IN_PROGRESS', createdAt: '', updatedAt: '',
    });
    expect(await start).toBe(true);
    expect(state.workItems()[0].status).toBe('IN_PROGRESS');

    const complete = state.completeWorkItem('work-1');
    http.expectOne('/api/v1/work/work-1/complete').flush({
      id: 'work-1', title: 'Work', description: null, projectId: null, status: 'DONE', createdAt: '', updatedAt: '',
    });
    expect(await complete).toBe(true);
    expect(state.workItems()[0].status).toBe('DONE');
  });
});
