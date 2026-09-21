package com.developeros.organizer.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkItemTest {
    private final Instant now = Instant.parse("2026-09-21T20:00:00Z");

    @Test
    void followsTheExplicitLifecycle() {
        WorkItem item = WorkItem.create("Ship backend", "", null, now);

        item.start(now.plusSeconds(1));
        item.complete(now.plusSeconds(2));

        assertThat(item.status()).isEqualTo(WorkItemStatus.DONE);
        assertThat(item.updatedAt()).isEqualTo(now.plusSeconds(2));
    }

    @Test
    void rejectsInvalidTransitions() {
        WorkItem item = WorkItem.create("Ship backend", null, null, now);

        assertThatThrownBy(() -> item.complete(now.plusSeconds(1)))
                .isInstanceOf(DomainException.class);

        item.start(now.plusSeconds(2));

        assertThatThrownBy(() -> item.start(now.plusSeconds(3)))
                .isInstanceOf(DomainException.class);
    }
}
