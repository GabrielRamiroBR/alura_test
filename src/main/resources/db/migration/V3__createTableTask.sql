CREATE TABLE task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    createdAt datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    statement VARCHAR(255) NOT NULL,
    task_order INT NOT NULL,
    type ENUM('OPEN_TEXT', 'SINGLE_CHOICE', 'MULTIPLE_CHOICE') NOT NULL,
    course_id BIGINT NOT NULL,
    CONSTRAINT fk_task_course FOREIGN KEY (course_id) REFERENCES course(id),
    CONSTRAINT uq_course_statement UNIQUE (course_id, statement)
);