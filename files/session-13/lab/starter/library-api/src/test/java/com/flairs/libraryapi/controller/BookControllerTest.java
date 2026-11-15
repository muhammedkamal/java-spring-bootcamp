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
import org.junit.jupiter.api.Disabled;
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

    // TODO 1
    @Test
    @Disabled("TODO 1")
    void aMemberCanReadABook() throws Exception {
        // Stub the service: when(service.findById(1L)).thenReturn(new BookResponse(...)).
        // GET /api/books/1 .with(member()) -> expect 200, $.title and $.authorName.
    }

    // TODO 2
    @Test
    @Disabled("TODO 2")
    void withoutATokenTheCatalogueIs401() throws Exception {
        // GET /api/books with NO .with(...) -> expect 401
        // and the header WWW-Authenticate: Bearer.
    }

    // TODO 3
    @Test
    @Disabled("TODO 3")
    void aMemberCannotAddABook() throws Exception {
        // POST /api/books .with(member()), JSON body VALID_BOOK -> expect 403.
        // Also verify(service, never()).create(any()): the controller was never reached.
    }

    // TODO 4
    @Test
    @Disabled("TODO 4")
    void aLibrarianAddsABook() throws Exception {
        // Stub service.create(any()) to return a BookResponse with id 7.
        // POST as librarian() -> expect 201 and Location: /api/books/7.
    }

    // TODO 5
    @Test
    @Disabled("TODO 5")
    void aBadIsbnIs400() throws Exception {
        // POST as librarian() with VALID_BOOK.replace("9780321125217", "123") -> expect 400
        // and $.errors.isbn = "isbn must be exactly 13 digits".
    }
}
