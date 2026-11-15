package com.flairs.libraryapi.service;

import com.flairs.libraryapi.config.LibraryProperties;
import com.flairs.libraryapi.dto.BorrowRequest;
import com.flairs.libraryapi.dto.LoanResponse;
import com.flairs.libraryapi.dto.Mapper;
import com.flairs.libraryapi.exception.ConflictException;
import com.flairs.libraryapi.exception.NotFoundException;
import com.flairs.libraryapi.model.Book;
import com.flairs.libraryapi.model.Loan;
import com.flairs.libraryapi.model.Member;
import com.flairs.libraryapi.repository.BookRepository;
import com.flairs.libraryapi.repository.LoanRepository;
import com.flairs.libraryapi.repository.MemberRepository;
import java.time.Clock;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LoanService {

    private static final Logger log = LoggerFactory.getLogger(LoanService.class);

    private final LoanRepository loans;
    private final BookRepository books;
    private final MemberRepository members;
    private final LibraryProperties props;
    private final Clock clock;

    public LoanService(LoanRepository loans, BookRepository books, MemberRepository members,
                       LibraryProperties props, Clock clock) {
        this.loans = loans;
        this.books = books;
        this.members = members;
        this.props = props;
        this.clock = clock;
    }

    // TODO 5: borrow a book. Everything below runs in ONE transaction.
    //  1. load the book and the member (404 if missing)
    //  2. 409 if every copy is already out   (loans.countByBookIdAndReturnedAtIsNull >= book.getCopies())
    //  3. 409 if the member already has props.maxActiveLoans() active loans
    //  4. due date = today (use the clock) + props.loanDays()
    //  5. save the Loan, log it, return the response
    @Transactional
    public LoanResponse borrow(BorrowRequest request) {
        throw new UnsupportedOperationException("TODO 5");
    }

    // TODO 6: return a book: 404 if the loan is missing, 409 if it was already returned,
    // otherwise loan.markReturned(). Do you need loans.save(...)? Why not?
    @Transactional
    public LoanResponse giveBack(Long loanId) {
        throw new UnsupportedOperationException("TODO 6");
    }
}
