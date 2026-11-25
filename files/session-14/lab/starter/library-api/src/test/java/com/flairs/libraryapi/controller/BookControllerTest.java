package com.flairs.libraryapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.flairs.libraryapi.dto.BookResponse;
import com.flairs.libraryapi.security.SecurityConfig;
import com.flairs.libraryapi.service.BookService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// Web slice: BookController + SecurityConfig + GlobalExceptionHandler. BookService is a mock.
@WebMvcTest(BookController.class)
@Import(SecurityConfig.class)
class BookControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean BookService service;
    @MockitoBean JwtDecoder jwtDecoder;

    static final String VALID_BOOK = """
            {"title": "Domain-Driven Design", "isbn": "9780321125217", "year": 2003, "copies": 2, "authorId": 3}
            """;

    // jwt() skips our roles converter, so we give the authority ourselves
    static JwtRequestPostProcessor member() {
        return jwt().jwt(j -> j.subject("1").claim("email", "sara@example.com").claim("roles", List.of("MEMBER")))
                .authorities(new SimpleGrantedAuthority("ROLE_MEMBER"));
    }

    static JwtRequestPostProcessor librarian() {
        return jwt().jwt(j -> j.subject("3").claim("email", "librarian@example.com").claim("roles", List.of("LIBRARIAN")))
                .authorities(new SimpleGrantedAuthority("ROLE_LIBRARIAN"));
    }

    // Session 13, TODO 1 (done)
    @Test
    void aMemberCanReadABook() throws Exception {
        when(service.findById(1L)).thenReturn(new BookResponse(1L, "1984", "9780451524935", 1949, 3, 1L, "George Orwell"));

        mvc.perform(get("/api/books/1").with(member()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("1984"))
                .andExpect(jsonPath("$.authorName").value("George Orwell"));
    }

    // Session 13, TODO 2 (done)
    @Test
    void withoutATokenTheCatalogueIs401() throws Exception {
        mvc.perform(get("/api/books"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", "Bearer"));
    }

    // Session 13, TODO 3 (done)
    @Test
    void aMemberCannotAddABook() throws Exception {
        mvc.perform(post("/api/books").with(member()).contentType(MediaType.APPLICATION_JSON).content(VALID_BOOK))
                .andExpect(status().isForbidden());
        verify(service, never()).create(any());
    }

    // Session 13, TODO 4 (done)
    @Test
    void aLibrarianAddsABook() throws Exception {
        when(service.create(any())).thenReturn(new BookResponse(7L, "Domain-Driven Design", "9780321125217", 2003, 2, 3L, "Martin Fowler"));

        mvc.perform(post("/api/books").with(librarian()).contentType(MediaType.APPLICATION_JSON).content(VALID_BOOK))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/books/7"));
    }

    // Session 13, TODO 5 (done)
    @Test
    void aBadIsbnIs400() throws Exception {
        mvc.perform(post("/api/books").with(librarian()).contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BOOK.replace("9780321125217", "123")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.isbn").value("isbn must be exactly 13 digits"));
    }
}
