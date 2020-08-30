package com.dev.tracker.controller;

import com.dev.tracker.model.dto.task.TaskCreateDto;
import com.dev.tracker.model.dto.task.TaskDeleteDto;
import com.dev.tracker.model.dto.task.TaskGetDto;
import com.dev.tracker.model.dto.task.TaskGetResponseDto;
import com.dev.tracker.model.dto.task.TaskResponseDto;
import com.dev.tracker.model.dto.task.TaskUpdateDto;
import com.dev.tracker.model.dto.task.TaskUpdateResponseDto;
import com.dev.tracker.model.dto.task.TaskUpdateStatusDto;
import com.dev.tracker.model.dto.task.TaskUpdateUserDto;
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
    private static final String CHANGE_TASK_STATUS_ENDPOINT = "/tasks/status";
    private static final String CHANGE_TASK_USER_ENDPOINT = "/tasks/user";
    private static final String TASKS_ENDPOINT = "/tasks";
    private static final String FIELD_ERROR = "%s can't be null or blank!";
    private static final String NO_SUCH_USER_ERROR = "There is no such user with email: %s!";
    private TaskDeleteDto taskDeleteDto;
    private TaskGetDto taskGetDto;
    private TaskCreateDto taskCreateDto;
    private TaskUpdateDto taskUpdateDto;
    private TaskUpdateStatusDto taskUpdateStatusDto;
    private TaskUpdateUserDto taskUpdateUserDto;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        setTaskDeleteDto();
        setTaskGetDto();
        setTaskPostDto();
        setTaskPutDto();
        setTaskUpdateStatusDto();
        setTaskUpdateUserDto();
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email@ukr.net", password = "1234", roles = "USER")
    public void checkCreateTaskIsOk() {
        String json = objectMapper.writeValueAsString(taskCreateDto);
        mockMvc.perform(MockMvcRequestBuilders.post(TASKS_ENDPOINT)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email@ukr.net", password = "1234", roles = "USER")
    public void checkCreateTaskWithNotExistentUser() {
        taskCreateDto.setEmail("wrong email");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(TASKS_ENDPOINT);
        String content = getContent(MockMvcResultMatchers.status().isBadRequest(),
                taskCreateDto, builder);
        Map<String,Object> map = objectMapper.readValue(content,
                new TypeReference<HashMap<String, Object>>(){});
        String errorMessage = (String) map.get("error");
        Assertions.assertEquals(errorMessage, String.format(NO_SUCH_USER_ERROR,
                taskCreateDto.getEmail()));
    }

    @Test
    @WithMockUser(username = "email@ukr.net", password = "1234", roles = "USER")
    public void checkIncorrectCreateTaskData() {
        taskCreateDto.setEmail(null);
        taskCreateDto.setStatus(null);
        taskCreateDto.setDescription("");
        taskCreateDto.setTitle("");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(TASKS_ENDPOINT);
        List<String> errors = getErrors(taskCreateDto, builder);
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Email")));
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Status")));
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Title")));
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Description")));
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email@ukr.net", password = "1234", roles = "USER")
    public void checkUpdateTaskStatusIsOk() {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(CHANGE_TASK_STATUS_ENDPOINT);
        String content = getContent(MockMvcResultMatchers.status().isOk(),
                taskUpdateStatusDto, builder);
        TaskResponseDto expected = new TaskResponseDto();
        expected.setEmail("email@ukr.net");
        expected.setTitle("title9");
        expected.setStatus("Done");
        TaskResponseDto actual = objectMapper.readValue(content, TaskResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "email@ukr.net", password = "1234", roles = "USER")
    public void checkIncorrectUpdateTaskStatusData() {
        taskUpdateStatusDto.setEmail(null);
        taskUpdateStatusDto.setNewStatus("");
        taskUpdateStatusDto.setTitle(null);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(CHANGE_TASK_STATUS_ENDPOINT);
        List<String> errors = getErrors(taskUpdateStatusDto, builder);
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Email")));
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "New status")));
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Title")));
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email@ukr.net", password = "1234", roles = "USER")
    public void checkUpdateTaskIsOk() {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(TASKS_ENDPOINT);
        String content = getContent(MockMvcResultMatchers.status().isOk(),
                taskUpdateDto, builder);
        TaskUpdateResponseDto expected = new TaskUpdateResponseDto();
        expected.setEmail("email@ukr.net");
        expected.setStatus("View");
        expected.setTitle("New title");
        expected.setDescription("New description");
        TaskUpdateResponseDto actual = objectMapper.readValue(content, TaskUpdateResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "email@ukr.net", password = "1234", roles = "USER")
    public void checkIncorrectUpdateTaskData() {
        taskUpdateDto.setTitle("");
        taskUpdateDto.setEmail(null);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(TASKS_ENDPOINT);
        List<String> errors = getErrors(taskUpdateDto, builder);
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Email")));
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Title")));
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email@ukr.net", password = "1234", roles = "USER")
    public void checkGetTasksIsOk() {
        changeUsersForSomeTasks();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get(TASKS_ENDPOINT);
        String content = getContent(MockMvcResultMatchers.status().isOk(),
                taskGetDto, builder);
        List<TaskGetResponseDto> tasks = objectMapper.readValue(content,
                new TypeReference<>() {
                });
        Assertions.assertEquals(7, tasks.size());
        Assertions.assertEquals("title10", tasks.get(0).getTitle());
        Assertions.assertEquals("title19", tasks.get(1).getTitle());
    }

    @Test
    @WithMockUser(username = "email@ukr.net", password = "1234", roles = "USER")
    public void checkIncorrectGetTasksData() {
        taskGetDto.setStatus(null);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get(TASKS_ENDPOINT);
        List<String> errors = getErrors(taskGetDto, builder);
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Status")));
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email@ukr.net", password = "1234", roles = "USER")
    public void checkDeleteTaskIsOk() {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete(TASKS_ENDPOINT);
        getContent(MockMvcResultMatchers.status().isOk(),
                taskDeleteDto, builder);
    }

    @Test
    @WithMockUser(username = "email@ukr.net", password = "1234", roles = "USER")
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
    @WithMockUser(username = "email@ukr.net", password = "1234", roles = "USER")
    public void checkUpdateTaskUserIsOk() {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(CHANGE_TASK_USER_ENDPOINT);
        String content = getContent(MockMvcResultMatchers.status().isOk(),
                taskUpdateUserDto, builder);
        TaskResponseDto expected = new TaskResponseDto();
        expected.setEmail("email0@ukr.net");
        expected.setTitle("title17");
        expected.setStatus("Done");
        TaskResponseDto actual = objectMapper.readValue(content, TaskResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email@ukr.net", password = "1234", roles = "USER")
    public void checkUpdateTaskUserIsFailed() {
        taskUpdateUserDto.setNewEmail("wrong email");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(CHANGE_TASK_USER_ENDPOINT);
        String content = getContent(MockMvcResultMatchers.status().isBadRequest(),
                taskUpdateUserDto, builder);
        Map<String,Object> map = objectMapper.readValue(content,
                new TypeReference<HashMap<String, Object>>(){});
        String errorMessage = (String) map.get("error");
        Assertions.assertEquals(String.format(NO_SUCH_USER_ERROR,
                taskUpdateUserDto.getNewEmail()), errorMessage);
    }

    @Test
    @WithMockUser(username = "email@ukr.net", password = "1234", roles = "USER")
    public void checkIncorrectUpdateTaskUserData() {
        taskUpdateUserDto.setTitle(null);
        taskUpdateUserDto.setNewEmail("");
        taskUpdateUserDto.setEmail("");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(CHANGE_TASK_USER_ENDPOINT);
        List<String> errors = getErrors(taskUpdateUserDto, builder);
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Email")));
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "New email")));
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Title")));
    }

    @SneakyThrows
    private List<String> getErrors(Object object,
                                   MockHttpServletRequestBuilder builder) {
        String content = getContent(MockMvcResultMatchers.status().isBadRequest(),
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

    private void changeUsersForSomeTasks() {
        taskUpdateUserDto.setNewEmail("email15@ukr.net");
        taskUpdateUserDto.setTitle("title10");
        makePutRequest();
        taskUpdateUserDto.setNewEmail("email5@ukr.net");
        taskUpdateUserDto.setTitle("title19");
        makePutRequest();
    }

    private void makePutRequest() {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(CHANGE_TASK_USER_ENDPOINT);
        getContent(MockMvcResultMatchers.status().isOk(), taskUpdateUserDto, builder);
    }

    private void setTaskGetDto() {
        taskGetDto = new TaskGetDto();
        taskGetDto.setStatus("In Progress");
    }

    private void setTaskDeleteDto() {
        taskDeleteDto = new TaskDeleteDto();
        taskDeleteDto.setTitle("title5");
        taskDeleteDto.setEmail("email@ukr.net");
    }

    private void setTaskPostDto() {
        taskCreateDto = new TaskCreateDto();
        taskCreateDto.setTitle("Title");
        taskCreateDto.setDescription("Description");
        taskCreateDto.setStatus("Done");
        taskCreateDto.setEmail("email@ukr.net");
    }

    private void setTaskPutDto() {
        taskUpdateDto = new TaskUpdateDto();
        taskUpdateDto.setTitle("title0");
        taskUpdateDto.setEmail("email@ukr.net");
        taskUpdateDto.setNewTitle("New title");
        taskUpdateDto.setNewDescription("New description");
    }

    private void setTaskUpdateStatusDto() {
        taskUpdateStatusDto = new TaskUpdateStatusDto();
        taskUpdateStatusDto.setTitle("title9");
        taskUpdateStatusDto.setNewStatus("Done");
        taskUpdateStatusDto.setEmail("email@ukr.net");
    }

    private void setTaskUpdateUserDto() {
        taskUpdateUserDto = new TaskUpdateUserDto();
        taskUpdateUserDto.setEmail("email@ukr.net");
        taskUpdateUserDto.setNewEmail("email0@ukr.net");
        taskUpdateUserDto.setTitle("title17");
    }
}
