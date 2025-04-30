package br.com.alura.AluraFake.task;

import jakarta.persistence.*;


@Entity
public class TaskOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String option;

    private boolean isCorrect;

    @ManyToOne
    private Task task;

    @Deprecated
    public TaskOption() {}

    public TaskOption(String option, boolean isCorrect, Task task) {
        this.option = option;
        this.isCorrect = isCorrect;
        this.task = task;
    }

    public Long getId() {
        return id;
    }

    public String getOption() {
        return option;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public Task getTask() {
        return task;
    }
}
