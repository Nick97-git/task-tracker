package com.dev.tracker.controller;

import com.dev.tracker.model.dto.TaskPutResponseDto;
import com.dev.tracker.model.dto.task.TaskCreationDto;
import com.dev.tracker.model.dto.task.TaskPutDto;
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
public class TaskControllerTest {
    private static final String TASKS_ENDPOINT = "/tasks";
    private static final ResultMatcher STATUS_200 = MockMvcResultMatchers.status().isOk();
    private static final ResultMatcher STATUS_400 = MockMvcResultMatchers.status().isBadRequest();
    private static final String NO_SUCH_USER_ERROR = "There is no such user with email: %s!";
    private static final String USER_EMAIL_ERROR = "User email can't be null or blank!";
    private static final String EMAIL_ERROR = "Email can't be null or blank!";
    private static final String NEW_EMAIL_ERROR = "New email can't be null or blank!";
    private static final String TITLE_ERROR = "Title can't be null or blank!";
    private static final String DESCRIPTION_ERROR = "Description can't be null or blank!";
    private static final String STATUS_ERROR = "Status can't be null or blank!";
    private static final String NEW_STATUS_ERROR = "New status can't be null or blank!";
    private static final String CHANGE_TASK_STATUS_ENDPOINT = "/tasks/status";
    private static final String CHANGE_TASK_USER_ENDPOINT = "/tasks/user";
    private TaskCreationDto taskCreationDto;
    private TaskStatusChangeDto taskStatusChangeDto;
    private TaskUserChangeDto taskUserChangeDto;
    private TaskPutDto taskPutDto;
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
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkCreationOfTaskIsOk() {
        String json = objectMapper.writeValueAsString(taskCreationDto);
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
        taskCreationDto.setUserEmail("wrongEmail");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(TASKS_ENDPOINT);
        String content = getContent(STATUS_400, taskCreationDto, builder);
        Map<String,Object> map = objectMapper.readValue(content,
                new TypeReference<HashMap<String, Object>>(){});
        String errorMessage = (String) map.get("error");
        Assertions.assertEquals(errorMessage, String.format(NO_SUCH_USER_ERROR,
                taskCreationDto.getUserEmail()));
    }

    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkIncorrectCreationTaskData() {
        taskCreationDto.setUserEmail(null);
        taskCreationDto.setStatus(null);
        taskCreationDto.setDescription("");
        taskCreationDto.setTitle("");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(TASKS_ENDPOINT);
        List<String> errors = getErrors(taskCreationDto, builder);
        Assertions.assertTrue(errors.contains(USER_EMAIL_ERROR));
        Assertions.assertTrue(errors.contains(STATUS_ERROR));
        Assertions.assertTrue(errors.contains(TITLE_ERROR));
        Assertions.assertTrue(errors.contains(DESCRIPTION_ERROR));
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkTaskStatusChangeIsOk() {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(CHANGE_TASK_STATUS_ENDPOINT);
        String content = getContent(STATUS_200,
                taskStatusChangeDto, builder);
        TaskResponseDto expected = new TaskResponseDto();
        expected.setUserEmail("email");
        expected.setTitle("title9");
        expected.setStatus("Done");
        TaskResponseDto actual = objectMapper.readValue(content, TaskResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkIncorrectChangeTaskStatusData() {
        taskStatusChangeDto.setUserEmail(null);
        taskStatusChangeDto.setNewStatus("");
        taskStatusChangeDto.setTitle(null);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(CHANGE_TASK_STATUS_ENDPOINT);
        List<String> errors = getErrors(taskStatusChangeDto, builder);
        Assertions.assertTrue(errors.contains(USER_EMAIL_ERROR));
        Assertions.assertTrue(errors.contains(NEW_STATUS_ERROR));
        Assertions.assertTrue(errors.contains(TITLE_ERROR));
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkUpdateTaskIsOk() {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(TASKS_ENDPOINT);
        String content = getContent(STATUS_200,
                taskPutDto, builder);
        TaskPutResponseDto expected = new TaskPutResponseDto();
        expected.setUserEmail("email");
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
        taskPutDto.setUserEmail(null);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(TASKS_ENDPOINT);
        List<String> errors = getErrors(taskPutDto, builder);
        Assertions.assertTrue(errors.contains(USER_EMAIL_ERROR));
        Assertions.assertTrue(errors.contains(TITLE_ERROR));
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkTaskUserIsOk() {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(CHANGE_TASK_USER_ENDPOINT);
        String content = getContent(STATUS_200,
                taskUserChangeDto, builder);
        TaskResponseDto expected = new TaskResponseDto();
        expected.setUserEmail("email0");
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
        String content = getContent(STATUS_400,
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
        taskUserChangeDto.setCurrentEmail("");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(CHANGE_TASK_USER_ENDPOINT);
        List<String> errors = getErrors(taskUserChangeDto, builder);
        Assertions.assertTrue(errors.contains(EMAIL_ERROR));
        Assertions.assertTrue(errors.contains(NEW_EMAIL_ERROR));
        Assertions.assertTrue(errors.contains(TITLE_ERROR));
    }

    @SneakyThrows
    private List<String> getErrors(Object object,
                                   MockHttpServletRequestBuilder builder) {
        String content = getContent(STATUS_400,
                object, builder);
        Map<String,Object> map = objectMapper.readValue(content,
                new TypeReference<HashMap<String, Object>>(){});
        return ((ArrayList<String>) map.get("errors"));
    }

    @SneakyThrows
    private String getContent(ResultMatcher status, Object object,
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

    private void setTaskUserChangeDto() {
        taskUserChangeDto = new TaskUserChangeDto();
        taskUserChangeDto.setCurrentEmail("email");
        taskUserChangeDto.setNewEmail("email0");
        taskUserChangeDto.setTitle("title17");
    }

    private void setTaskPutDto() {
        taskPutDto = new TaskPutDto();
        taskPutDto.setCurrentTitle("title0");
        taskPutDto.setUserEmail("email");
        taskPutDto.setNewTitle("New title");
        taskPutDto.setNewDescription("New description");
    }

    private void setTaskCreationDto() {
        taskCreationDto = new TaskCreationDto();
        taskCreationDto.setTitle("Title");
        taskCreationDto.setDescription("Description");
        taskCreationDto.setStatus("Done");
        taskCreationDto.setUserEmail("email");
    }

    private void setTaskStatusChangeDto() {
        taskStatusChangeDto = new TaskStatusChangeDto();
        taskStatusChangeDto.setTitle("title9");
        taskStatusChangeDto.setNewStatus("Done");
        taskStatusChangeDto.setUserEmail("email");
    }
}
