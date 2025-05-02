package br.com.alura.AluraFake.exceptions;

import br.com.alura.AluraFake.error.ErrorMessage;

import java.util.List;

public abstract class TaskOptionExceptions extends RuntimeException {

    public TaskOptionExceptions(String message) {
        super(message);
    }

    public abstract ErrorMessage getErrorMessage();

    public static class InvalidOptionsCountException extends TaskOptionExceptions {
        public InvalidOptionsCountException() {
            super("Quantidade de alternativas inválida");
        }
        @Override
        public ErrorMessage getErrorMessage() {
            return new ErrorMessage(null, getMessage());
        }
    }

    public static class InvalidCorrectOptionException extends TaskOptionExceptions {
        public InvalidCorrectOptionException() {
            super("Quantidade de alternativas corretas ou incorretas inválida");
        }
        @Override
        public ErrorMessage getErrorMessage() {
            return new ErrorMessage("isCorrect", getMessage());
        }
    }

    public static class InvalidOptionLengthException extends TaskOptionExceptions {
        private String option;

        public InvalidOptionLengthException(String option) { super("Tamanho de altrnativa inválido:" + option);
        }
        @Override
        public ErrorMessage getErrorMessage() {
            return new ErrorMessage("option", getMessage());
        }
    }


    public static class DuplicateOptionException extends TaskOptionExceptions {
        public DuplicateOptionException(List<String> duplicates) {
            super("As seguintes opções estão duplicadas: " + String.join(", ", duplicates));
        }

        @Override
        public ErrorMessage getErrorMessage() {
            return new ErrorMessage("options", getMessage());
        }
    }

    public static class OptionEqualsStatementException extends TaskOptionExceptions {
        private String option;

        public OptionEqualsStatementException() { super("Existe pelo menos uma alternativa igual ao enunciado");
        }
        @Override
        public ErrorMessage getErrorMessage() {
            return new ErrorMessage("option", getMessage());
        }
    }

}
