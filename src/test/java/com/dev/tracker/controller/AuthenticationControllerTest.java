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
import org.springframework.test.annotation.DirtiesContext;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class AuthenticationControllerTest {
    private static final String LOGIN_ENDPOINT = "/login";
    private static final String REGISTRATION_ENDPOINT = "/registration";
    private static final String AUTHENTICATION_ERROR = "Incorrect username or password!";
    private static final String EMAIL_ERROR = "Invalid email!";
    private static final String FIELD_ERROR = "%s can't be null or blank!";
    private static final String PASSWORDS_DONT_MATCH_ERROR = "Passwords don't match!";
    private static final String USER_NOT_FOUND_ERROR = "There is no such user with email: %s!";
    private UserLoginDto userLoginDto;
    private UserRegistrationDto userRegistrationDto;
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
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    public void getEmailError() {
        userRegistrationDto.setEmail("email");
        List<String> errors = getErrors();
        Assertions.assertTrue(errors.contains(EMAIL_ERROR));
    }

    @Test
    public void getPasswordsError() {
        userRegistrationDto.setPassword("1234567");
        List<String> errors = getErrors();
        Assertions.assertTrue(errors.contains(PASSWORDS_DONT_MATCH_ERROR));
    }

    @Test
    public void checkIncorrectRegistrationData() {
        userRegistrationDto.setFirstName(null);
        userRegistrationDto.setLastName(null);
        List<String> errors = getErrors();
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "First name")));
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Last name")));
    }

    @SneakyThrows
    @Test
    public void registerNotUniqueEmail() {
        userRegistrationDto.setEmail("email@ukr.net");
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
    public void checkLoginIsOk() {
        Map<String,Object> map = getContentAsMap(MockMvcResultMatchers.status().isOk(),
                userLoginDto, LOGIN_ENDPOINT);
        String token = (String) map.get("token");
        Assertions.assertFalse(token.isEmpty());
    }

    @SneakyThrows
    @Test
    public void checkLoginWithWrongEmail() {
        userLoginDto.setEmail("wrong email");
        Map<String,Object> map = getContentAsMap(MockMvcResultMatchers.status().isBadRequest(),
                userLoginDto, LOGIN_ENDPOINT);
        String errorMessage = (String) map.get("error");
        Assertions.assertEquals(String.format(USER_NOT_FOUND_ERROR,
                userLoginDto.getEmail()), errorMessage);
    }

    @Test
    public void checkLoginWithWrongPassword() {
        userLoginDto.setPassword("wrong password");
        Map<String,Object> map = getContentAsMap(MockMvcResultMatchers.status().isBadRequest(),
                userLoginDto, LOGIN_ENDPOINT);
        String message = (String) map.get("error");
        Assertions.assertEquals(AUTHENTICATION_ERROR, message);
    }

    @Test
    public void checkIncorrectLoginData() {
        userLoginDto.setEmail("");
        userLoginDto.setPassword(null);
        Map<String,Object> map = getContentAsMap(MockMvcResultMatchers.status().isBadRequest(),
                userLoginDto, LOGIN_ENDPOINT);
        List<String> errors = (ArrayList<String>) map.get("errors");
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Email")));
        Assertions.assertTrue(errors.contains(String.format(FIELD_ERROR, "Password")));
    }

    @SneakyThrows
    private List<String> getErrors() {
        Map<String,Object> map = getContentAsMap(MockMvcResultMatchers.status().isBadRequest(),
                userRegistrationDto, REGISTRATION_ENDPOINT);
        return ((ArrayList<String>) map.get("errors"));
    }

    @SneakyThrows
    private Map<String, Object> getContentAsMap(ResultMatcher status,
                                                Object object, String endpoint) {
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
        userLoginDto.setEmail("email@ukr.net");
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
