package br.com.alura.AluraFake.response;

public class TaskOptionResponseDTO {
    private String optionText;
    private boolean correct;

    public TaskOptionResponseDTO(String optionText, boolean correct) {
        this.optionText = optionText;
        this.correct = correct;
    }

    // Getters
    public String getOptionText() {
        return optionText;
    }

    public boolean isCorrect() {
        return correct;
    }
}
