ALTER TABLE app_user
ADD CONSTRAINT uk_email UNIQUE (email);

ALTER TABLE app_user
ADD CONSTRAINT uk_nickname UNIQUE (nickname);