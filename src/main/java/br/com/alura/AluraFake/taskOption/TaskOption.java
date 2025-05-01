package br.com.alura.AluraFake.taskOption;

import br.com.alura.AluraFake.task.Task;
import jakarta.persistence.*;


@Entity
@Table(name = "task_option")
public class TaskOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_text")
    private String optionText;

    @Column(name = "is_correct")
    private boolean isCorrect;

    @ManyToOne
    private Task task;

    @Deprecated
    public TaskOption() {}

    public TaskOption(String option, boolean isCorrect, Task task) {
        this.optionText = option;
        this.isCorrect = isCorrect;
        this.task = task;
    }

    public Long getId() {
        return id;
    }

    public String getOptionText() {
        return optionText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public Task getTask() {
        return task;
    }
}
