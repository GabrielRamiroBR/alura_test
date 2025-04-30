package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    boolean existsByCourseAndStatement(Course course, String statement);

    List<Task> findByCourseAndTaskOrderGreaterThanEqualOrderByTaskOrderDesc(Course course, Integer taskOrder);
}
