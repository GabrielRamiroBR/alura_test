package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.error.ErrorMessage;
import br.com.alura.AluraFake.exceptions.TaskExceptions;
import br.com.alura.AluraFake.exceptions.TaskOptionExceptions;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class TaskController {
    @Autowired
    TaskService taskService;


    @PostMapping("/task/new/opentext")
    public ResponseEntity<?> newOpenTextExercise(@RequestBody NewTaskDTO dto) {
        try {
            return ResponseEntity.ok().body(taskService.createOpenTextTask(dto));
        } catch (TaskExceptions ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ex.getErrorMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorMessage(null, ex.getMessage()));
        }
    }

    @PostMapping("/task/new/singlechoice")
    public ResponseEntity newSingleChoice(@RequestBody NewTaskDTO dto) {
        try {
            return ResponseEntity.ok(taskService.createSingleChoiceTask(dto));
        } catch (TaskExceptions ex) {
            return ResponseEntity.badRequest().body(ex.getErrorMessage());
        } catch (TaskOptionExceptions ex) {
            return ResponseEntity.badRequest().body(ex.getErrorMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorMessage(null, ex.getMessage()));
        }

    }

    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity newMultipleChoice() {
        return ResponseEntity.ok().build();
    }

}