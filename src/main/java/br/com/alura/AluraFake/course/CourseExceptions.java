package br.com.alura.AluraFake.course;


import br.com.alura.AluraFake.util.ErrorItemDTO;

public abstract class CourseExceptions extends RuntimeException {

  public CourseExceptions(String message) {
    super(message);
  }

  public abstract ErrorItemDTO getErrorMessage();

  public static class NotFoundException extends CourseExceptions {
    public NotFoundException() {
      super("Curso não encontrado");
    }

    @Override
    public ErrorItemDTO getErrorMessage() {
      return new ErrorItemDTO("id", getMessage());
    }
  }

  public static class InvalidStatusException extends CourseExceptions {
    public InvalidStatusException() {
      super("Curso não está com status BUILDING");
    }

    @Override
    public ErrorItemDTO getErrorMessage() {
      return new ErrorItemDTO("status", getMessage());
    }
  }

  public static class MissingTaskTypesException extends CourseExceptions {
    public MissingTaskTypesException() {
      super("Curso deve conter ao menos uma atividade de cada tipo");
    }

    @Override
    public ErrorItemDTO getErrorMessage() {
      return new ErrorItemDTO("tasks", getMessage());
    }
  }

  public static class InvalidTaskOrderException extends CourseExceptions {
    public InvalidTaskOrderException() {
      super("As atividades devem ter ordens contínuas (ex: 1, 2, 3...)");
    }

    @Override
    public ErrorItemDTO getErrorMessage() {
      return new ErrorItemDTO("order", getMessage());
    }
  }
}
