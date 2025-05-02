package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.user.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private CourseRepository courseRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CourseService courseService;

    @Test
    void newCourseDTO__should_return_bad_request_when_email_is_invalid() throws Exception {

        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setTitle("Java");
        newCourseDTO.setDescription("Curso de Java");
        newCourseDTO.setEmailInstructor("paulo@alura.com.br");

        doReturn(Optional.empty()).when(userRepository)
                .findByEmail(newCourseDTO.getEmailInstructor());

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("emailInstructor"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }


    @Test
    void newCourseDTO__should_return_bad_request_when_email_is_no_instructor() throws Exception {

        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setTitle("Java");
        newCourseDTO.setDescription("Curso de Java");
        newCourseDTO.setEmailInstructor("paulo@alura.com.br");

        User user = mock(User.class);
        doReturn(false).when(user).isInstructor();

        doReturn(Optional.of(user)).when(userRepository)
                .findByEmail(newCourseDTO.getEmailInstructor());

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("emailInstructor"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void newCourseDTO__should_return_created_when_new_course_request_is_valid() throws Exception {

        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setTitle("Java");
        newCourseDTO.setDescription("Curso de Java");
        newCourseDTO.setEmailInstructor("paulo@alura.com.br");

        User user = mock(User.class);
        doReturn(true).when(user).isInstructor();

        doReturn(Optional.of(user)).when(userRepository).findByEmail(newCourseDTO.getEmailInstructor());

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isCreated());

        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void listAllCourses__should_list_all_courses() throws Exception {
        User paulo = new User("Paulo", "paulo@alua.com.br", Role.INSTRUCTOR);

        Course java = new Course("Java", "Curso de java", paulo);
        Course hibernate = new Course("Hibernate", "Curso de hibernate", paulo);
        Course spring = new Course("Spring", "Curso de spring", paulo);

        when(courseRepository.findAll()).thenReturn(Arrays.asList(java, hibernate, spring));

        mockMvc.perform(get("/course/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java"))
                .andExpect(jsonPath("$[0].description").value("Curso de java"))
                .andExpect(jsonPath("$[1].title").value("Hibernate"))
                .andExpect(jsonPath("$[1].description").value("Curso de hibernate"))
                .andExpect(jsonPath("$[2].title").value("Spring"))
                .andExpect(jsonPath("$[2].description").value("Curso de spring"));
    }

    @Test
    void publishCourse_should_return_bad_request_when_course_is_not_found() throws Exception {
        Long courseId = 1L;

        when(courseService.publishCourse(any()))
                .thenThrow(new CourseExceptions.NotFoundException());

        mockMvc.perform(post("/course/10/publish"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("id"))
                .andExpect(jsonPath("$.message").value("Curso não encontrado"));
    }

    @Test
    void publishCourse_should_return_bad_request_when_status_is_invalid() throws Exception {
        Long courseId = 2L;

        when(courseService.publishCourse(any()))
                .thenThrow(new CourseExceptions.InvalidStatusException());

        mockMvc.perform(post("/course/10/publish"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("status"))
                .andExpect(jsonPath("$.message").value("Curso não está com status BUILDING"));
    }

    @Test
    void publishCourse_should_return_bad_request_when_task_types_are_missing() throws Exception {
        Long courseId = 3L;

        when(courseService.publishCourse(any()))
                .thenThrow(new CourseExceptions.MissingTaskTypesException());

        mockMvc.perform(post("/course/10/publish"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("tasks"))
                .andExpect(jsonPath("$.message").value("Curso deve conter ao menos uma atividade de cada tipo"));
    }

    @Test
    void publishCourse_should_return_bad_request_when_task_order_is_invalid() throws Exception {
        Long courseId = 4L;

        when(courseService.publishCourse(any()))
                .thenThrow(new CourseExceptions.InvalidTaskOrderException());

        mockMvc.perform(post("/course/4/publish"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("order"))
                .andExpect(jsonPath("$.message").value("As atividades devem ter ordens contínuas (ex: 1, 2, 3...)"));
    }

    @Test
    void publishCourse_should_return_internal_server_error_when_unexpected_exception_occurs() throws Exception {
        Long courseId = 5L;

        when(courseService.publishCourse(any()))
                .thenThrow(new RuntimeException("Erro inesperado"));

        mockMvc.perform(post("/course/10/publish"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.field").value("N/A"))
                .andExpect(jsonPath("$.message").value("Erro inesperado"));
    }

    @Test
    void publishCourse_should_return_ok_when_course_is_published_successfully() throws Exception {
        Long courseId = 10L;

        Course publishedCourse = new Course();
        publishedCourse.setId(courseId);
        publishedCourse.setTitle("Curso de Spring Boot");
        publishedCourse.setDescription("Aprenda a criar APIs com Spring Boot");
        publishedCourse.setStatus(Status.PUBLISHED);
        publishedCourse.setCreatedAt(LocalDateTime.now());

        when(courseService.publishCourse(any())).thenReturn(publishedCourse);

        mockMvc.perform(post("/course/10/publish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(courseId))
                .andExpect(jsonPath("$.title").value("Curso de Spring Boot"))
                .andExpect(jsonPath("$.description").value("Aprenda a criar APIs com Spring Boot"))
                .andExpect(jsonPath("$.status").value("PUBLISHED"));
    }

}