CREATE TABLE project (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(500) NOT NULL,
    owner_id BIGINT NOT NULL,
    start_date DATE,
    create_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE project_member (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    project_id BIGINT NOT NULL,
    role VARCHAR(25) NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES project(id)
);