package com.developeros.organizer;

import com.developeros.organizer.application.InboxApplicationService;
import com.developeros.organizer.application.ProjectApplicationService;
import com.developeros.organizer.domain.InboxItemStatus;
import com.developeros.organizer.domain.WorkItemStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class OrganizerPersistenceIntegrationTest {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private ProjectApplicationService projectService;

    @Autowired
    private InboxApplicationService inboxService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void flywaySchemaAndPromotionArePersistedTransactionally() {
        var project = projectService.create("Persistence project");
        var inboxItem = inboxService.capture("Persist this work", project.id());

        var workItem = inboxService.promoteToWork(inboxItem.id());

        assertThat(workItem.status()).isEqualTo(WorkItemStatus.TODO);
        assertThat(workItem.projectId()).isEqualTo(project.id());
        assertThat(inboxService.get(inboxItem.id()).status()).isEqualTo(InboxItemStatus.PROMOTED);
    }

    @Test
    void exposesCaptureAndPromotionThroughRest() throws Exception {
        var project = projectService.create("REST project");

        var captureResponse = mockMvc.perform(post("/api/v1/inbox")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Use REST API\",\"projectId\":\"" + project.id() + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CAPTURED"))
                .andReturn();

        String inboxId = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(captureResponse.getResponse().getContentAsString())
                .get("id")
                .asText();

        mockMvc.perform(post("/api/v1/inbox/" + inboxId + "/promote"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.projectId").value(project.id().toString()));
    }
}
