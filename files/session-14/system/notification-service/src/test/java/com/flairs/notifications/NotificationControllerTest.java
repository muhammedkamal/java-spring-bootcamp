package com.flairs.notifications;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NotificationController.class)
@Import(SecurityConfig.class)
class NotificationControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean NotificationStore store;

    @Test
    void withoutATokenIs401() throws Exception {
        mvc.perform(get("/api/notifications")).andExpect(status().isUnauthorized());
    }

    @Test
    void theUserIdComesFromTheToken() throws Exception {
        when(store.forOwner(2L)).thenReturn(List.of(new Notification(5L, "Done: T", Instant.EPOCH)));

        mvc.perform(get("/api/notifications").with(jwt().jwt(j -> j.subject("2"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Done: T"));
    }
}
