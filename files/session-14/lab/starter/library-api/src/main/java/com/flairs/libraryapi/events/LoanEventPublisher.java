package com.flairs.libraryapi.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

// Should run only AFTER the loan's transaction COMMITS, so a rolled-back borrow is never announced.
@Component
public class LoanEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LoanEventPublisher.class);

    private final RabbitTemplate rabbit;

    public LoanEventPublisher(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    // TODO 4: run this only after the transaction commits (one annotation), then send the event to
    // RabbitConfig.EXCHANGE with event.routingKey(). Catch AmqpException and log it: the loan is saved
    // already, so a broker problem must not fail the request.
    public void send(LoanEvent event) {
        log.debug("TODO 4: send {} for loan {}", event.routingKey(), event.loanId());
    }
}
