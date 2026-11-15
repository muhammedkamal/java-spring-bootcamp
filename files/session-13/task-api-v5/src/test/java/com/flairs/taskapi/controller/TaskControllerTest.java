package com.flairs.taskapi.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.flairs.taskapi.dto.TaskResponse;
import com.flairs.taskapi.exception.NotFoundException;
import com.flairs.taskapi.model.Priority;
import com.flairs.taskapi.model.Role;
import com.flairs.taskapi.security.AuthUser;
import com.flairs.taskapi.security.CorsProperties;
import com.flairs.taskapi.security.SecurityConfig;
import com.flairs.taskapi.service.TaskService;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// A SLICE test: only the web layer starts (TaskController, GlobalExceptionHandler, our SecurityConfig).
// No database, no real service: TaskService is a Mockito mock. It checks the HTTP contract:
// URLs, status codes, JSON, validation and the security rules. Runs in about a second.
@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
@EnableConfigurationProperties(CorsProperties.class)   // slices skip @ConfigurationPropertiesScan
class TaskControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean TaskService service;
    @MockitoBean JwtDecoder jwtDecoder;      // SecurityConfig needs one; jwt() below skips it

    static final AuthUser SARA = new AuthUser(2L, "sara@flairs.dev", Role.USER);

    // jwt() puts a ready-made, already-verified token into the request: no signing, no secret needed
    static JwtRequestPostProcessor sara() {
        return jwt().jwt(j -> j.subject("2").claim("email", "sara@flairs.dev").claim("roles", java.util.List.of("USER")))
                .authorities(new SimpleGrantedAuthority("ROLE_USER"));   // jwt() skips our roles converter
    }

    static TaskResponse task(long id, String title) {
        Instant now = Instant.parse("2026-11-15T09:00:00Z");
        return new TaskResponse(id, title, null, Priority.HIGH, false, null, null, 1L, "Internship", Set.of(), now, now);
    }

    @Test
    void getReturnsTheTaskAsJson() throws Exception {
        when(service.findById(1L, SARA)).thenReturn(task(1, "Learn Spring"));

        mvc.perform(get("/api/tasks/1").with(sara()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Learn Spring"))
                .andExpect(jsonPath("$.projectName").value("Internship"));
    }

    @Test
    void withoutATokenTheAnswerIs401ProblemDetail() throws Exception {
        mvc.perform(get("/api/tasks/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", containsString("Bearer")))
                .andExpect(jsonPath("$.status").value(401));
        verifyNoInteractions(service);
    }

    @Test
    void aBrokenTokenIs401() throws Exception {
        when(jwtDecoder.decode("garbage")).thenThrow(new BadJwtException("bad signature"));

        mvc.perform(get("/api/tasks/1").header("Authorization", "Bearer garbage"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void someoneElsesTaskIs404() throws Exception {
        when(service.findById(99L, SARA)).thenThrow(new NotFoundException("Task", 99L));

        mvc.perform(get("/api/tasks/99").with(sara()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(containsString("99")));
    }

    @Test
    void createReturns201WithALocationHeader() throws Exception {
        when(service.create(any(), eq(SARA))).thenReturn(task(7, "Write tests"));

        mvc.perform(post("/api/tasks").with(sara())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Write tests", "priority": "HIGH", "projectId": 1}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/tasks/7"))
                .andExpect(jsonPath("$.id").value(7));
    }

    @Test
    void anInvalidBodyIs400WithTheFieldErrors() throws Exception {
        mvc.perform(post("/api/tasks").with(sara())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "", "projectId": -1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("title is required"))
                .andExpect(jsonPath("$.errors.priority").exists())
                .andExpect(jsonPath("$.errors.projectId").value("projectId must be positive"));
        verify(service, never()).create(any(), any());
    }
}
