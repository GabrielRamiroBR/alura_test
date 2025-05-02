package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.taskOption.TaskOptionExceptions;
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


    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;
    private final TaskOptionRepository taskOptionRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository,
                       CourseRepository courseRepository,
                       TaskOptionRepository taskOptionRepository) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
        this.taskOptionRepository = taskOptionRepository;
    }

    @Transactional
    public TaskResponseDTO createOpenTextTask(NewTaskDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(TaskExceptions.CourseNotFoundException::new);

        validateTask(dto, course,Type.OPEN_TEXT);

        taskRepository.updateOrderForCourse(dto.getCourseId(), dto.getOrder());

        Task task = new Task(dto.getStatement(), dto.getOrder(), Type.OPEN_TEXT, course);
        taskRepository.save(task);

        return new TaskResponseDTO(task, Collections.emptyList());
    }

    @Transactional
    public TaskResponseDTO createSingleChoiceTask(NewTaskDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(TaskExceptions.CourseNotFoundException::new);

        validateTask(dto, course, Type.SINGLE_CHOICE);
        validateTaskOptions(dto, true); // true = single choice

        taskRepository.updateOrderForCourse(dto.getCourseId(), dto.getOrder());

        Task task = new Task(dto.getStatement(), dto.getOrder(), Type.SINGLE_CHOICE, course);
        task = taskRepository.save(task);

        createTaskOptions(task, dto.getOptions());

        List<TaskOption> options = taskOptionRepository.findByTask(task);

        return new TaskResponseDTO(task,options);
    }

    @Transactional
    public TaskResponseDTO createMultipleChoiceTask(NewTaskDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(TaskExceptions.CourseNotFoundException::new);

        validateTask(dto, course, Type.SINGLE_CHOICE);
        validateTaskOptions(dto, false);

        taskRepository.updateOrderForCourse(dto.getCourseId(), dto.getOrder());

        Task task = new Task(dto.getStatement(), dto.getOrder(), Type.MULTIPLE_CHOICE, course);
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

    private void validateTaskOptions(NewTaskDTO dto, boolean singleChoice) {
        List<NewTaskOptionDTO> options = dto.getOptions();

        if (options == null) {
            throw new TaskOptionExceptions.InvalidOptionsCountException();
        }

        validateOptionsCount(options.size(), singleChoice);
        validateCorrectAndIncorrectCount(options, singleChoice);
        validateOptionLength(options);
        validateRepeatedOptions(options);
        validateOptionsAgainstStatement(dto.getStatement(), options);

    }

    private void createTaskOptions(Task task, List<NewTaskOptionDTO> options) {
        List<TaskOption> taskOptions = options.stream()
                .map(o -> new TaskOption(o.getOption(), o.isCorrect(), task))
                .collect(Collectors.toList());
        taskOptionRepository.saveAll(taskOptions);
    }

    private void validateRepeatedOptions(List<NewTaskOptionDTO> options){
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

    private void validateOptionsAgainstStatement(String statement, List <NewTaskOptionDTO> options){
        boolean hasEqualOption = options.stream()
                .map(opt -> opt.getOption().trim().toLowerCase())
                .anyMatch(opt -> opt.equals(statement.trim().toLowerCase()));

        if (hasEqualOption) {
            throw new TaskOptionExceptions.OptionEqualsStatementException();
        }
    }

    private void validateOptionsCount(int size, boolean singleChoice) {
        if (singleChoice) {
            if (size < 2 || size > 5)
                throw new TaskOptionExceptions.InvalidOptionsCountException();
        } else {
            if (size < 3 || size > 5)
                throw new TaskOptionExceptions.InvalidOptionsCountException();
        }
    }

    private void validateCorrectAndIncorrectCount(List<NewTaskOptionDTO> options, boolean singleChoice) {
        long correctCount = options.stream().filter(NewTaskOptionDTO::isCorrect).count();

        if (singleChoice) {
            if (correctCount != 1)
                throw new TaskOptionExceptions.InvalidCorrectOptionException();
        } else {
            long incorrectCount = options.size() - correctCount;

            if (correctCount < 2 || incorrectCount < 1)
                throw new TaskOptionExceptions.InvalidCorrectOptionException();
        }
    }

    private void validateOptionLength(List<NewTaskOptionDTO> options) {
        for (NewTaskOptionDTO option : options) {
            int length = option.getOption().length();
            if (length < 4 || length > 80)
                throw new TaskOptionExceptions.InvalidOptionLengthException(option.getOption());
        }
    }
}

