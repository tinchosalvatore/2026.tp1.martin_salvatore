package com.bibliotech.repository;

import com.bibliotech.db.JsonDatabase;
import com.bibliotech.model.Resource;
import com.bibliotech.model.SearchCriteria;
import java.util.List;
import java.util.stream.Collectors;

public class JsonResourceRepository extends JsonRepository<Resource, String> implements ResourceRepository {
    public JsonResourceRepository(JsonDatabase db) {
        super(db, db::getResources);
    }

    @Override
    public List<Resource> search(SearchCriteria criteria) {
        return findAll().stream()
            .filter(r -> criteria.title() == null || r.title().toLowerCase().contains(criteria.title().toLowerCase()))
            .filter(r -> criteria.author() == null || r.author().toLowerCase().contains(criteria.author().toLowerCase()))
            .filter(r -> criteria.category() == null || r.category() == criteria.category())
            .filter(r -> criteria.type() == null || criteria.type().isInstance(r))
            .collect(Collectors.toList());
    }
}
