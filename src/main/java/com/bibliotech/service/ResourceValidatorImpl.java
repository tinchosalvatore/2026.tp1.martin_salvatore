package com.bibliotech.service;

import com.bibliotech.exception.ValidationException;
import com.bibliotech.model.Resource;
import com.bibliotech.repository.ResourceRepository;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class ResourceValidatorImpl implements ResourceValidator {
    private static final Pattern ISBN_PATTERN = Pattern.compile("^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[0-9- ]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[0-9- ]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$");

    @Override
    public void validate(Resource resource, ResourceRepository repository) throws ValidationException {
        validateIsbn(resource.isbn());
        validateTitle(resource.title());
        validateAuthor(resource.author());
        validateYear(resource.year());
        validateUniqueness(resource.isbn(), repository);
    }

    private void validateIsbn(String isbn) throws ValidationException {
        if (isbn == null || !ISBN_PATTERN.matcher(isbn).matches()) {
            throw new ValidationException("Invalid ISBN format: " + isbn);
        }
    }

    private void validateTitle(String title) throws ValidationException {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Title cannot be empty.");
        }
    }

    private void validateAuthor(String author) throws ValidationException {
        if (author == null || author.trim().isEmpty()) {
            throw new ValidationException("Author cannot be empty.");
        }
    }

    private void validateYear(int year) throws ValidationException {
        int currentYear = LocalDate.now().getYear();
        if (year > currentYear) {
            throw new ValidationException("Year cannot be in the future.");
        }
        if (year < 0) {
            throw new ValidationException("Year cannot be negative.");
        }
    }

    private void validateUniqueness(String isbn, ResourceRepository repository) throws ValidationException {
        if (repository.findById(isbn).isPresent()) {
            throw new ValidationException("Resource with ISBN " + isbn + " already exists.");
        }
    }
}
