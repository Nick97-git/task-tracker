package com.dev.tracker.controller;

import com.dev.tracker.model.dto.user.UserDeleteDto;
import com.dev.tracker.model.dto.user.UserGetRequestDto;
import com.dev.tracker.model.dto.user.UserGetResponseDto;
import com.dev.tracker.model.dto.user.UserPutDto;
import com.dev.tracker.model.dto.user.UserResponseDto;
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
import org.springframework.web.util.NestedServletException;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class UserControllerTest {
    private static final String USERS_ENDPOINT = "/users";
    private static final String EMAIL_ERROR = "Email can't be null or blank!";
    private static final ResultMatcher STATUS_200 = MockMvcResultMatchers.status().isOk();
    private static final ResultMatcher STATUS_400 = MockMvcResultMatchers.status().isBadRequest();
    private static final String GET_USER_ENDPOINT = "/users/user";
    private static final String DELETE_USER_ERROR = "You have no right to delete this user!";
    private UserDeleteDto userDeleteDto;
    private UserPutDto userPutDto;
    private UserGetRequestDto userGetRequestDto;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        setUserDeleteDto();
        setUserPutDto();
        setUserGetRequestDto();
    }

    @Test
    @WithMockUser(username = "email", password = "1234", roles = {"USER","ADMIN"})
    public void checkDeleteUserIsOk() {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete(USERS_ENDPOINT);
        getResponse(STATUS_200, userDeleteDto, builder);
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email15", password = "1234", roles = "USER")
    public void checkDeleteOtherUserWithoutAdminRole() {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete(USERS_ENDPOINT);
        String content = getResponse(STATUS_400, userDeleteDto, builder);
        Map<String, Object> map = objectMapper.readValue(content,
                new TypeReference<HashMap<String, Object>>() {
                });
        String errorMessage = (String) map.get("error");
        Assertions.assertEquals(DELETE_USER_ERROR, errorMessage);
    }

    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkIncorrectDeleteUserData() {
        userDeleteDto.setEmail("");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete(USERS_ENDPOINT);
        List<String> errors = getErrors(userDeleteDto, builder);
        Assertions.assertTrue(errors.contains(EMAIL_ERROR));
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkUpdateUserIsOk() {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(USERS_ENDPOINT);
        String content = getResponse(STATUS_200, userPutDto, builder);
        UserResponseDto expected = new UserResponseDto();
        expected.setEmail("new email");
        expected.setFirstName("first");
        expected.setLastName("new last name");
        UserResponseDto actual = objectMapper.readValue(content, UserResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkIncorrectUpdateUserData() {
        userPutDto.setEmail("");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put(USERS_ENDPOINT);
        List<String> errors = getErrors(userPutDto, builder);
        Assertions.assertTrue(errors.contains(EMAIL_ERROR));
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkGetUserDataIsOk() {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get(GET_USER_ENDPOINT);
        String content = getResponse(STATUS_200, userGetRequestDto, builder);
        UserGetResponseDto userGetResponseDto = objectMapper
                .readValue(content, UserGetResponseDto.class);
        Assertions.assertEquals(20, userGetResponseDto.getTasks().size());
        Assertions.assertEquals("first", userGetResponseDto.getFirstName());
        Assertions.assertEquals("last", userGetResponseDto.getLastName());
    }

    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkIncorrectGetUserData() {
        userGetRequestDto.setEmail("");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get(GET_USER_ENDPOINT);
        List<String> errors = getErrors(userGetRequestDto, builder);
        Assertions.assertTrue(errors.contains(EMAIL_ERROR));
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkGetUsersIsOk() {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(USERS_ENDPOINT)
                .param("offset", "1")
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(STATUS_200)
                .andReturn();
        String content = result.getResponse().getContentAsString();
        List<UserResponseDto> users = objectMapper.readValue(content,
                new TypeReference<>() {});
        Assertions.assertEquals(10, users.size());
        Assertions.assertEquals("email10", users.get(0).getEmail());
        Assertions.assertEquals("email19",
                users.get(users.size() - 1).getEmail());
    }

    @Test
    @WithMockUser(username = "email", password = "1234", roles = "USER")
    public void checkIncorrectLimitAndOffsetForGetUsers() {
        makeGetUsersRequest("0", "11");
        makeGetUsersRequest("0", "0");
        makeGetUsersRequest("-1", "10");
    }

    @SneakyThrows
    private void makeGetUsersRequest(String offset, String limit) {
        Assertions.assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders
                    .get(USERS_ENDPOINT)
                    .param("offset", offset)
                    .param("limit", limit)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
        });
    }

    @SneakyThrows
    private List<String> getErrors(Object object,
                                   MockHttpServletRequestBuilder builder) {
        String content = getResponse(STATUS_400,
                object, builder);
        Map<String, Object> map = objectMapper.readValue(content,
                new TypeReference<HashMap<String, Object>>() {
                });
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

    private void setUserGetRequestDto() {
        userGetRequestDto = new UserGetRequestDto();
        userGetRequestDto.setEmail("email");
    }

    private void setUserPutDto() {
        userPutDto = new UserPutDto();
        userPutDto.setEmail("email3");
        userPutDto.setNewEmail("new email");
        userPutDto.setLastName("new last name");
    }

    private void setUserDeleteDto() {
        userDeleteDto = new UserDeleteDto();
        userDeleteDto.setEmail("email2");
    }
}
