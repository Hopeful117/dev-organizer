import { WorkspaceNavigationService } from './workspace-navigation.service';

describe('WorkspaceNavigationService', () => {
  it('delegates canonical references to the configured Workspace entry route', () => {
    const open = vi.spyOn(window, 'open').mockImplementation(() => null);

    new WorkspaceNavigationService().navigateToResource('devlog://projects/devlog-ai/freshness');

    expect(open).toHaveBeenCalledWith(
      'http://127.0.0.1:18083/navigation?resource=devlog%3A%2F%2Fprojects%2Fdevlog-ai%2Ffreshness',
      '_blank',
      'noopener,noreferrer',
    );
    open.mockRestore();
  });

  it('opens the linked DevLog project context using its actual slug', () => {
    const open = vi.spyOn(window, 'open').mockImplementation(() => null);

    new WorkspaceNavigationService().navigateToProjectContext('launch-organizer');

    expect(open).toHaveBeenCalledWith(
      'http://127.0.0.1:18083/navigation?resource=devlog%3A%2F%2Fprojects%2Flaunch-organizer%2Ffreshness',
      '_blank',
      'noopener,noreferrer',
    );
    open.mockRestore();
  });
});
