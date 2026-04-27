package com.bibliotech.service;

import com.bibliotech.exception.*;
import com.bibliotech.model.*;
import com.bibliotech.repository.LoanRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LoanServiceImpl implements LoanService {
    private final ResourceService resourceService;
    private final MemberService memberService;
    private final LoanRepository loanRepository;
    private final LoanValidator loanValidator;
    private final LoanTrackerService loanTrackerService;

    public LoanServiceImpl(ResourceService resourceService, MemberService memberService, 
                           LoanRepository loanRepository, LoanValidator loanValidator,
                           LoanTrackerService loanTrackerService) {
        this.resourceService = resourceService;
        this.memberService = memberService;
        this.loanRepository = loanRepository;
        this.loanValidator = loanValidator;
        this.loanTrackerService = loanTrackerService;
    }

    @Override
    public Loan registerLoan(String isbn, String memberDni) throws LibraryException {
        Resource resource = resourceService.findByIsbn(isbn)
            .orElseThrow(() -> new EntityNotFoundException("Resource not found: " + isbn));
        
        Member member = memberService.findByDni(memberDni)
            .orElseThrow(() -> new EntityNotFoundException("Member not found: " + memberDni));

        List<Loan> activeLoans = loanRepository.findActiveByMemberDni(memberDni);
        
        loanValidator.validateNewLoan(isbn, member, activeLoans, loanRepository);

        Loan loan = new Loan(
            UUID.randomUUID(),
            isbn,
            memberDni,
            LocalDate.now(),
            LocalDate.now().plusDays(7),
            Optional.empty()
        );

        loanRepository.save(loan);
        return loan;
    }

    @Override
    public long returnResource(String isbn) throws LibraryException {
        Loan activeLoan = loanRepository.findActiveByIsbn(isbn).stream()
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("No active loan found for resource: " + isbn));

        long delay = loanTrackerService.getDelay(activeLoan);

        Loan updatedLoan = new Loan(
            activeLoan.id(),
            activeLoan.isbn(),
            activeLoan.memberDni(),
            activeLoan.loanDate(),
            activeLoan.dueDate(),
            Optional.of(LocalDate.now())
        );

        loanRepository.save(updatedLoan);
        return delay;
    }

    @Override
    public List<Loan> getHistory() {
        return loanRepository.findAll();
    }

    @Override
    public List<Loan> searchHistory(LoanSearchCriteria criteria) {
        return loanRepository.search(criteria);
    }
}
