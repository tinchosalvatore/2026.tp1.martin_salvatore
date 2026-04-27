package com.bibliotech.model;

public record LoanSearchCriteria(
    String memberDni,
    String isbn,
    Boolean isCompleted
) {}
