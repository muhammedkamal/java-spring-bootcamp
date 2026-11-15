package com.flairs.taskapi.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.flairs.taskapi.repository.UserRepository;
import com.flairs.taskapi.security.CorsProperties;
import com.flairs.taskapi.security.SecurityConfig;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// NOTE: jwt() does NOT run our JwtAuthenticationConverter (roles -> ROLE_x), it reads the "scope" claim.
// So we pass the authorities ourselves with .authorities(...).
// Role rules: only ADMIN may list users. These tests break if someone deletes the URL rule
// AND the @PreAuthorize, which is exactly the mistake a test should catch.
@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
@EnableConfigurationProperties(CorsProperties.class)   // slices skip @ConfigurationPropertiesScan
class AdminControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean UserRepository users;
    @MockitoBean JwtDecoder jwtDecoder;

    @Test
    void aUserGets403() throws Exception {
        mvc.perform(get("/api/admin/users").with(jwt().jwt(j -> j.subject("2").claim("roles", List.of("USER"))).authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void anAdminGets200() throws Exception {
        mvc.perform(get("/api/admin/users").with(jwt().jwt(j -> j.subject("1").claim("roles", List.of("ADMIN"))).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());
    }
}
