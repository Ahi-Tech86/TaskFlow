CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(50) NOT NULL,
    description VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL,
    priority VARCHAR(10) NOT NULL,
    due_date DATE NOT NULL,
    create_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    update_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    project_id BIGINT NOT NULL,
    assigned_id BIGINT NOT NULL,
    creator_id BIGINT NOT NULL,
    FOREIGN KEY (project_id) REFERENCES project (id) ON DELETE CASCADE
);