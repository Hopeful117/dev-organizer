package com.developeros.organizer.api;

import com.developeros.organizer.application.ProjectApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {
    private final ProjectApplicationService service;

    public ProjectController(ProjectApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ProjectResponse.from(service.create(request.name())));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> list() {
        return ResponseEntity.ok(service.list().stream().map(ProjectResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ProjectResponse.from(service.get(id)));
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<ProjectResponse> archive(@PathVariable UUID id) {
        return ResponseEntity.ok(ProjectResponse.from(service.archive(id)));
    }
}
