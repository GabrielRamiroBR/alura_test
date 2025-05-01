package br.com.alura.AluraFake.taskOption;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NewTaskOptionDTO {

    @NotBlank(message = "A opção não pode ser vazia.")
//    @Size(min = 4, max = 80, message = "A opção deve ter entre 4 e 80 caracteres.")
    private String option;

    private boolean isCorrect;

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}
