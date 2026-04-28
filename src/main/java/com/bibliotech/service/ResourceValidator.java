package com.bibliotech.service;

import com.bibliotech.exception.ValidationException;
import com.bibliotech.model.Resource;
import com.bibliotech.repository.ResourceRepository;

public interface ResourceValidator {
    void validate(Resource resource, ResourceRepository repository) throws ValidationException;
}
