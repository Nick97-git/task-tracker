package com.dev.tracker.controller;

import com.dev.tracker.model.dto.user.UserLoginDto;
import com.dev.tracker.model.dto.user.UserRegistrationDto;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.NestedServletException;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class AuthenticationControllerTest {
    private static final String LOGIN_ENDPOINT = "/login";
    private static final String REGISTRATION_ENDPOINT = "/registration";
    private static final String EMAIL_ERROR = "Email can't be null or blank!";
    private static final String FIRST_NAME_ERROR = "First name can't be null or blank!";
    private static final String INCORRECT_EMAIL = "There is no such user with email: %s!";
    private static final String LAST_NAME_ERROR = "Last name can't be null or blank!";
    private static final String PASSWORDS_DONT_MATCH_ERROR = "Passwords don't match!";
    private static final String PASSWORD_ERROR = "Password can't be null or blank!";
    private static final String AUTHENTICATION_ERROR = "Incorrect username or password!";
    private static final ResultMatcher STATUS_200 = MockMvcResultMatchers.status().isOk();
    private static final ResultMatcher STATUS_400 = MockMvcResultMatchers.status().isBadRequest();
    private UserRegistrationDto userRegistrationDto;
    private UserLoginDto userLoginDto;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        setUserRegistrationDto();
        setUserLoginDto();
    }

    @SneakyThrows
    @Test
    public void checkRegistrationIsOk() {
        String json = objectMapper.writeValueAsString(userRegistrationDto);
        mockMvc.perform(MockMvcRequestBuilders.post(REGISTRATION_ENDPOINT)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(STATUS_200)
                .andReturn();
    }

    @Test
    public void checkPasswordsError() {
        userRegistrationDto.setPassword("1234567");
        List<String> errors = getErrors();
        Assertions.assertTrue(errors.contains(PASSWORDS_DONT_MATCH_ERROR));
    }

    @Test
    public void checkIncorrectRegistrationData() {
        userRegistrationDto.setEmail("");
        userRegistrationDto.setFirstName(null);
        userRegistrationDto.setLastName(null);
        List<String> errors = getErrors();
        Assertions.assertTrue(errors.contains(EMAIL_ERROR));
        Assertions.assertTrue(errors.contains(FIRST_NAME_ERROR));
        Assertions.assertTrue(errors.contains(LAST_NAME_ERROR));
    }

    @SneakyThrows
    @Test
    public void checkRegistrationWithEmptyRequestBody() {
        mockMvc.perform(MockMvcRequestBuilders.post(REGISTRATION_ENDPOINT)
                .content("")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(STATUS_400)
                .andReturn();
    }

    @SneakyThrows
    @Test
    public void registerNotUniqueEmail() {
        userRegistrationDto.setEmail("email");
        String json = objectMapper.writeValueAsString(userRegistrationDto);
        Assertions.assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.post(REGISTRATION_ENDPOINT)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
        });
    }

    @SneakyThrows
    @Test
    public void checkLoginSuccessful() {
        Map<String,Object> map = getMap(STATUS_200,
                userLoginDto, LOGIN_ENDPOINT);
        String token = (String) map.get("token");
        Assertions.assertFalse(token.isEmpty());
    }

    @SneakyThrows
    @Test
    public void checkIncorrectEmail() {
        userLoginDto.setEmail("wrongEmail");
        Map<String,Object> map = getMap(STATUS_400,
                userLoginDto, LOGIN_ENDPOINT);
        String message = (String) map.get("error");
        Assertions.assertEquals(String.format(INCORRECT_EMAIL,
                userLoginDto.getEmail()), message);
    }

    @Test
    public void checkIncorrectPassword() {
        userLoginDto.setPassword("wrong password");
        Map<String,Object> map = getMap(STATUS_400,
                userLoginDto, LOGIN_ENDPOINT);
        String message = (String) map.get("error");
        Assertions.assertEquals(AUTHENTICATION_ERROR, message);
    }

    @Test
    public void checkIncorrectLoginData() {
        userLoginDto.setEmail("");
        userLoginDto.setPassword(null);
        Map<String,Object> map = getMap(STATUS_400,
                userLoginDto, LOGIN_ENDPOINT);
        List<String> errors = (ArrayList<String>) map.get("errors");
        Assertions.assertTrue(errors.contains(EMAIL_ERROR));
        Assertions.assertTrue(errors.contains(PASSWORD_ERROR));
    }

    @SneakyThrows
    private List<String> getErrors() {
        Map<String,Object> map = getMap(STATUS_400,
                userRegistrationDto, REGISTRATION_ENDPOINT);
        return ((ArrayList<String>) map.get("errors"));
    }

    @SneakyThrows
    private Map<String, Object> getMap(ResultMatcher status, Object object, String endpoint) {
        String json = objectMapper.writeValueAsString(object);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status)
                .andReturn();
        String content = result.getResponse().getContentAsString();
        return objectMapper.readValue(content,
                new TypeReference<HashMap<String, Object>>(){});
    }

    private void setUserLoginDto() {
        userLoginDto = new UserLoginDto();
        userLoginDto.setEmail("email");
        userLoginDto.setPassword("1234");
    }

    private void setUserRegistrationDto() {
        userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setEmail("nickarchangel@gmail.com");
        userRegistrationDto.setFirstName("Nick");
        userRegistrationDto.setLastName("Archangel");
        userRegistrationDto.setPassword("12345678");
        userRegistrationDto.setRepeatPassword("12345678");
    }
}
