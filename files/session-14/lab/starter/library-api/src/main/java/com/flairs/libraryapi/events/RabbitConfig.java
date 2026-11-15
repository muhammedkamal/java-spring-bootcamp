package com.flairs.libraryapi.events;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// The Library API only publishes: it declares the exchange, each consumer brings its own queue
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "library.events";

    @Bean
    TopicExchange libraryEvents() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
