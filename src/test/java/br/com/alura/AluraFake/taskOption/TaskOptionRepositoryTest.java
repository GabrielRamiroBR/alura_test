package br.com.alura.AluraFake.taskOption;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.taskOption.TaskOption;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.user.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskOptionRepositoryTest {

    @Autowired
    private TaskOptionRepository taskOptionRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByTask__should_return_all_options_of_task() {
        // Cria usuário e curso
        User instructor = new User("Julia", "julia@alura.com", Role.INSTRUCTOR);
        userRepository.save(instructor);

        Course course = new Course("Curso Spring", "Descricao", instructor);
        course.setStatus(Status.BUILDING);
        courseRepository.save(course);

        // Cria uma tarefa
        Task task = new Task("Qual a capital da França?", 1, Type.SINGLE_CHOICE, course);
        taskRepository.save(task);

        // Cria opções
        TaskOption op1 = new TaskOption("Paris", true, task);
        TaskOption op2 = new TaskOption("Londres", false, task);
        TaskOption op3 = new TaskOption("Berlim", false, task);
        taskOptionRepository.saveAll(List.of(op1, op2, op3));

        // Busca as opções por tarefa
        List<TaskOption> options = taskOptionRepository.findByTask(task);

        // Verifica
        assertThat(options).hasSize(3);
        assertThat(options)
                .extracting(TaskOption::getOptionText)
                .containsExactlyInAnyOrder("Paris", "Londres", "Berlim");
    }
}
