package com.flairs.taskapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.flairs.taskapi.config.CacheConfig;
import com.jayway.jsonpath.JsonPath;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// Session 14: the stats are stored in (real) Redis after the first call and forgotten after a change.
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class StatsCacheIT {

    @Autowired MockMvc mvc;
    @Autowired CacheManager cacheManager;

    Cache stats;
    String token;

    @BeforeEach
    void setUp() throws Exception {
        stats = cacheManager.getCache(CacheConfig.TASK_STATS);
        stats.clear();
        token = TestTokens.login(mvc, "sara@flairs.dev");
    }

    @Test
    void theFirstCallFillsTheCacheAndACreateEmptiesIt() throws Exception {
        assertThat(stats.get(2L)).isNull();

        mvc.perform(get("/api/tasks/stats").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
        assertThat(stats.get(2L)).as("Sara's stats are now in Redis under key 2").isNotNull();

        String created = mvc.perform(post("/api/tasks").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Cache test\",\"priority\":\"HIGH\",\"projectId\":1}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        // The eviction runs right after create() returns, but the delete in Redis can land a moment later
        // than our next read. Wait briefly instead of asserting on the exact millisecond.
        await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
                assertThat(stats.get(2L)).as("a new task changes the counts, so the entry is evicted").isNull());

        long id = ((Number) JsonPath.read(created, "$.id")).longValue();
        mvc.perform(delete("/api/tasks/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}
