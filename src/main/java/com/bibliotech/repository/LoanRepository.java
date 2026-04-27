package com.bibliotech.repository;

import com.bibliotech.model.Loan;
import com.bibliotech.model.LoanSearchCriteria;
import java.util.List;
import java.util.UUID;

public interface LoanRepository extends Repository<Loan, UUID> {
    List<Loan> findActiveByIsbn(String isbn);
    List<Loan> findActiveByMemberDni(String dni);
    List<Loan> search(LoanSearchCriteria criteria);
}
