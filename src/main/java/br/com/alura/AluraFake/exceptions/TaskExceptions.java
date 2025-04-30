package br.com.alura.AluraFake.exceptions;

import br.com.alura.AluraFake.error.ErrorMessage;

public abstract class TaskExceptions extends RuntimeException {

    public TaskExceptions(String message) {
        super(message);
    }

    public abstract ErrorMessage getErrorMessage();

    public static class CourseNotFoundException extends TaskExceptions {
        public CourseNotFoundException() {
            super("Curso não encontrado");
        }

        @Override
        public ErrorMessage getErrorMessage() {
            return new ErrorMessage("courseId", getMessage());
        }
    }

    public static class InvalidCourseStatusException extends TaskExceptions {
        public InvalidCourseStatusException() {
            super("Curso deve estar com status BUILDING para adicionar atividades");
        }

        @Override
        public ErrorMessage getErrorMessage() {
            return new ErrorMessage("course.status", getMessage());
        }
    }

    public static class InvalidTaskOrderException extends TaskExceptions {
        public InvalidTaskOrderException() {
            super("Ordem da tarefa inválida");
        }

        @Override
        public ErrorMessage getErrorMessage() {
            return new ErrorMessage("order", getMessage());
        }
    }

    public static class DuplicateStatementException extends TaskExceptions {
        public DuplicateStatementException() {
            super("Já existe uma atividade com esse enunciado para o curso");
        }

        @Override
        public ErrorMessage getErrorMessage() {
            return new ErrorMessage("statement", getMessage());
        }
    }
}
