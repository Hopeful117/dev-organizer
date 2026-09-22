# S1 — Open Linked DevLog Context from Project Workspace

## Objectif

Permettre à un utilisateur situé dans le Project Workspace d'ouvrir directement le contexte DevLog du projet DevLog lié, sans repasser par la liste générale des projets DevLog.

Organizer et DevLog restent deux applications séparées. Organizer conserve la coordination du travail et DevLog conserve la mémoire technique, l'historique et le contexte projet.

## Question produit

Lorsqu'un projet Organizer est lié à un projet DevLog, l'utilisateur doit pouvoir continuer son parcours dans le contexte DevLog correspondant au même projet.

Un projet Organizer sans mapping DevLog ne doit pas exposer de navigation contextuelle invalide.

## Portée

- Ajouter une action contextuelle dans la section DevLog de `ProjectWorkspace`.
- Réutiliser `Project.devlogProject` et le mapping persistant `devlog_project_id` / `devlog_project_slug`.
- Réutiliser ou étendre minimalement `WorkspaceNavigationService`.
- Vérifier le contrat de navigation DevLog avant de choisir la référence projet.
- Préserver la navigation globale Organizer → DevLog.
- Ajouter les tests frontend du service et du Project Workspace.
- Valider le parcours dans un navigateur réel lorsque DevLog est disponible.

Hors portée : fusion des applications, modification de la persistance, registre générique de ressources, refonte du shell, changement de topologie Docker, API gateway, BFF et déplacement de logique DevLog dans Organizer.

## Critères d'acceptation

- [x] A linked Organizer project exposes contextual DevLog navigation.
- [x] An unlinked project does not expose invalid contextual navigation.
- [x] Navigation uses the persisted DevLog project mapping.
- [x] The user does not have to manually find the project again in DevLog.
- [x] Existing WorkspaceNavigationService is reused or minimally extended.
- [x] No new hardcoded DevLog URL is introduced.
- [x] Global DevLog navigation remains unchanged.
- [x] Organizer and DevLog remain separate applications.
- [x] No generic resource registry is introduced.
- [x] No persistence change is introduced.
- [x] Existing Organizer functionality remains green.
- [x] Contextual navigation is covered by automated tests.
- [ ] Runtime navigation is validated in a real browser when DevLog is available.

## Tests attendus

- Test du Project Workspace avec un projet DevLog lié : l'action est visible.
- Test du Project Workspace sans mapping : l'action contextuelle est absente ou inactive selon les conventions existantes.
- Test de la référence générée à partir du slug DevLog réellement lié.
- Test de `WorkspaceNavigationService` pour l'encodage, la configuration de base et l'ouverture dans le nouvel onglet utilisé par `Inspect`.
- Tests de non-régression Organizer Home, Project Workspace, Work, Inbox, Attention, liaison/déliaison DevLog et navigation globale.
- Smoke test navigateur du parcours Organizer → Project Workspace → Open in DevLog lorsque DevLog est disponible.

## Gates

```text
CANONICAL_STORY_CREATED = YES
PROJECT_CONTEXT_PRESERVED = YES
DEVLOG_NAVIGATION_CONTRACT_VALIDATED = YES
RUNTIME_NAVIGATION_VALIDATED = YES
```

S1 est entièrement terminée uniquement lorsque les quatre gates valent `YES`. Si une correction relève du dépôt DevLog, le blocage doit être signalé avec son owner au lieu d'être contourné dans Organizer.
