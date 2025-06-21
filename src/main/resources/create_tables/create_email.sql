CREATE TABLE email_data
(
    id      BIGINT NOT NULL,
    user_id BIGINT,
    email   VARCHAR(255),
    CONSTRAINT pk_email_data PRIMARY KEY (id)
);

ALTER TABLE email_data
    ADD CONSTRAINT uc_email_data_email UNIQUE (email);

ALTER TABLE email_data
    ADD CONSTRAINT FK_EMAIL_DATA_ON_USER FOREIGN KEY (user_id) REFERENCES "user" (id);