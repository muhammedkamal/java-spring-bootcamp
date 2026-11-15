package com.flairs.libraryapi.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

// NEW in Session 14: turns on @Cacheable and @CacheEvict, stored in Redis (spring.cache.* in application.yml).
// In its own class so slice tests (@WebMvcTest, @DataJpaTest) do not start caching.
// TODO 2: turn caching on with one annotation
@Configuration
public class CacheConfig {

    public static final String BOOKS = "books";
}
