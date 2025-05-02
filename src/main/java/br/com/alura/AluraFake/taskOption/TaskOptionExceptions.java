package br.com.alura.AluraFake.taskOption;


import br.com.alura.AluraFake.util.ErrorItemDTO;

import java.util.List;

public abstract class TaskOptionExceptions extends RuntimeException {

    public TaskOptionExceptions(String message) {
        super(message);
    }

    public abstract ErrorItemDTO getErrorMessage();

    public static class InvalidOptionsCountException extends TaskOptionExceptions {
        public InvalidOptionsCountException() {
            super("Quantidade de alternativas inválida");
        }
        @Override
        public ErrorItemDTO getErrorMessage() {
            return new ErrorItemDTO("N/A", getMessage());
        }
    }

    public static class InvalidCorrectOptionException extends TaskOptionExceptions {
        public InvalidCorrectOptionException() {
            super("Quantidade de alternativas corretas ou incorretas inválida");
        }
        @Override
        public ErrorItemDTO getErrorMessage() {
            return new ErrorItemDTO("isCorrect", getMessage());
        }
    }

    public static class InvalidOptionLengthException extends TaskOptionExceptions {
        private String option;

        public InvalidOptionLengthException(String option) { super("Tamanho de altrnativa inválido:" + option);
        }
        @Override
        public ErrorItemDTO getErrorMessage() {
            return new ErrorItemDTO("option", getMessage());
        }
    }


    public static class DuplicateOptionException extends TaskOptionExceptions {
        public DuplicateOptionException(List<String> duplicates) {
            super("As seguintes opções estão duplicadas: " + String.join(", ", duplicates));
        }

        @Override
        public ErrorItemDTO getErrorMessage() {
            return new ErrorItemDTO("options", getMessage());
        }
    }

    public static class OptionEqualsStatementException extends TaskOptionExceptions {
        private String option;

        public OptionEqualsStatementException() { super("Existe pelo menos uma alternativa igual ao enunciado");
        }
        @Override
        public ErrorItemDTO getErrorMessage() {
            return new ErrorItemDTO("option", getMessage());
        }
    }

}
