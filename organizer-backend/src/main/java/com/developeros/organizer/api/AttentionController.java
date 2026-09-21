package com.developeros.organizer.api;

import com.developeros.organizer.application.AttentionApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attention")
public class AttentionController {
    private final AttentionApplicationService service;

    public AttentionController(AttentionApplicationService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AttentionResponse>> list() {
        return ResponseEntity.ok(service.listCurrent().stream().map(AttentionResponse::from).toList());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AttentionRefreshResponse> refresh() {
        return ResponseEntity.ok(AttentionRefreshResponse.from(service.refreshAll()));
    }

    @PostMapping("/{id}/acknowledge")
    public ResponseEntity<AttentionResponse> acknowledge(@PathVariable UUID id) {
        return ResponseEntity.ok(AttentionResponse.from(service.acknowledge(id)));
    }

    @PostMapping("/{id}/dismiss")
    public ResponseEntity<AttentionResponse> dismiss(@PathVariable UUID id) {
        return ResponseEntity.ok(AttentionResponse.from(service.dismiss(id)));
    }
}
