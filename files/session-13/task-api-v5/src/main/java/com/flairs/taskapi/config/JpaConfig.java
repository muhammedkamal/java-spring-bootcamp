package com.flairs.taskapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// Turns on @CreatedDate and @LastModifiedDate: Spring fills them in when an entity is saved.
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
