CREATE TABLE projects (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT projects_status_check CHECK (status IN ('ACTIVE', 'ARCHIVED'))
);

CREATE TABLE inbox_items (
    id UUID PRIMARY KEY,
    content TEXT NOT NULL,
    project_id UUID NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT inbox_items_status_check CHECK (status IN ('CAPTURED', 'DISMISSED', 'PROMOTED')),
    CONSTRAINT inbox_items_project_fk FOREIGN KEY (project_id) REFERENCES projects (id)
);

CREATE TABLE work_items (
    id UUID PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NULL,
    project_id UUID NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT work_items_status_check CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE')),
    CONSTRAINT work_items_project_fk FOREIGN KEY (project_id) REFERENCES projects (id)
);

CREATE INDEX inbox_items_status_idx ON inbox_items (status);
CREATE INDEX work_items_status_idx ON work_items (status);
