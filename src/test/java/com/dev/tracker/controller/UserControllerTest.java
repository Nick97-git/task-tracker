package com.dev.tracker.controller;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserControllerTest {
}
