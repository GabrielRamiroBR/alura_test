package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private br.com.alura.AluraFake.course.CourseRepository courseRepository;

    @Autowired
    private br.com.alura.AluraFake.user.UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Course course;

    @BeforeEach
    void setup() {
        User instructor = userRepository.save(new User("Instrutor", "instructor@alura.com.br", Role.INSTRUCTOR));
        course = new Course();
        course.setTitle("Curso de Java");
        course.setDescription("Descrição");
        course.setStatus(Status.BUILDING);
        course.setInstructor(instructor);
        course = courseRepository.save(course);
    }

    @Test
    void existsByCourseAndStatement__should_return_true_when_task_exists() {
        Task task = new Task("Qual a diferença entre JDK e JRE?", 1, Type.OPEN_TEXT, course);
        taskRepository.save(task);

        boolean exists = taskRepository.existsByCourseAndStatement(course, "Qual a diferença entre JDK e JRE?");
        assertThat(exists).isTrue();

        boolean notExists = taskRepository.existsByCourseAndStatement(course, "Outra pergunta?");
        assertThat(notExists).isFalse();
    }

    @Test
    void findByCourseAndTaskOrderGreaterThanEqualOrderByTaskOrderDesc__should_return_desc_ordered_tasks() {
        taskRepository.save(new Task("Pergunta 1", 1, Type.OPEN_TEXT, course));
        taskRepository.save(new Task("Pergunta 2", 2, Type.SINGLE_CHOICE, course));
        taskRepository.save(new Task("Pergunta 3", 3, Type.MULTIPLE_CHOICE, course));

        List<Task> result = taskRepository.findByCourseAndTaskOrderGreaterThanEqualOrderByTaskOrderDesc(course, 2);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTaskOrder()).isEqualTo(3);
        assertThat(result.get(1).getTaskOrder()).isEqualTo(2);
    }

    @Test
    void updateOrderForCourse__should_increment_task_order() {
        taskRepository.save(new Task("Pergunta 1", 1, Type.OPEN_TEXT, course));
        taskRepository.save(new Task("Pergunta 2", 2, Type.SINGLE_CHOICE, course));
        taskRepository.save(new Task("Pergunta 3", 3, Type.MULTIPLE_CHOICE, course));

        taskRepository.updateOrderForCourse(course.getId(), 2);

        List<Task> tasks = taskRepository.findByCourseId(course.getId());

        assertThat(tasks).extracting(Task::getTaskOrder)
                .containsExactlyInAnyOrder(1, 3, 4);
    }

    @Test
    void countByCourse__should_return_number_of_tasks() {
        taskRepository.save(new Task("Pergunta 1", 1, Type.OPEN_TEXT, course));
        taskRepository.save(new Task("Pergunta 2", 2, Type.SINGLE_CHOICE, course));

        int count = taskRepository.countByCourse(course);
        assertThat(count).isEqualTo(2);
    }

    @Test
    void findByCourseId__should_return_all_tasks_for_course() {
        Task t1 = taskRepository.save(new Task("Pergunta 1", 1, Type.OPEN_TEXT, course));
        Task t2 = taskRepository.save(new Task("Pergunta 2", 2, Type.SINGLE_CHOICE, course));

        List<Task> result = taskRepository.findByCourseId(course.getId());
        assertThat(result).containsExactlyInAnyOrder(t1, t2);
    }

}
