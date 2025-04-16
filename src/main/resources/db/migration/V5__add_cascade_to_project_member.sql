ALTER TABLE project_member DROP CONSTRAINT project_member_project_id_fkey;

ALTER TABLE project_member
ADD CONSTRAINT project_member_project_id_fkey
FOREIGN KEY (project_id)
REFERENCES project (id)
ON DELETE CASCADE;