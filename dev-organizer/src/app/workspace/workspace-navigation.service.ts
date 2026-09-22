import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class WorkspaceNavigationService {
  navigateToResource(canonicalReference: string): void {
    const destination = new URL('/navigation', environment.workspaceBaseUrl);
    destination.searchParams.set('resource', canonicalReference);
    window.open(destination.toString(), '_blank', 'noopener,noreferrer');
  }

  navigateToProjectContext(projectSlug: string): void {
    this.navigateToResource(`devlog://projects/${projectSlug}/freshness`);
  }
}
