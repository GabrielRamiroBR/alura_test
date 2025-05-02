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

    public TaskResponseDTO() {
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOptions(List<TaskOptionResponseDTO> options) {
        this.options = options;
    }
}