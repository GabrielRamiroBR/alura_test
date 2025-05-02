package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.*;
import br.com.alura.AluraFake.taskOption.NewTaskOptionDTO;
import br.com.alura.AluraFake.taskOption.TaskOptionExceptions;
import br.com.alura.AluraFake.taskOption.TaskOptionRepository;
import br.com.alura.AluraFake.user.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @MockBean
    private TaskService taskService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void newOpenText_should_return_created_when_request_is_valid() throws Exception {

        NewTaskDTO dto = new NewTaskDTO(1L,"Uma pergunta",1,null);


        TaskResponseDTO response = new TaskResponseDTO();
        response.setId(29L);
        response.setStatement("Uma boa pergunta");
        response.setOrder(1);
        response.setType(Type.OPEN_TEXT);
        response.setOptions(Collections.emptyList());

        when(taskService.createOpenTextTask(any())).thenReturn(response);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(29))
                .andExpect(jsonPath("$.statement").value("Uma boa pergunta"))
                .andExpect(jsonPath("$.order").value(1))
                .andExpect(jsonPath("$.type").value(Type.OPEN_TEXT.toString()))
                .andExpect(jsonPath("$.options").isEmpty());
    }

    @Test
    void newOpenText_should_return_bad_request_when_statement_is_duplicated_for_course() throws Exception {
        NewTaskDTO dto = new NewTaskDTO(1L, "Enunciado duplicado", 1, null);

        when(taskService.createOpenTextTask(any()))
                .thenThrow(new TaskExceptions.DuplicateStatementException());

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("statement"))
                .andExpect(jsonPath("$.message").value("Já existe uma atividade com esse enunciado para o curso"));
    }

    @Test
    void newOpenText_should_return_bad_request_when_statement_is_invalid() throws Exception {
        NewTaskDTO dto = new NewTaskDTO(1L, "abc", 1, null);

        when(taskService.createOpenTextTask(any()))
                .thenThrow(new TaskExceptions.InvalidStatementException());

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("statement"))
                .andExpect(jsonPath("$.message").value("O enunciado deve ter entre 4 e 255 caracteres."));
    }

    @Test
    void newOpenText_should_return_bad_request_when_task_order_is_invalid() throws Exception {
        NewTaskDTO dto = new NewTaskDTO(1L, "Enunciado válido", -5, null);

        when(taskService.createOpenTextTask(any()))
                .thenThrow(new TaskExceptions.InvalidTaskOrderException());

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("order"))
                .andExpect(jsonPath("$.message").value("Ordem da tarefa inválida"));
    }

    @Test
    void newOpenText_should_return_bad_request_when_course_status_is_invalid() throws Exception {
        NewTaskDTO dto = new NewTaskDTO(1L, "Pergunta válida", 1, null);

        when(taskService.createOpenTextTask(any()))
                .thenThrow(new TaskExceptions.InvalidCourseStatusException());

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("course.status"))
                .andExpect(jsonPath("$.message").value("Curso deve estar com status BUILDING para adicionar atividades"));
    }

    @Test
    void newOpenText_should_return_bad_request_when_course_does_not_exist() throws Exception {
        NewTaskDTO dto = new NewTaskDTO(null, "Enunciado qualquer", 1, null);

        when(taskService.createOpenTextTask(any()))
                .thenThrow(new TaskExceptions.CourseNotFoundException());

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("courseId"))
                .andExpect(jsonPath("$.message").value("Curso não encontrado"));
    }


    @Test
    void newOpenText_should_return_internal_server_error_when_unexpected_exception_occurs() throws Exception {
        NewTaskDTO dto = new NewTaskDTO(1L, "Uma pergunta válida", 1, null);

        when(taskService.createOpenTextTask(any()))
                .thenThrow(new RuntimeException("Erro inesperado"));

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.field").value("N/A"))
                .andExpect(jsonPath("$.message").value("Erro inesperado"));
    }


    @Test
    void newSingleChoice_should_return_created_when_request_is_valid() throws Exception {

        List <NewTaskOptionDTO> options = List.of(
                new NewTaskOptionDTO("Paris", true),
                new NewTaskOptionDTO("Londres", false),
                new NewTaskOptionDTO("Berlim", false)
        );
        NewTaskDTO dto = new NewTaskDTO(1L,"Qual a capital da França",1,options);


        TaskResponseDTO response = new TaskResponseDTO();
        response.setId(29L);
        response.setStatement("Qual a capital da França");
        response.setOrder(1);
        response.setType(Type.SINGLE_CHOICE);
        response.setOptions(options);

        when(taskService.createSingleChoiceTask(any())).thenReturn(response);

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(29))
                .andExpect(jsonPath("$.statement").value("Qual a capital da França"))
                .andExpect(jsonPath("$.order").value(1))
                .andExpect(jsonPath("$.type").value(Type.SINGLE_CHOICE.toString()))
                .andExpect(jsonPath("$.options[0].option").value("Paris"))
                .andExpect(jsonPath("$.options[0].isCorrect").value("true"))
                .andExpect(jsonPath("$.options[1].option").value("Londres"))
                .andExpect(jsonPath("$.options[1].isCorrect").value("false"))
                .andExpect(jsonPath("$.options[2].option").value("Berlim"))
                .andExpect(jsonPath("$.options[2].isCorrect").value("false"));
    }

    @Test
    void newSingleChoice_should_return_bad_request_when_statement_is_duplicated_for_course() throws Exception {
        List <NewTaskOptionDTO> options = List.of(
                new NewTaskOptionDTO("Paris", true),
                new NewTaskOptionDTO("Londres", false),
                new NewTaskOptionDTO("Berlim", false)
        );

        NewTaskDTO dto = new NewTaskDTO(1L, "Enunciado duplicado", 1, options);

        when(taskService.createSingleChoiceTask(any()))
                .thenThrow(new TaskExceptions.DuplicateStatementException());

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("statement"))
                .andExpect(jsonPath("$.message").value("Já existe uma atividade com esse enunciado para o curso"));
    }

    @Test
    void newSingleChoice_should_return_bad_request_when_statement_is_invalid() throws Exception {
        NewTaskDTO dto = new NewTaskDTO(1L, "abc", 1, null);

        when(taskService.createSingleChoiceTask(any()))
                .thenThrow(new TaskExceptions.InvalidStatementException());

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("statement"))
                .andExpect(jsonPath("$.message").value("O enunciado deve ter entre 4 e 255 caracteres."));
    }

    @Test
    void newSingleChoice_should_return_bad_request_when_task_order_is_invalid() throws Exception {
        NewTaskDTO dto = new NewTaskDTO(1L, "Enunciado válido", -5, null);

        when(taskService.createSingleChoiceTask(any()))
                .thenThrow(new TaskExceptions.InvalidTaskOrderException());

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("order"))
                .andExpect(jsonPath("$.message").value("Ordem da tarefa inválida"));
    }

    @Test
    void newSingleChoice_should_return_bad_request_when_course_status_is_invalid() throws Exception {
        NewTaskDTO dto = new NewTaskDTO(1L, "Pergunta válida", 1, null);

        when(taskService.createSingleChoiceTask(any()))
                .thenThrow(new TaskExceptions.InvalidCourseStatusException());

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("course.status"))
                .andExpect(jsonPath("$.message").value("Curso deve estar com status BUILDING para adicionar atividades"));
    }

    @Test
    void newSingleChoice_should_return_bad_request_when_course_does_not_exist() throws Exception {
        NewTaskDTO dto = new NewTaskDTO(null, "Enunciado qualquer", 1, null);

        when(taskService.createSingleChoiceTask(any()))
                .thenThrow(new TaskExceptions.CourseNotFoundException());

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("courseId"))
                .andExpect(jsonPath("$.message").value("Curso não encontrado"));
    }


    @Test
    void newSingleChoice_should_return_internal_server_error_when_unexpected_exception_occurs() throws Exception {
        NewTaskDTO dto = new NewTaskDTO(1L, "Uma pergunta válida", 1, null);

        when(taskService.createSingleChoiceTask(any()))
                .thenThrow(new RuntimeException("Erro inesperado"));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.field").value("N/A"))
                .andExpect(jsonPath("$.message").value("Erro inesperado"));
    }

    @Test
    void newSingleChoice_should_return_bad_request_when_invalid_options_count() throws Exception {
        NewTaskDTO dto = new NewTaskDTO(1L, "Pergunta com poucas opções", 1, new ArrayList<>());

        when(taskService.createSingleChoiceTask(any()))
                .thenThrow(new TaskOptionExceptions.InvalidOptionsCountException());

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("N/A"))
                .andExpect(jsonPath("$.message").value("Quantidade de alternativas inválida"));
    }

    @Test
    void newSingleChoice_should_return_bad_request_when_invalid_correct_option() throws Exception {
        List <NewTaskOptionDTO> options = List.of(
                new NewTaskOptionDTO("Paris", false),
                new NewTaskOptionDTO("Londres", false),
                new NewTaskOptionDTO("Berlim", false)
        );
        NewTaskDTO dto = new NewTaskDTO(1L, "Pergunta com erros nas corretas", 1, options);

        when(taskService.createSingleChoiceTask(any()))
                .thenThrow(new TaskOptionExceptions.InvalidCorrectOptionException());

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("isCorrect"))
                .andExpect(jsonPath("$.message").value("Quantidade de alternativas corretas ou incorretas inválida"));
    }

    @Test
    void newSingleChoice_should_return_bad_request_when_option_length_is_invalid() throws Exception {
        List <NewTaskOptionDTO> options = List.of(
                new NewTaskOptionDTO("Pa", false),
                new NewTaskOptionDTO("Londres", false),
                new NewTaskOptionDTO("Berlim", false)
        );
        NewTaskDTO dto = new NewTaskDTO(1L, "Pergunta", 1, options);

        when(taskService.createSingleChoiceTask(any()))
                .thenThrow(new TaskOptionExceptions.InvalidOptionLengthException("Op"));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("option"))
                .andExpect(jsonPath("$.message").value("Tamanho de altrnativa inválido:Op"));
    }
    @Test
    void newSingleChoice_should_return_bad_request_when_options_are_duplicated() throws Exception {
        List <NewTaskOptionDTO> options = List.of(
                new NewTaskOptionDTO("A", false),
                new NewTaskOptionDTO("A", true),
                new NewTaskOptionDTO("B", false),
                new NewTaskOptionDTO("B", false)
        );
        NewTaskDTO dto = new NewTaskDTO(1L, "Pergunta", 1, options);

        when(taskService.createSingleChoiceTask(any()))
                .thenThrow(new TaskOptionExceptions.DuplicateOptionException(List.of("A", "B")));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("options"))
                .andExpect(jsonPath("$.message").value("As seguintes opções estão duplicadas: A, B"));
    }

    @Test
    void newSingleChoice_should_return_bad_request_when_option_equals_statement() throws Exception {
        List <NewTaskOptionDTO> options = List.of(
                new NewTaskOptionDTO("Mesma opção", false),
                new NewTaskOptionDTO("A", true),
                new NewTaskOptionDTO("B", false)

        );
        NewTaskDTO dto = new NewTaskDTO(1L, "Mesma opção", 1, options);

        when(taskService.createSingleChoiceTask(any()))
                .thenThrow(new TaskOptionExceptions.OptionEqualsStatementException());

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("option"))
                .andExpect(jsonPath("$.message").value("Existe pelo menos uma alternativa igual ao enunciado"));
    }


    @Test
    void newMultipleChoice_should_return_created_when_request_is_valid() throws Exception {

        List <NewTaskOptionDTO> options = List.of(
                new NewTaskOptionDTO("Vermelho", true),
                new NewTaskOptionDTO("Branco", true),
                new NewTaskOptionDTO("Azul", false)
        );
        NewTaskDTO dto = new NewTaskDTO(1L,"Quais as cores da bandeira da polonia",1,options);


        TaskResponseDTO response = new TaskResponseDTO();
        response.setId(29L);
        response.setStatement("Quais as cores da bandeira da polonia");
        response.setOrder(1);
        response.setType(Type.MULTIPLE_CHOICE);
        response.setOptions(options);

        when(taskService.createMultipleChoiceTask(any())).thenReturn(response);

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(29))
                .andExpect(jsonPath("$.statement").value("Quais as cores da bandeira da polonia"))
                .andExpect(jsonPath("$.order").value(1))
                .andExpect(jsonPath("$.type").value(Type.MULTIPLE_CHOICE.toString()))
                .andExpect(jsonPath("$.options[0].option").value("Vermelho"))
                .andExpect(jsonPath("$.options[0].isCorrect").value("true"))
                .andExpect(jsonPath("$.options[1].option").value("Branco"))
                .andExpect(jsonPath("$.options[1].isCorrect").value("true"))
                .andExpect(jsonPath("$.options[2].option").value("Azul"))
                .andExpect(jsonPath("$.options[2].isCorrect").value("false"));
    }

    @Test
    void newMultipleChoice_should_return_bad_request_when_statement_is_duplicated_for_course() throws Exception {
        List <NewTaskOptionDTO> options = List.of(
                new NewTaskOptionDTO("Paris", true),
                new NewTaskOptionDTO("Londres", false),
                new NewTaskOptionDTO("Berlim", false)
        );

        NewTaskDTO dto = new NewTaskDTO(1L, "Enunciado duplicado", 1, options);

        when(taskService.createOpenTextTask(any()))
                .thenThrow(new TaskExceptions.DuplicateStatementException());

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("statement"))
                .andExpect(jsonPath("$.message").value("Já existe uma atividade com esse enunciado para o curso"));
    }

    @Test
    void newMultipleChoice_should_return_bad_request_when_statement_is_invalid() throws Exception {
        NewTaskDTO dto = new NewTaskDTO(1L, "abc", 1, null);

        when(taskService.createMultipleChoiceTask(any()))
                .thenThrow(new TaskExceptions.InvalidStatementException());

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("statement"))
                .andExpect(jsonPath("$.message").value("O enunciado deve ter entre 4 e 255 caracteres."));
    }

    @Test
    void newMultipleChoice_should_return_bad_request_when_task_order_is_invalid() throws Exception {
        NewTaskDTO dto = new NewTaskDTO(1L, "Enunciado válido", -5, null);

        when(taskService.createMultipleChoiceTask(any()))
                .thenThrow(new TaskExceptions.InvalidTaskOrderException());

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("order"))
                .andExpect(jsonPath("$.message").value("Ordem da tarefa inválida"));
    }

    @Test
    void newMultipleChoice_should_return_bad_request_when_course_status_is_invalid() throws Exception {
        NewTaskDTO dto = new NewTaskDTO(1L, "Pergunta válida", 1, null);

        when(taskService.createMultipleChoiceTask(any()))
                .thenThrow(new TaskExceptions.InvalidCourseStatusException());

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("course.status"))
                .andExpect(jsonPath("$.message").value("Curso deve estar com status BUILDING para adicionar atividades"));
    }

    @Test
    void newMultipoleChoice_should_return_bad_request_when_course_does_not_exist() throws Exception {
        NewTaskDTO dto = new NewTaskDTO(null, "Enunciado qualquer", 1, null);

        when(taskService.createMultipleChoiceTask(any()))
                .thenThrow(new TaskExceptions.CourseNotFoundException());

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("courseId"))
                .andExpect(jsonPath("$.message").value("Curso não encontrado"));
    }


    @Test
    void newMultipleChoice_should_return_internal_server_error_when_unexpected_exception_occurs() throws Exception {
        NewTaskDTO dto = new NewTaskDTO(1L, "Uma pergunta válida", 1, null);

        when(taskService.createMultipleChoiceTask(any()))
                .thenThrow(new RuntimeException("Erro inesperado"));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.field").value("N/A"))
                .andExpect(jsonPath("$.message").value("Erro inesperado"));
    }

    @Test
    void newMultipleChoice_should_return_bad_request_when_invalid_options_count() throws Exception {
        NewTaskDTO dto = new NewTaskDTO(1L, "Pergunta com poucas opções", 1, new ArrayList<>());

        when(taskService.createMultipleChoiceTask(any()))
                .thenThrow(new TaskOptionExceptions.InvalidOptionsCountException());

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("N/A"))
                .andExpect(jsonPath("$.message").value("Quantidade de alternativas inválida"));
    }

    @Test
    void newMultipleChoice_should_return_bad_request_when_invalid_correct_option() throws Exception {
        List <NewTaskOptionDTO> options = List.of(
                new NewTaskOptionDTO("Paris", false),
                new NewTaskOptionDTO("Londres", false),
                new NewTaskOptionDTO("Berlim", false)
        );
        NewTaskDTO dto = new NewTaskDTO(1L, "Pergunta com erros nas corretas", 1, options);

        when(taskService.createMultipleChoiceTask(any()))
                .thenThrow(new TaskOptionExceptions.InvalidCorrectOptionException());

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("isCorrect"))
                .andExpect(jsonPath("$.message").value("Quantidade de alternativas corretas ou incorretas inválida"));
    }

    @Test
    void newMultipleChoice_should_return_bad_request_when_option_length_is_invalid() throws Exception {
        List <NewTaskOptionDTO> options = List.of(
                new NewTaskOptionDTO("Pa", false),
                new NewTaskOptionDTO("Londres", false),
                new NewTaskOptionDTO("Berlim", false)
        );
        NewTaskDTO dto = new NewTaskDTO(1L, "Pergunta", 1, options);

        when(taskService.createMultipleChoiceTask(any()))
                .thenThrow(new TaskOptionExceptions.InvalidOptionLengthException("Op"));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("option"))
                .andExpect(jsonPath("$.message").value("Tamanho de altrnativa inválido:Op"));
    }
    @Test
    void newMultipleChoice_should_return_bad_request_when_options_are_duplicated() throws Exception {
        List <NewTaskOptionDTO> options = List.of(
                new NewTaskOptionDTO("A", false),
                new NewTaskOptionDTO("A", true),
                new NewTaskOptionDTO("B", false),
                new NewTaskOptionDTO("B", false)
        );
        NewTaskDTO dto = new NewTaskDTO(1L, "Pergunta", 1, options);

        when(taskService.createMultipleChoiceTask(any()))
                .thenThrow(new TaskOptionExceptions.DuplicateOptionException(List.of("A", "B")));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("options"))
                .andExpect(jsonPath("$.message").value("As seguintes opções estão duplicadas: A, B"));
    }

    @Test
    void newMultipleChoice_should_return_bad_request_when_option_equals_statement() throws Exception {
        List <NewTaskOptionDTO> options = List.of(
                new NewTaskOptionDTO("Mesma opção", false),
                new NewTaskOptionDTO("A", true),
                new NewTaskOptionDTO("B", false)

        );
        NewTaskDTO dto = new NewTaskDTO(1L, "Mesma opção", 1, options);

        when(taskService.createMultipleChoiceTask(any()))
                .thenThrow(new TaskOptionExceptions.OptionEqualsStatementException());

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("option"))
                .andExpect(jsonPath("$.message").value("Existe pelo menos uma alternativa igual ao enunciado"));
    }

}