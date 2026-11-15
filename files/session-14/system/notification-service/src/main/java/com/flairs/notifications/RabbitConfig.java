package com.flairs.notifications;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// The CONSUMER declares its own queue and binds it. RabbitMQ creates what is missing at startup.
//
//   tasks.events (topic) --"task.#"--> notifications.task-events --(rejected)--> notifications.dlx --> notifications.task-events.dlq
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "tasks.events";
    public static final String QUEUE = "notifications.task-events";
    public static final String DEAD_LETTER_EXCHANGE = "notifications.dlx";
    public static final String DEAD_LETTER_QUEUE = QUEUE + ".dlq";

    @Bean
    TopicExchange taskEvents() {
        return new TopicExchange(EXCHANGE);     // the same declaration as in the Task API: whoever starts first creates it
    }

    // durable: survives a broker restart. A message we reject goes to the dead letter exchange, not into the void.
    @Bean
    Queue taskEventsQueue() {
        return QueueBuilder.durable(QUEUE).deadLetterExchange(DEAD_LETTER_EXCHANGE).deadLetterRoutingKey(QUEUE).build();
    }

    @Bean
    Binding taskEventsBinding() {
        return BindingBuilder.bind(taskEventsQueue()).to(taskEvents()).with("task.#");   // task.created, task.completed, ...
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    // A human looks at the messages in here (RabbitMQ console), fixes the cause, and moves them back
    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    @Bean
    Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(QUEUE);
    }

    @Bean
    MessageConverter jsonMessageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        // The Task API puts its own class name (com.flairs.taskapi...) in a header. We do not have that class,
        // so convert to the type of the @RabbitListener parameter instead.
        converter.setAlwaysConvertToInferredType(true);
        return converter;
    }
}
