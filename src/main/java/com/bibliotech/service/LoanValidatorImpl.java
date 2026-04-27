package com.bibliotech.service;

import com.bibliotech.exception.*;
import com.bibliotech.model.Loan;
import com.bibliotech.model.Member;
import com.bibliotech.repository.LoanRepository;
import java.time.LocalDate;
import java.util.List;

public class LoanValidatorImpl implements LoanValidator {
    private final SanctionService sanctionService;

    public LoanValidatorImpl(SanctionService sanctionService) {
        this.sanctionService = sanctionService;
    }

    @Override
    public void validateNewLoan(String isbn, Member member, List<Loan> activeLoans, LoanRepository repository) throws LibraryException {
        checkBookAvailability(isbn, repository);
        checkMemberQuota(member, activeLoans);
        checkOverdueLoans(activeLoans);
        checkSanctions(member);
    }

    private void checkBookAvailability(String isbn, LoanRepository repository) throws ResourceNotAvailableException {
        if (!repository.findActiveByIsbn(isbn).isEmpty()) {
            throw new ResourceNotAvailableException("Resource is already loaned: " + isbn);
        }
    }

    private void checkMemberQuota(Member member, List<Loan> activeLoans) throws MemberLimitExceededException {
        if (activeLoans.size() >= member.maxLoans()) {
            throw new MemberLimitExceededException("Member reached maximum loan limit: " + member.dni());
        }
    }

    private void checkOverdueLoans(List<Loan> activeLoans) throws ValidationException {
        LocalDate today = LocalDate.now();
        boolean hasOverdue = activeLoans.stream()
            .anyMatch(l -> l.dueDate().isBefore(today));
        
        if (hasOverdue) {
            throw new ValidationException("Member has overdue loans and cannot take new resources.");
        }
    }

    private void checkSanctions(Member member) throws ValidationException {
        if (sanctionService.isSanctioned(member.dni())) {
            throw new ValidationException("Member is currently sanctioned and cannot take new resources.");
        }
    }
}
