package br.com.alura.AluraFake.exceptions;

public class TaskExceptions extends RuntimeException {

    public TaskExceptions(String message) {
        super(message);
    }

    public static class CourseNotFoundException extends TaskExceptions {
        public CourseNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidCourseStatusException extends TaskExceptions {
        public InvalidCourseStatusException(String message) {
            super(message);
        }
    }

    public static class InvalidTaskOrderException extends TaskExceptions {
        public InvalidTaskOrderException(String message) {
            super(message);
        }
    }

    public static class DuplicateStatementException extends TaskExceptions {
        public DuplicateStatementException(String message) {
            super(message);
        }
    }
}
