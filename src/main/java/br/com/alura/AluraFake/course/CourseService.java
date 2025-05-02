package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.task.Type;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Transactional
    public Course publishCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(CourseExceptions.NotFoundException::new);

        validateCourseForPublication(courseId, course);


        course.setStatus(Status.PUBLISHED);
        course.setPublishedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    private void validateCourseForPublication(Long courseId, Course course) {

        if (course == null || !course.getStatus().equals(Status.BUILDING)) {
            throw new CourseExceptions.InvalidStatusException();
        }


        List<Task> tasks = taskRepository.findByCourseId(courseId);
        boolean hasOpenText = tasks.stream().anyMatch(task -> task.getType() == Type.OPEN_TEXT);
        boolean hasSingleChoice = tasks.stream().anyMatch(task -> task.getType() == Type.SINGLE_CHOICE);
        boolean hasMultipleChoice = tasks.stream().anyMatch(task -> task.getType() == Type.MULTIPLE_CHOICE);

        if (!hasOpenText || !hasSingleChoice || !hasMultipleChoice) {
            throw new CourseExceptions.MissingTaskTypesException();
        }


        List<Integer> orders = tasks.stream()
                .map(Task::getTaskOrder)
                .sorted()
                .toList();

        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i) != i + 1) {
                throw new CourseExceptions.InvalidTaskOrderException();
            }
        }
    }
}