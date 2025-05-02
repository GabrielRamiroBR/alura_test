package br.com.alura.AluraFake.taskOption;

public class TaskOptionResponseDTO {
    private String option;
    private boolean correct;

    public TaskOptionResponseDTO(String optionText, boolean correct) {
        this.option = optionText;
        this.correct = correct;
    }

    // Getters
    public String getOption() {
        return option;
    }

    public boolean isCorrect() {
        return correct;
    }
}
