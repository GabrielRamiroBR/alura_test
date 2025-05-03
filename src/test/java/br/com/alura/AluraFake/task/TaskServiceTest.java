package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.*;
import br.com.alura.AluraFake.task.*;
import br.com.alura.AluraFake.taskOption.NewTaskOptionDTO;
import br.com.alura.AluraFake.taskOption.TaskOption;
import br.com.alura.AluraFake.taskOption.TaskOptionExceptions;
import br.com.alura.AluraFake.taskOption.TaskOptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private TaskOptionRepository taskOptionRepository;

    @InjectMocks
    private TaskService taskService;

    private Course course;

    @BeforeEach
    void setup() {
        course = new Course();
        course.setId(1L);
        course.setStatus(Status.BUILDING);
    }

    @Test
    void createOpenTextTask_should_create_task_when_valid_request() {
        Course course = new Course();
        course.setId(1L);
        course.setStatus(Status.BUILDING);

        NewTaskDTO dto = new NewTaskDTO(1L, "Descreva a Revolução Francesa", 1, null);

        when(courseRepository.findById(any())).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseAndStatement(any(), any())).thenReturn(false);
        when(taskRepository.countByCourse(any())).thenReturn(0);
        when(taskRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponseDTO response = taskService.createOpenTextTask(dto);

        assertEquals("Descreva a Revolução Francesa", response.getStatement());
        assertEquals(Type.OPEN_TEXT.toString(), response.getType().toString());
        assertTrue(response.getOptions().isEmpty());
    }

    @Test
    void createOpenTextTask_should_throw_exception_when_course_not_found() {
        NewTaskDTO dto = new NewTaskDTO(99L, "Enunciado", 1, null);

        when(courseRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(TaskExceptions.CourseNotFoundException.class, () -> {
            taskService.createOpenTextTask(dto);
        });
    }

    @Test
    void createOpenTextTask_should_throw_exception_when_course_status_is_not_building() {
        course.setStatus(Status.PUBLISHED);
        NewTaskDTO dto = new NewTaskDTO(1L, "Enunciado", 1, null);

        when(courseRepository.findById(any())).thenReturn(Optional.of(course));

        assertThrows(TaskExceptions.InvalidCourseStatusException.class, () -> {
            taskService.createOpenTextTask(dto);
        });
    }

    @Test
    void createOpenTextTask_should_throw_exception_when_statement_is_duplicate() {
        NewTaskDTO dto = new NewTaskDTO(1L, "Duplicado", 1, null);

        when(courseRepository.findById(any())).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseAndStatement(course, "Duplicado")).thenReturn(true);

        assertThrows(TaskExceptions.DuplicateStatementException.class, () -> {
            taskService.createOpenTextTask(dto);
        });
    }

    @Test
    void createOpenTextTask_should_throw_exception_when_order_is_negative() {
        NewTaskDTO dto = new NewTaskDTO(1L, "Questao", -1, null);

        when(courseRepository.findById(any())).thenReturn(Optional.of(course));

        assertThrows(TaskExceptions.InvalidTaskOrderException.class, () -> {
            taskService.createOpenTextTask(dto);
        });
    }

    @Test
    void createOpenTextTask_should_throw_exception_when_statement_length_is_invalid() {
        NewTaskDTO dto = new NewTaskDTO(1L, "A", 1, null);

        when(courseRepository.findById(any())).thenReturn(Optional.of(course));

        assertThrows(TaskExceptions.InvalidStatementException.class, () -> {
            taskService.createOpenTextTask(dto);
        });
    }

    @Test
    void createOpenTextTask_should_throw_exception_when_order_is_too_high() {
        NewTaskDTO dto = new NewTaskDTO(1L, "Enunciado válido", 5, null);

        when(courseRepository.findById(any())).thenReturn(Optional.of(course));
        when(taskRepository.countByCourse(course)).thenReturn(2);

        assertThrows(TaskExceptions.InvalidTaskOrderException.class, () -> {
            taskService.createOpenTextTask(dto);
        });
    }

    @Test
    void createSingleChoiceTask_should_create_task_when_valid_request() {
        Course course = new Course();
        course.setId(1L);
        course.setStatus(Status.BUILDING);

        NewTaskDTO dto = new NewTaskDTO(1L, "Qual é a capital da França?", 1, List.of(
                new NewTaskOptionDTO("Paris", true),
                new NewTaskOptionDTO("Londres", false),
                new NewTaskOptionDTO("Berlim", false)
        ));

        when(courseRepository.findById(any())).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseAndStatement(any(), any())).thenReturn(false);
        when(taskRepository.countByCourse(any())).thenReturn(0);
        when(taskRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(taskOptionRepository.findByTask(any())).thenReturn(List.of(
                new TaskOption("Paris", true, null),
                new TaskOption("Londres", false, null),
                new TaskOption("Berlim", false, null)
        ));

        TaskResponseDTO response = taskService.createSingleChoiceTask(dto);

        assertEquals("Qual é a capital da França?", response.getStatement());
        assertEquals(Type.SINGLE_CHOICE.toString(), response.getType().toString());
        assertEquals(3, response.getOptions().size());
    }
    @Test
    void createMultipleChoiceTask_should_create_task_when_valid_request() {
        Course course = new Course();
        course.setId(1L);
        course.setStatus(Status.BUILDING);

        NewTaskDTO dto = new NewTaskDTO(1L, "Quais são linguagens de programação?", 1, List.of(
                new NewTaskOptionDTO("Java", true),
                new NewTaskOptionDTO("Python", true),
                new NewTaskOptionDTO("Banana", false)
        ));

        when(courseRepository.findById(any())).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseAndStatement(any(), any())).thenReturn(false);
        when(taskRepository.countByCourse(any())).thenReturn(0);
        when(taskRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(taskOptionRepository.findByTask(any())).thenReturn(List.of(
                new TaskOption("Java", true, null),
                new TaskOption("Python", true, null),
                new TaskOption("Banana", false, null)
        ));

        TaskResponseDTO response = taskService.createMultipleChoiceTask(dto);

        assertEquals("Quais são linguagens de programação?", response.getStatement());
        assertEquals(Type.MULTIPLE_CHOICE.toString(), response.getType().toString());
        assertEquals(3, response.getOptions().size());
    }

    ///
    @Test
    void createSingleChoiceTask_should_throw_exception_when_course_not_found() {
        NewTaskDTO dto = new NewTaskDTO(99L, "Pergunta", 1, List.of(
                new NewTaskOptionDTO("Opção 1", true),
                new NewTaskOptionDTO("Opção 2", false)
        ));

        when(courseRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(TaskExceptions.CourseNotFoundException.class, () -> {
            taskService.createSingleChoiceTask(dto);
        });
    }

    @Test
    void createSingleChoiceTask_should_throw_exception_when_course_status_is_not_building() {
        Course course = new Course();
        course.setId(1L);
        course.setStatus(Status.PUBLISHED); // status inválido

        NewTaskDTO dto = new NewTaskDTO(1L, "Pergunta", 1, List.of(
                new NewTaskOptionDTO("Opção 1", true),
                new NewTaskOptionDTO("Opção 2", false)
        ));

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        assertThrows(TaskExceptions.InvalidCourseStatusException.class, () -> {
            taskService.createSingleChoiceTask(dto);
        });
    }

    @Test
    void createSingleChoiceTask_should_throw_exception_when_duplicate_statement() {
        Course course = new Course();
        course.setId(1L);
        course.setStatus(Status.BUILDING);

        NewTaskDTO dto = new NewTaskDTO(1L, "Pergunta", 1, List.of(
                new NewTaskOptionDTO("Opção 1", true),
                new NewTaskOptionDTO("Opção 2", false)
        ));

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseAndStatement(any(), any())).thenReturn(true);

        assertThrows(TaskExceptions.DuplicateStatementException.class, () -> {
            taskService.createSingleChoiceTask(dto);
        });
    }

    @Test
    void createSingleChoiceTask_should_throw_exception_when_options_are_null() {
        Course course = new Course();
        course.setId(1L);
        course.setStatus(Status.BUILDING);

        NewTaskDTO dto = new NewTaskDTO(1L, "Pergunta", 1, null);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseAndStatement(any(), any())).thenReturn(false);
        when(taskRepository.countByCourse(any())).thenReturn(0);

        assertThrows(TaskOptionExceptions.InvalidOptionsCountException.class, () -> {
            taskService.createSingleChoiceTask(dto);
        });
    }

    @Test
    void createSingleChoiceTask_should_throw_exception_when_correct_options_count_is_invalid() {
        Course course = new Course();
        course.setId(1L);
        course.setStatus(Status.BUILDING);

        NewTaskDTO dto = new NewTaskDTO(1L, "Pergunta", 1, List.of(
                new NewTaskOptionDTO("Opção 1", true),
                new NewTaskOptionDTO("Opção 2", true)
        ));

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseAndStatement(any(), any())).thenReturn(false);
        when(taskRepository.countByCourse(any())).thenReturn(0);

        assertThrows(TaskOptionExceptions.InvalidCorrectOptionException.class, () -> {
            taskService.createSingleChoiceTask(dto);
        });
    }

    @Test
    void createMultipleChoiceTask_should_throw_exception_when_correct_or_incorrect_count_invalid() {
        Course course = new Course();
        course.setId(1L);
        course.setStatus(Status.BUILDING);

        NewTaskDTO dto = new NewTaskDTO(1L, "Pergunta", 1, List.of(
                new NewTaskOptionDTO("Opção 1", true),
                new NewTaskOptionDTO("Opção 2", true),
                new NewTaskOptionDTO("Opção 3", true) // Nenhuma incorreta
        ));

        when(courseRepository.findById(any())).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseAndStatement(any(), any())).thenReturn(false);
        when(taskRepository.countByCourse(any())).thenReturn(0);

        assertThrows(TaskOptionExceptions.InvalidCorrectOptionException.class, () -> {
            taskService.createMultipleChoiceTask(dto);
        });
    }

    @Test
    void createMultipleChoiceTask_should_throw_exception_when_options_are_repeated() {
        Course course = new Course();
        course.setId(1L);
        course.setStatus(Status.BUILDING);

        NewTaskDTO dto = new NewTaskDTO(1L, "Pergunta", 1, List.of(
                new NewTaskOptionDTO("Java", true),
                new NewTaskOptionDTO("Java", false),
                new NewTaskOptionDTO("Python", true)
        ));

        when(courseRepository.findById(any())).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseAndStatement(any(), any())).thenReturn(false);
        when(taskRepository.countByCourse(any())).thenReturn(0);


        assertThrows(TaskOptionExceptions.DuplicateOptionException.class, () -> {
            taskService.createMultipleChoiceTask(dto);
        });
    }

    @Test
    void createSingleChoiceTask_should_throw_exception_when_option_equals_statement() {
        NewTaskDTO dto = new NewTaskDTO(1L, "França", 1, List.of(
                new NewTaskOptionDTO("França", true),
                new NewTaskOptionDTO("Alemanha", false)
        ));

        when(courseRepository.findById(any())).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseAndStatement(any(), any())).thenReturn(false);
        when(taskRepository.countByCourse(any())).thenReturn(0);

        assertThrows(TaskOptionExceptions.OptionEqualsStatementException.class, () -> {
            taskService.createSingleChoiceTask(dto);
        });
    }

    @Test
    void createSingleChoiceTask_should_throw_exception_when_option_is_too_short() {
        NewTaskDTO dto = new NewTaskDTO(1L, "Qual é?", 1, List.of(
                new NewTaskOptionDTO("A", true),
                new NewTaskOptionDTO("Algo", false)
        ));

        when(courseRepository.findById(any())).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseAndStatement(any(), any())).thenReturn(false);
        when(taskRepository.countByCourse(any())).thenReturn(0);

        assertThrows(TaskOptionExceptions.InvalidOptionLengthException.class, () -> {
            taskService.createSingleChoiceTask(dto);
        });
    }
    @Test
    void createMultipleChoiceTask_should_throw_exception_when_options_count_is_less_than_three() {
        NewTaskDTO dto = new NewTaskDTO(1L, "Pergunta", 1, List.of(
                new NewTaskOptionDTO("Opção 1", true),
                new NewTaskOptionDTO("Opção 2", false)
        ));

        when(courseRepository.findById(any())).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseAndStatement(any(), any())).thenReturn(false);
        when(taskRepository.countByCourse(any())).thenReturn(0);

        assertThrows(TaskOptionExceptions.InvalidOptionsCountException.class, () -> {
            taskService.createMultipleChoiceTask(dto);
        });
    }


}
