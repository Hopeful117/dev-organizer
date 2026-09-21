import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { OrganizerApiClient } from './organizer.api';

describe('OrganizerApiClient', () => {
  let api: OrganizerApiClient;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), OrganizerApiClient],
    });
    api = TestBed.inject(OrganizerApiClient);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('captures through the Organizer API', () => {
    api.capture('Review API', null).subscribe();

    const request = http.expectOne('/api/v1/inbox');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ content: 'Review API', projectId: null });
    request.flush({ id: 'inbox-1', content: 'Review API', projectId: null, status: 'CAPTURED', createdAt: '2026-09-21T20:00:00Z' });
  });

  it('starts work through the explicit lifecycle endpoint', () => {
    api.startWorkItem('work-1').subscribe();

    const request = http.expectOne('/api/v1/work/work-1/start');
    expect(request.request.method).toBe('POST');
    request.flush({ id: 'work-1', title: 'Work', description: null, projectId: null, status: 'IN_PROGRESS', createdAt: '', updatedAt: '' });
  });
});
