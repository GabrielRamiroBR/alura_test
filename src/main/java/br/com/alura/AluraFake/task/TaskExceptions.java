package br.com.alura.AluraFake.task;


import br.com.alura.AluraFake.util.ErrorItemDTO;

public abstract class TaskExceptions extends RuntimeException {

    public TaskExceptions(String message) {
        super(message);
    }

    public abstract ErrorItemDTO getErrorMessage();

    public static class CourseNotFoundException extends TaskExceptions {
        public CourseNotFoundException() {
            super("Curso não encontrado");
        }

        @Override
        public ErrorItemDTO getErrorMessage() {
            return new ErrorItemDTO("courseId", getMessage());
        }
    }

    public static class InvalidCourseStatusException extends TaskExceptions {
        public InvalidCourseStatusException() {
            super("Curso deve estar com status BUILDING para adicionar atividades");
        }

        @Override
        public ErrorItemDTO getErrorMessage() {
            return new ErrorItemDTO("course.status", getMessage());
        }
    }

    public static class InvalidTaskOrderException extends TaskExceptions {
        public InvalidTaskOrderException() {
            super("Ordem da tarefa inválida");
        }

        @Override
        public ErrorItemDTO getErrorMessage() {
            return new ErrorItemDTO("order", getMessage());
        }
    }

    public static class DuplicateStatementException extends TaskExceptions {
        public DuplicateStatementException() {
            super("Já existe uma atividade com esse enunciado para o curso");
        }

        @Override
        public ErrorItemDTO getErrorMessage() {
            return new ErrorItemDTO("statement", getMessage());
        }
    }

    public static class InvalidStatementException extends TaskExceptions {
        public InvalidStatementException() {
            super("O enunciado deve ter entre 4 e 255 caracteres.");
        }

        @Override
        public ErrorItemDTO getErrorMessage() {
            return new ErrorItemDTO("statement", getMessage());
        }
    }
}
