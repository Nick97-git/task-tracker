package com.dev.tracker.controller;

import com.dev.tracker.model.dto.task.TaskDeleteDto;
import com.dev.tracker.model.dto.task.TaskGetRequestDto;
import com.dev.tracker.model.dto.task.TaskGetResponseDto;
import com.dev.tracker.model.dto.task.TaskPostDto;
import com.dev.tracker.model.dto.task.TaskPutDto;
import com.dev.tracker.model.dto.task.TaskPutResponseDto;
import com.dev.tracker.model.dto.task.TaskResponseDto;
import com.dev.tracker.model.dto.task.TaskStatusChangeDto;
import com.dev.tracker.model.dto.task.TaskUserChangeDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class TaskControllerTest {
    private static final String TASKS_ENDPOINT = "/tasks";
    private static final String CHANGE_TASK_STATUS_ENDPOINT = "/tasks/status";
    private static final String CHANGE_TASK_USER_ENDPOINT = "/tasks/user";
    private static final String FIELD_ERROR = "%s can't be null or blank!";
    private static final String NO_SUCH_USER_ERROR = "There is no such user with email: %s!";
    private static final ResultMatcher STATUS_200 = MockMvcResultMatchers.status().isOk();
    private static final ResultMatcher STATUS_400 = MockMvcResultMatchers.status().isBadRequest();
    private TaskPostDto taskPostDto;
    private TaskStatusChangeDto taskStatusChangeDto;
    private TaskUserChangeDto taskUserChangeDto;
    private TaskPutDto taskPutDto;
    private TaskGetRequestDto taskGetRequestDto;
    private TaskDeleteDto taskDeleteDto;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        setTaskCreationDto();
        setTaskStatusChangeDto();
        setTaskUserChangeDto();
        setTaskPutDto();
        setTaskGetRequestDto();
        setTaskDeleteDto();
    }

    private void setTaskDeleteDto() {
        taskDeleteDto = new TaskDeleteDto();
        taskDeleteDto.setTitle("title5");
        taskDeleteDto.setEmail("email");
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkCreationOfTaskIsOk() {
        String json = objectMapper.writeValueAsString(taskPostDto);
        mockMvc.perform(MockMvcRequestBuilders.post(TASKS_ENDPOINT)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(STATUS_200)
                .andReturn();
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkCreationOfTaskWithNotExistentUser() {
        taskPostDto.setEmail("wrongEmail");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(TASKS_ENDPOINT);
        String content = getResponse(STATUS_400, taskPostDto, builder);
        Map<String,Object> map = objectMapper.readValue(content,
                new TypeReference<HashMap<String, Object>>(){});
        String errorMessage = (String) map.get("error");
        Assertions.assertEquals(errorMessage, String.format(NO_SUCH_USER_ERROR,
                taskPostDto.getEmail()));
    }

    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkIncorrectCreationTaskData() {
        taskPostDto.setEmail(null);
        taskPostDto.setStatus(null);
        taskPostDto.setDescription("");
        taskPostDto.setTitle("");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(TASKS_ENDPOINT);
        List<String> errors = getErrors(taskPostDto, builder);
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Email")));
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Status")));
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Title")));
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Description")));
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkTaskStatusChangeIsOk() {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(CHANGE_TASK_STATUS_ENDPOINT);
        String content = getResponse(STATUS_200,
                taskStatusChangeDto, builder);
        TaskResponseDto expected = new TaskResponseDto();
        expected.setEmail("email");
        expected.setTitle("title9");
        expected.setStatus("Done");
        TaskResponseDto actual = objectMapper.readValue(content, TaskResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkIncorrectChangeTaskStatusData() {
        taskStatusChangeDto.setEmail(null);
        taskStatusChangeDto.setNewStatus("");
        taskStatusChangeDto.setTitle(null);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(CHANGE_TASK_STATUS_ENDPOINT);
        List<String> errors = getErrors(taskStatusChangeDto, builder);
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Email")));
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "New status")));
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Title")));
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkUpdateTaskIsOk() {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(TASKS_ENDPOINT);
        String content = getResponse(STATUS_200,
                taskPutDto, builder);
        TaskPutResponseDto expected = new TaskPutResponseDto();
        expected.setEmail("email");
        expected.setStatus("View");
        expected.setTitle("New title");
        expected.setDescription("New description");
        TaskPutResponseDto actual = objectMapper.readValue(content, TaskPutResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkIncorrectUpdateTaskData() {
        taskPutDto.setCurrentTitle("");
        taskPutDto.setEmail(null);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(TASKS_ENDPOINT);
        List<String> errors = getErrors(taskPutDto, builder);
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Email")));
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Title")));
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkGetTasksIsOk() {
        changeUsersForSomeTasks();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get(TASKS_ENDPOINT);
        String content = getResponse(STATUS_200,
                taskGetRequestDto, builder);
        List<TaskGetResponseDto> tasks = objectMapper.readValue(content,
                new TypeReference<>() {
                });
        Assertions.assertEquals(7, tasks.size());
        Assertions.assertEquals("title10", tasks.get(0).getTitle());
        Assertions.assertEquals("title19", tasks.get(1).getTitle());
    }

    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkIncorrectGetRequestTaskData() {
        taskGetRequestDto.setStatus(null);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get(TASKS_ENDPOINT);
        List<String> errors = getErrors(taskGetRequestDto, builder);
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Status")));
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkDeleteTasksIsOk() {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete(TASKS_ENDPOINT);
        getResponse(STATUS_200,
                taskDeleteDto, builder);
    }

    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkIncorrectDeleteTaskData() {
        taskDeleteDto.setEmail("");
        taskDeleteDto.setTitle(null);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete(TASKS_ENDPOINT);
        List<String> errors = getErrors(taskDeleteDto, builder);
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Email")));
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Title")));
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkTaskUserIsOk() {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(CHANGE_TASK_USER_ENDPOINT);
        String content = getResponse(STATUS_200,
                taskUserChangeDto, builder);
        TaskResponseDto expected = new TaskResponseDto();
        expected.setEmail("email0");
        expected.setTitle("title17");
        expected.setStatus("Done");
        TaskResponseDto actual = objectMapper.readValue(content, TaskResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkTaskUserIsFailed() {
        taskUserChangeDto.setNewEmail("wrong email");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(CHANGE_TASK_USER_ENDPOINT);
        String content = getResponse(STATUS_400,
                taskUserChangeDto, builder);
        Map<String,Object> map = objectMapper.readValue(content,
                new TypeReference<HashMap<String, Object>>(){});
        String errorMessage = (String) map.get("error");
        Assertions.assertEquals(String.format(NO_SUCH_USER_ERROR,
                taskUserChangeDto.getNewEmail()), errorMessage);
    }

    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkIncorrectChangeTaskUserData() {
        taskUserChangeDto.setTitle(null);
        taskUserChangeDto.setNewEmail("");
        taskUserChangeDto.setEmail("");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(CHANGE_TASK_USER_ENDPOINT);
        List<String> errors = getErrors(taskUserChangeDto, builder);
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Email")));
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "New email")));
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Title")));
    }

    @SneakyThrows
    private List<String> getErrors(Object object,
                                   MockHttpServletRequestBuilder builder) {
        String content = getResponse(STATUS_400,
                object, builder);
        Map<String,Object> map = objectMapper.readValue(content,
                new TypeReference<HashMap<String, Object>>(){});
        return ((ArrayList<String>) map.get("errors"));
    }

    @SneakyThrows
    private String getResponse(ResultMatcher status, Object object,
                               MockHttpServletRequestBuilder builder) {
        String json = objectMapper.writeValueAsString(object);
        MvcResult result = mockMvc.perform(builder
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status)
                .andReturn();
        return result.getResponse().getContentAsString();
    }

    private void changeUsersForSomeTasks() {
        taskUserChangeDto.setNewEmail("email15");
        taskUserChangeDto.setTitle("title10");
        makePutRequest();
        taskUserChangeDto.setNewEmail("email5");
        taskUserChangeDto.setTitle("title19");
        makePutRequest();
    }

    private void makePutRequest() {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(CHANGE_TASK_USER_ENDPOINT);
        getResponse(STATUS_200, taskUserChangeDto, builder);
    }

    private void setTaskUserChangeDto() {
        taskUserChangeDto = new TaskUserChangeDto();
        taskUserChangeDto.setEmail("email");
        taskUserChangeDto.setNewEmail("email0");
        taskUserChangeDto.setTitle("title17");
    }

    private void setTaskPutDto() {
        taskPutDto = new TaskPutDto();
        taskPutDto.setCurrentTitle("title0");
        taskPutDto.setEmail("email");
        taskPutDto.setNewTitle("New title");
        taskPutDto.setNewDescription("New description");
    }

    private void setTaskCreationDto() {
        taskPostDto = new TaskPostDto();
        taskPostDto.setTitle("Title");
        taskPostDto.setDescription("Description");
        taskPostDto.setStatus("Done");
        taskPostDto.setEmail("email");
    }

    private void setTaskStatusChangeDto() {
        taskStatusChangeDto = new TaskStatusChangeDto();
        taskStatusChangeDto.setTitle("title9");
        taskStatusChangeDto.setNewStatus("Done");
        taskStatusChangeDto.setEmail("email");
    }

    private void setTaskGetRequestDto() {
        taskGetRequestDto = new TaskGetRequestDto();
        taskGetRequestDto.setStatus("In Progress");
    }
}
