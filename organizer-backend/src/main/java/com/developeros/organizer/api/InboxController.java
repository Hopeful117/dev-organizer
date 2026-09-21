package com.developeros.organizer.api;

import com.developeros.organizer.application.InboxApplicationService;
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
@RequestMapping("/api/v1/inbox")
public class InboxController {
    private final InboxApplicationService service;

    public InboxController(InboxApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<InboxItemResponse> capture(@Valid @RequestBody InboxCaptureRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InboxItemResponse.from(service.capture(request.content(), request.projectId())));
    }

    @GetMapping
    public ResponseEntity<List<InboxItemResponse>> listCaptured() {
        return ResponseEntity.ok(service.listCaptured().stream().map(InboxItemResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InboxItemResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(InboxItemResponse.from(service.get(id)));
    }

    @PostMapping("/{id}/dismiss")
    public ResponseEntity<InboxItemResponse> dismiss(@PathVariable UUID id) {
        return ResponseEntity.ok(InboxItemResponse.from(service.dismiss(id)));
    }

    @PutMapping("/{id}/project")
    public ResponseEntity<InboxItemResponse> associateProject(@PathVariable UUID id,
                                                               @RequestBody ProjectAssociationRequest request) {
        return ResponseEntity.ok(InboxItemResponse.from(service.associateProject(id, request.projectId())));
    }

    @PostMapping("/{id}/promote")
    public ResponseEntity<WorkItemResponse> promote(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(WorkItemResponse.from(service.promoteToWork(id)));
    }
}
