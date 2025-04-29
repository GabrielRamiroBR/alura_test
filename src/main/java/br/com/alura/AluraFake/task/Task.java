package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt = LocalDateTime.now();

    private String statement;

    private Integer taskOrder;

    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne
    private Course course;

    @Deprecated
    public Task() {}

    public Task(String statement, Integer taskOrder, Type type, Course course) {
        Assert.notNull(statement, "Enunciado não pode ser nulo");
        Assert.isTrue(statement.length() >= 4 && statement.length() <= 255, "Enunciado deve ter entre 4 e 255 caracteres");
        Assert.notNull(type, "Tipo da atividade não pode ser nulo");
        Assert.notNull(course, "Curso não pode ser nulo");

        this.statement = statement;
        this.taskOrder = taskOrder;
        this.type = type;
        this.course = course;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getStatement() {
        return statement;
    }

    public Integer getTaskOrder() {
        return taskOrder;
    }

    public Type getType() {
        return type;
    }

    public Course getCourse() {
        return course;
    }
}
