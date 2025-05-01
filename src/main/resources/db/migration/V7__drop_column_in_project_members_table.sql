ALTER TABLE project_member
DROP COLUMN nickname;

ALTER TABLE project_member
ADD CONSTRAINT fk_project_member_user
FOREIGN KEY (user_id) REFERENCES app_user (id)
ON DELETE CASCADE;