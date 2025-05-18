CREATE TABLE completed_task (
    id SERIAL PRIMARY KEY,
    title VARCHAR(50) NOT NULL,
    description VARCHAR(500) NOT NULL,
    completion_status VARCHAR(20) NOT NULL,
    priority VARCHAR(10) NOT NULL,
    due_date DATE NOT NULL,
    create_at TIMESTAMP WITH TIME ZONE NOT NULL,
    update_at TIMESTAMP WITH TIME ZONE NOT NULL,
    project_id BIGINT NOT NULL,
    assigned_id BIGINT NOT NULL,
    creator_id BIGINT NOT NULL,
    FOREIGN KEY (project_id) REFERENCES project (id) ON DELETE CASCADE
);