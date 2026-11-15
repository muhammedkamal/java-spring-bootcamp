package com.flairs.taskapi.events;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// The Task API only PUBLISHES. It declares the exchange; each consumer declares and binds its own queue.
// So adding a new consumer never needs a change here.
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "tasks.events";

    @Bean
    TopicExchange taskEvents() {
        return new TopicExchange(EXCHANGE);     // durable, survives a RabbitMQ restart
    }

    // Messages as JSON instead of Java serialization: any language can read them
    @Bean
    MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
