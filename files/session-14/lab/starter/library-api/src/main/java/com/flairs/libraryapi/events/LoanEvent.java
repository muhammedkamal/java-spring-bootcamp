package com.flairs.libraryapi.events;

import com.flairs.libraryapi.model.Loan;
import java.time.Clock;
import java.time.Instant;

// NEW in Session 14: what the Library API tells other services. A contract: add fields, never rename one.
public record LoanEvent(Type type, Long loanId, Long bookId, Long memberId, Instant occurredAt) {

    public enum Type { BORROWED, RETURNED }

    public static LoanEvent of(Type type, Loan loan, Clock clock) {
        return new LoanEvent(type, loan.getId(), loan.getBook().getId(), loan.getMember().getId(), Instant.now(clock));
    }

    // loan.borrowed, loan.returned: a consumer subscribes with "loan.#"
    public String routingKey() {
        return "loan." + type.name().toLowerCase();
    }
}
