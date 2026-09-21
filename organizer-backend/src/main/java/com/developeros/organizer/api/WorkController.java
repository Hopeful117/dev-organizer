package com.developeros.organizer.api;

import com.developeros.organizer.application.WorkApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/work")
public class WorkController {
    private final WorkApplicationService service;

    public WorkController(WorkApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<WorkItemResponse> create(@Valid @RequestBody WorkCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WorkItemResponse.from(service.create(request.title(), request.description(), request.projectId())));
    }

    @GetMapping
    public ResponseEntity<List<WorkItemResponse>> list() {
        return ResponseEntity.ok(service.list().stream().map(WorkItemResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkItemResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(WorkItemResponse.from(service.get(id)));
    }

    @PutMapping("/{id}/project")
    public ResponseEntity<WorkItemResponse> associateProject(@PathVariable UUID id,
                                                              @RequestBody ProjectAssociationRequest request) {
        return ResponseEntity.ok(WorkItemResponse.from(service.associateProject(id, request.projectId())));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<WorkItemResponse> start(@PathVariable UUID id) {
        return ResponseEntity.ok(WorkItemResponse.from(service.start(id)));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<WorkItemResponse> complete(@PathVariable UUID id) {
        return ResponseEntity.ok(WorkItemResponse.from(service.complete(id)));
    }
}
