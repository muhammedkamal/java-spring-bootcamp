package com.flairs.taskapi.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

// NEW in Session 14: turns on @Cacheable and @CacheEvict. Spring Boot sees Redis on the classpath and
// stores the cache there (settings in application.yml: spring.cache.*). In its own class, not on
// TaskApiApplication, so slice tests (@WebMvcTest, @DataJpaTest) do not start caching.
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String TASK_STATS = "task-stats";
}
