package br.com.alura.AluraFake.taskOption;

import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.TaskOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskOptionRepository extends JpaRepository<TaskOption, Long> {

    List<TaskOption> findByTask(Task task);
}

