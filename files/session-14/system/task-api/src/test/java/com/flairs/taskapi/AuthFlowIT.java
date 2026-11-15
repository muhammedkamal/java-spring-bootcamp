package com.flairs.taskapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

// An INTEGRATION test: the whole application (real security, real JWT signing, real PostgreSQL in Docker).
// It checks that the pieces work together, end to end, like a client would use the API.
// Named *IT, so it runs in "mvn verify" (failsafe), not in the quick "mvn test".
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AuthFlowIT {

    @Autowired MockMvc mvc;

    @Test
    void loginThenUseTheToken() throws Exception {
        String token = login("sara@flairs.dev", "secret123").accessToken;

        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("sara@flairs.dev"));
        mvc.perform(get("/api/projects").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void saraCannotSeeOmarsProject() throws Exception {
        String token = login("sara@flairs.dev", "secret123").accessToken;

        mvc.perform(get("/api/projects/3").header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void aWrongPasswordIs401() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"sara@flairs.dev\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Invalid email, password or token"));
    }

    @Test
    void aTamperedTokenIs401() throws Exception {
        String token = login("sara@flairs.dev", "secret123").accessToken;
        String tampered = token.substring(0, token.length() - 3) + (token.endsWith("aaa") ? "bbb" : "aaa");

        mvc.perform(get("/api/tasks").header("Authorization", "Bearer " + tampered))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void aRefreshTokenWorksOnlyOnce() throws Exception {
        Tokens first = login("omar@flairs.dev", "secret123");

        Tokens second = refresh(first.refreshToken, 200);
        assertThat(second.refreshToken).isNotEqualTo(first.refreshToken);

        refresh(first.refreshToken, 401);    // reuse of an old token: everything of Omar is revoked
        refresh(second.refreshToken, 401);   // so the new one is dead too
    }

    record Tokens(String accessToken, String refreshToken) { }

    private Tokens login(String email, String password) throws Exception {
        MvcResult result = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return tokens(result);
    }

    private Tokens refresh(String refreshToken, int expectedStatus) throws Exception {
        MvcResult result = mvc.perform(post("/api/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().is(expectedStatus))
                .andReturn();
        return expectedStatus == 200 ? tokens(result) : null;
    }

    private static Tokens tokens(MvcResult result) throws Exception {
        String json = result.getResponse().getContentAsString();
        return new Tokens(JsonPath.read(json, "$.accessToken"), JsonPath.read(json, "$.refreshToken"));
    }
}
