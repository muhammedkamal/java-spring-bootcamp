package com.flairs.libraryapi;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

// The whole app on a real database, called like a client would. Named *IT: runs in "mvn verify".
// The tests share one database, so each one leaves the data as it found it (or uses data nobody else uses).
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class LoanFlowIT {

    @Autowired MockMvc mvc;

    // Session 13, TODO 8 (done)
    @Test
    void aMemberBorrowsAndReturnsABook() throws Exception {
        String sara = login("sara@example.com");

        String body = mvc.perform(post("/api/loans").header("Authorization", "Bearer " + sara)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"bookId\": 1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value(1))
                .andExpect(jsonPath("$.bookTitle").value("1984"))
                .andReturn().getResponse().getContentAsString();
        Integer loanId = JsonPath.read(body, "$.id");

        mvc.perform(patch("/api/loans/" + loanId + "/return").header("Authorization", "Bearer " + sara))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnedAt").exists());
        mvc.perform(patch("/api/loans/" + loanId + "/return").header("Authorization", "Bearer " + sara))
                .andExpect(status().isConflict());
    }

    // Session 13, TODO 9 (done)
    @Test
    void theLastCopyIsAlreadyOut() throws Exception {
        // "Animal Farm" has 1 copy and Sara has it (seed data)
        mvc.perform(post("/api/loans").header("Authorization", "Bearer " + login("omar@example.com"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"bookId\": 2}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("No copies of 'Animal Farm' are available"));
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
