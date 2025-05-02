package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.*;
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
    void newOpenTextExercise_should_return_created_when_request_is_valid() throws Exception {

        NewTaskDTO dto = new NewTaskDTO(1L,"Uma pergunta",1,null);


        TaskResponseDTO response = new TaskResponseDTO();
        response.setId(29L);
        response.setStatement("Uma boa pergunta");
        response.setOrder(1);
        response.setType("OPEN_TEXT");
        response.setOptions(Collections.emptyList());

        when(taskService.createOpenTextTask(any())).thenReturn(response);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(29))
                .andExpect(jsonPath("$.statement").value("Uma boa pergunta"))
                .andExpect(jsonPath("$.order").value(1))
                .andExpect(jsonPath("$.type").value("OPEN_TEXT"))
                .andExpect(jsonPath("$.options").isEmpty());
    }
}