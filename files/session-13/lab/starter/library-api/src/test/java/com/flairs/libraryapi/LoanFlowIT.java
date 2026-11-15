package com.flairs.libraryapi;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// The whole app on a real database, called like a client would. Named *IT: runs in "mvn verify".
// The tests share one database, so each one leaves the data as it found it (or uses data nobody else uses).
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class LoanFlowIT {

    @Autowired MockMvc mvc;

    // TODO 8
    @Test
    @Disabled("TODO 8")
    void aMemberBorrowsAndReturnsABook() throws Exception {
        // login("sara@example.com"), POST /api/loans {"bookId": 1} -> 201, $.memberId = 1.
        // Read the id with JsonPath.read(body, "$.id"), PATCH /api/loans/{id}/return -> 200, again -> 409.
    }

    // TODO 9
    @Test
    @Disabled("TODO 9")
    void theLastCopyIsAlreadyOut() throws Exception {
        // Book 2 (Animal Farm) has 1 copy and Sara already has it.
        // Omar borrows it -> 409 and $.detail = "No copies of 'Animal Farm' are available".
    }

    @Test
    void aMemberCannotBorrowForSomeoneElse() throws Exception {
        mvc.perform(post("/api/loans").header("Authorization", "Bearer " + login("sara@example.com"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"bookId\": 3, \"memberId\": 2}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void aMemberCannotListMembersButALibrarianCan() throws Exception {
        mvc.perform(get("/api/members").header("Authorization", "Bearer " + login("sara@example.com")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/members").header("Authorization", "Bearer " + login("librarian@example.com")))
                .andExpect(status().isOk());
    }

    private String login(String email) throws Exception {
        String json = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(json, "$.accessToken");
    }
}
