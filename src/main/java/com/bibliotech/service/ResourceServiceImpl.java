package com.bibliotech.service;

import com.bibliotech.exception.ValidationException;
import com.bibliotech.model.Resource;
import com.bibliotech.model.SearchCriteria;
import com.bibliotech.repository.ResourceRepository;
import java.util.List;
import java.util.Optional;

public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceValidator resourceValidator;

    public ResourceServiceImpl(ResourceRepository resourceRepository, ResourceValidator resourceValidator) {
        this.resourceRepository = resourceRepository;
        this.resourceValidator = resourceValidator;
    }

    @Override
    public void registerResource(Resource resource) throws ValidationException {
        resourceValidator.validate(resource, resourceRepository);
        resourceRepository.save(resource);
    }

    @Override
    public Optional<Resource> findByIsbn(String isbn) {
        return resourceRepository.findById(isbn);
    }

    @Override
    public List<Resource> search(SearchCriteria criteria) {
        return resourceRepository.search(criteria);
    }
}
