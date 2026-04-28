package com.bibliotech.service;

import com.bibliotech.exception.ValidationException;
import com.bibliotech.model.Member;
import com.bibliotech.repository.MemberRepository;
import java.util.regex.Pattern;

public class MemberValidatorImpl implements MemberValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern DNI_PATTERN = Pattern.compile("^\\d+$");

    @Override
    public void validate(Member member, MemberRepository repository) throws ValidationException {
        validateDniFormat(member.dni());
        validateName(member.name());
        validateEmail(member.email());
        validateDniUniqueness(member.dni(), repository);
    }

    private void validateDniFormat(String dni) throws ValidationException {
        if (!DNI_PATTERN.matcher(dni).matches()) {
            throw new ValidationException("DNI must contain only digits.");
        }
    }

    private void validateName(String name) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Member name cannot be empty.");
        }
    }

    private void validateEmail(String email) throws ValidationException {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Invalid email format: " + email);
        }
    }

    private void validateDniUniqueness(String dni, MemberRepository repository) throws ValidationException {
        if (repository.findById(dni).isPresent()) {
            throw new ValidationException("Member with DNI " + dni + " already exists.");
        }
    }
}
