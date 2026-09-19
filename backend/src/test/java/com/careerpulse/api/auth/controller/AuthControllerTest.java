package com.careerpulse.api.auth.controller;

import com.careerpulse.api.auth.dto.RegisterRequest;
import com.careerpulse.api.auth.dto.UserResponse;
import com.careerpulse.api.auth.exception.EmailAlreadyRegisteredException;
import com.careerpulse.api.auth.service.UserService;
import com.careerpulse.api.common.exception.GlobalExceptionHandler;
import com.careerpulse.api.user.entity.UserRole;
import com.careerpulse.api.user.entity.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldReturnCreatedWhenRegistrationIsValid()
            throws Exception {

        UserResponse response = new UserResponse(
                1L,
                "Buddhima Dishantha",
                "buddhima@example.com",
                UserRole.USER,
                UserStatus.ACTIVE
        );

        when(userService.register(any(RegisterRequest.class)))
                .thenReturn(response);

        String requestBody = """
                {
                  "fullName": "Buddhima Dishantha",
                  "email": "buddhima@example.com",
                  "password": "StrongPass123!"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(
                        jsonPath("$.fullName")
                                .value("Buddhima Dishantha")
                )
                .andExpect(
                        jsonPath("$.email")
                                .value("buddhima@example.com")
                )
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void shouldReturnBadRequestWhenRegistrationIsInvalid()
            throws Exception {

        String requestBody = """
                {
                  "fullName": "",
                  "email": "invalid-email",
                  "password": "123"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(
                        jsonPath("$.message")
                                .value("Request validation failed")
                )
                .andExpect(
                        jsonPath("$.path")
                                .value("/api/v1/auth/register")
                )
                .andExpect(
                        jsonPath("$.fieldErrors.fullName").exists()
                )
                .andExpect(
                        jsonPath("$.fieldErrors.email").exists()
                )
                .andExpect(
                        jsonPath("$.fieldErrors.password").exists()
                );

        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists()
            throws Exception {

        when(userService.register(any(RegisterRequest.class)))
                .thenThrow(
                        new EmailAlreadyRegisteredException()
                );

        String requestBody = """
                {
                  "fullName": "Buddhima Dishantha",
                  "email": "buddhima@example.com",
                  "password": "StrongPass123!"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(
                        jsonPath("$.message")
                                .value("Email is already registered")
                )
                .andExpect(
                        jsonPath("$.path")
                                .value("/api/v1/auth/register")
                );
    }
}