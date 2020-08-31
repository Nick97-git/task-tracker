package com.dev.tracker.security.jwt;

import com.dev.tracker.exception.InvalidJwtAuthenticationException;
import com.dev.tracker.model.dto.user.UserDeleteDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class JwtTokenProviderTest {
    private static final String USERS_ENDPOINT = "/users";
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    public void jwtAuthenticationIsOk() {
        UserDeleteDto userDeleteDto = new UserDeleteDto();
        userDeleteDto.setEmail("email@ukr.net");
        String json = objectMapper.writeValueAsString(userDeleteDto);
        String token = jwtTokenProvider
                .createToken("email@ukr.net", List.of("ADMIN", "USER"));
        mockMvc.perform(MockMvcRequestBuilders.delete(USERS_ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .content(json).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @SneakyThrows
    @Test
    public void jwtAuthenticationIsFailed() {
        UserDeleteDto userDeleteDto = new UserDeleteDto();
        userDeleteDto.setEmail("email@ukr.net");
        String json = objectMapper.writeValueAsString(userDeleteDto);
        Assertions.assertThrows(InvalidJwtAuthenticationException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.delete(USERS_ENDPOINT)
                    .header("Authorization", "Bearer " + "fake token")
                    .content(json).contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        });
    }
}
