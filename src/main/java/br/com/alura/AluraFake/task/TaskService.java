package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.error.ErrorMessage;
import br.com.alura.AluraFake.exceptions.TaskExceptions;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    @Autowired
    private  TaskRepository taskRepository;
    @Autowired
    private  CourseRepository courseRepository;

//    public TaskService(TaskRepository taskRepository, CourseRepository courseRepository) {
//        this.taskRepository = taskRepository;
//        this.courseRepository = courseRepository;
//    }

    @Transactional
    public Task createOpenTextTask(NewTaskDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(TaskExceptions.CourseNotFoundException::new);

        validateTask(dto, course,Type.OPEN_TEXT);

        taskRepository.updateOrderForCourse(dto.getCourseId(), dto.getOrder());

        Task task = new Task(dto.getStatement(), dto.getOrder(), Type.OPEN_TEXT, course);
        return taskRepository.save(task);
    }

    private void validateTask(NewTaskDTO dto, Course course, Type type){
        if (type.equals(Type.OPEN_TEXT)){

            if (!course.getStatus().equals(Status.BUILDING)) throw new TaskExceptions.InvalidCourseStatusException();

            if (taskRepository.existsByCourseAndStatement(course, dto.getStatement())) throw new TaskExceptions.DuplicateStatementException();

            if (dto.getOrder() < 0) throw new TaskExceptions.InvalidTaskOrderException();

            if (dto.getStatement().length() < 4 || dto.getStatement().length() > 255) throw new TaskExceptions.InvalidStatementException();

        }

    }
}

