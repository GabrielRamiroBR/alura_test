package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.exceptions.TaskExceptions;
import br.com.alura.AluraFake.exceptions.TaskOptionExceptions;
import br.com.alura.AluraFake.response.TaskResponseDTO;
import br.com.alura.AluraFake.taskOption.NewTaskOptionDTO;
import br.com.alura.AluraFake.taskOption.TaskOption;
import br.com.alura.AluraFake.taskOption.TaskOptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private  TaskRepository taskRepository;
    @Autowired
    private  CourseRepository courseRepository;
    @Autowired
    private TaskOptionRepository taskOptionRepository;

//    public TaskService(TaskRepository taskRepository, CourseRepository courseRepository) {
//        this.taskRepository = taskRepository;
//        this.courseRepository = courseRepository;
//    }

    @Transactional
    public TaskResponseDTO createOpenTextTask(NewTaskDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(TaskExceptions.CourseNotFoundException::new);

        validateTask(dto, course,Type.OPEN_TEXT);

        taskRepository.updateOrderForCourse(dto.getCourseId(), dto.getOrder());

        Task task = new Task(dto.getStatement(), dto.getOrder(), Type.OPEN_TEXT, course);
        taskRepository.save(task);

        return new TaskResponseDTO(task, null);
    }

    public TaskResponseDTO createSingleChoiceTask(NewTaskDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(TaskExceptions.CourseNotFoundException::new);

        validateTask(dto, course, Type.SINGLE_CHOICE);
        validateTaskOptions(dto.getOptions(), true); // true = single choice

        taskRepository.updateOrderForCourse(dto.getCourseId(), dto.getOrder());

        Task task = new Task(dto.getStatement(), dto.getOrder(), Type.SINGLE_CHOICE, course);
        task = taskRepository.save(task);

        createTaskOptions(task, dto.getOptions());

        List<TaskOption> options = taskOptionRepository.findByTask(task);

        return new TaskResponseDTO(task,options);
    }



    private void validateTask(NewTaskDTO dto, Course course, Type type){

        if (!course.getStatus().equals(Status.BUILDING)) throw new TaskExceptions.InvalidCourseStatusException();

        if (taskRepository.existsByCourseAndStatement(course, dto.getStatement())) throw new TaskExceptions.DuplicateStatementException();

        if (dto.getOrder() < 0) throw new TaskExceptions.InvalidTaskOrderException();

        if (dto.getStatement().length() < 4 || dto.getStatement().length() > 255) throw new TaskExceptions.InvalidStatementException();

        int totalTasks = taskRepository.countByCourse(course);
        if (dto.getOrder() > totalTasks + 1) throw new TaskExceptions.InvalidTaskOrderException();

    }

    private void validateTaskOptions(List<NewTaskOptionDTO> options, boolean singleChoice) {
        if (singleChoice) {
            if (options == null || options.size() < 2 || options.size() > 5) throw new TaskOptionExceptions.InvalidOptionsCountException();

            long correctCount = options.stream().filter(NewTaskOptionDTO::isCorrect).count();
            if (singleChoice && correctCount != 1) throw new TaskOptionExceptions.InvalidCorrectOptionException();

            for (NewTaskOptionDTO alternative : options) {
                if (alternative.getOption().length() < 4 || alternative.getOption().length() > 80)
                    throw new TaskOptionExceptions.InvalidOptionLengthException(alternative.getOption());
            }

            Map<String, Integer> counts = new HashMap<>();

            for (NewTaskOptionDTO option : options) {
                String normalized = option.getOption().trim().toLowerCase();
                counts.put(normalized, counts.getOrDefault(normalized, 0) + 1);
            }

            List<String> duplicates = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                if (entry.getValue() > 1) {
                    duplicates.add(entry.getKey());
                }
            }

            if (!duplicates.isEmpty()) {
                throw new TaskOptionExceptions.DuplicateOptionException(duplicates);
            }
        }
    }

    private void createTaskOptions(Task task, List<NewTaskOptionDTO> options) {
        List<TaskOption> taskOptions = options.stream()
                .map(o -> new TaskOption(o.getOption(), o.isCorrect(), task))
                .collect(Collectors.toList());
        taskOptionRepository.saveAll(taskOptions);
    }

}

