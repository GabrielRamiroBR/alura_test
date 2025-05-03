package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.task.Type;
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
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private CourseService courseService;

    private Course course;

    @BeforeEach
    void setup() {
        course = new Course();
        course.setId(1L);
        course.setStatus(Status.BUILDING);
    }

    @Test
    void publishCourse_should_publish_when_valid_course_and_tasks() {
        List<Task> tasks = List.of(
                new Task("Tarefa 1", 1, Type.OPEN_TEXT, course),
                new Task("Tarefa 2", 2, Type.SINGLE_CHOICE, course),
                new Task("Tarefa 3", 3, Type.MULTIPLE_CHOICE, course)
        );

        when(courseRepository.findById(any())).thenReturn(Optional.of(course));
        when(taskRepository.findByCourseId(any())).thenReturn(tasks);
        when(courseRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Course published = courseService.publishCourse(1L);

        assertEquals(Status.PUBLISHED, published.getStatus());
        assertNotNull(published.getPublishedAt());
    }

    @Test
    void publishCourse_should_throw_when_course_not_found() {
        when(courseRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(CourseExceptions.NotFoundException.class, () -> {
            courseService.publishCourse(1L);
        });
    }

    @Test
    void publishCourse_should_throw_when_course_status_is_not_building() {
        course.setStatus(Status.PUBLISHED);

        when(courseRepository.findById(any())).thenReturn(Optional.of(course));

        assertThrows(CourseExceptions.InvalidStatusException.class, () -> {
            courseService.publishCourse(1L);
        });
    }

    @Test
    void publishCourse_should_throw_when_task_types_are_missing() {
        List<Task> tasks = List.of(
                new Task("Tarefa 1", 1, Type.OPEN_TEXT, course),
                new Task("Tarefa 2", 2, Type.SINGLE_CHOICE, course) // falta MULTIPLE_CHOICE
        );

        when(courseRepository.findById(any())).thenReturn(Optional.of(course));
        when(taskRepository.findByCourseId(any())).thenReturn(tasks);

        assertThrows(CourseExceptions.MissingTaskTypesException.class, () -> {
            courseService.publishCourse(1L);
        });
    }

    @Test
    void publishCourse_should_throw_when_task_order_is_invalid() {
        List<Task> tasks = List.of(
                new Task("Tarefa 1", 1, Type.OPEN_TEXT, course),
                new Task("Tarefa 2", 4, Type.SINGLE_CHOICE, course),
                new Task("Tarefa 3", 2, Type.MULTIPLE_CHOICE, course) // ordem quebrada
        );

        when(courseRepository.findById(any())).thenReturn(Optional.of(course));
        when(taskRepository.findByCourseId(any())).thenReturn(tasks);

        assertThrows(CourseExceptions.InvalidTaskOrderException.class, () -> {
            courseService.publishCourse(1L);
        });
    }
}

