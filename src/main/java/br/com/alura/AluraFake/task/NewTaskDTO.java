package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.taskOption.NewTaskOptionDTO;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public class NewTaskDTO {

    @NotNull
    private Long courseId;

    @NotBlank
//    @Size(min = 4, max = 255, message = "O enunciado deve ter entre 4 e 255 caracteres.")
    private String statement;

    @NotNull
//    @Positive(message = "A ordem deve ser um número inteiro positivo.")
    private Integer order;

    @Nullable
    private List<NewTaskOptionDTO> options;

    // Getters e setters

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public List<NewTaskOptionDTO> getOptions(){ return options; }
}
