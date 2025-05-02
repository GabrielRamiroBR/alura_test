package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.taskOption.TaskOption;
import br.com.alura.AluraFake.taskOption.TaskOptionResponseDTO;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class TaskResponseDTO {
    private Long id;
    private String statement;
    private Integer order;
    private String type;
    private List<TaskOptionResponseDTO> options;

    public TaskResponseDTO(Task task, List<TaskOption> options) {
        this.id = task.getId();
        this.statement = task.getStatement();
        this.order = task.getTaskOrder();
        this.type = task.getType().name();
        this.options = options.stream()
                .map(option -> new TaskOptionResponseDTO(option.getOptionText(), option.isCorrect()))
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getStatement() {
        return statement;
    }

    public Integer getOrder() {
        return order;
    }

    public String getType() {
        return type;
    }

    public List<TaskOptionResponseDTO> getOptions() {
        return options;
    }
}