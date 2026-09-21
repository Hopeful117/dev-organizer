package com.developeros.organizer.api;

import com.developeros.organizer.application.DevlogProjectCatalogPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/integrations/devlog")
public class DevlogIntegrationController {
    private final DevlogProjectCatalogPort catalog;

    public DevlogIntegrationController(DevlogProjectCatalogPort catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/projects")
    public ResponseEntity<List<DevlogProjectOptionResponse>> listProjects() {
        try {
            return ResponseEntity.ok(catalog.listProjects().stream()
                    .map(DevlogProjectOptionResponse::from).toList());
        } catch (RuntimeException exception) {
            return ResponseEntity.ok(List.of());
        }
    }
}
