package com.developeros.organizer.application;

import java.util.List;
import java.util.UUID;

public interface DevlogProjectCatalogPort {
    List<DevlogProjectOption> listProjects();

    record DevlogProjectOption(UUID id, String name, String slug, String status) { }
}
