package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.taskOption.NewTaskOptionDTO;
import br.com.alura.AluraFake.taskOption.TaskOption;
import br.com.alura.AluraFake.taskOption.TaskOptionResponseDTO;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class TaskResponseDTO {
    private Long id;
    private String statement;
    private Integer order;
    private Type type;
    private List<NewTaskOptionDTO> options;

    public TaskResponseDTO(Task task, List<TaskOption> options) {
        this.id = task.getId();
        this.statement = task.getStatement();
        this.order = task.getTaskOrder();
        this.type = task.getType();
        this.options = options.stream()
                .map(option -> new NewTaskOptionDTO(option.getOptionText(), option.isCorrect()))
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

    public Type getType() {
        return type;
    }

    public List<NewTaskOptionDTO> getOptions() {
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

    public void setType(Type type) {
        this.type = type;
    }

    public void setOptions(List<NewTaskOptionDTO> options) {
        this.options = options;
    }
}