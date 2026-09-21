ALTER TABLE projects
    ADD COLUMN devlog_project_id UUID,
    ADD COLUMN devlog_project_slug VARCHAR(100);

CREATE TABLE attention_items (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    source VARCHAR(30) NOT NULL,
    source_key VARCHAR(1000) NOT NULL,
    reason VARCHAR(500) NOT NULL,
    guidance VARCHAR(80) NOT NULL,
    observed_at TIMESTAMPTZ NOT NULL,
    state VARCHAR(20) NOT NULL,
    source_reference VARCHAR(500) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT attention_project_fk FOREIGN KEY (project_id) REFERENCES projects (id),
    CONSTRAINT attention_source_check CHECK (source IN ('DEVLOG')),
    CONSTRAINT attention_state_check CHECK (state IN ('OPEN', 'ACKNOWLEDGED', 'DISMISSED', 'RESOLVED')),
    CONSTRAINT attention_source_identity_uq UNIQUE (project_id, source, source_key)
);

CREATE INDEX attention_items_current_idx ON attention_items (state, observed_at DESC);
CREATE INDEX attention_items_project_source_idx ON attention_items (project_id, source);
