CREATE TABLE task_option (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    createdAt datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    option_text VARCHAR(80) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    task_id BIGINT NOT NULL,
    CONSTRAINT fk_option_activity FOREIGN KEY (task_id) REFERENCES task(id)
);