package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    boolean existsByCourseAndStatement(Course course, String statement);

    List<Task> findByCourseAndTaskOrderGreaterThanEqualOrderByTaskOrderDesc(Course course, Integer taskOrder);

    @Transactional
    @Modifying
    @Query("""
    UPDATE Task t 
    SET t.taskOrder = t.taskOrder + 1 
    WHERE t.course.id = :courseId AND t.taskOrder >= :order
""")
    void updateOrderForCourse(@Param("courseId") Long courseId, @Param("order") int order);

}
