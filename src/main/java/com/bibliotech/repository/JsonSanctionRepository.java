package com.bibliotech.repository;

import com.bibliotech.db.JsonDatabase;
import com.bibliotech.model.Sanction;
import java.util.UUID;

public class JsonSanctionRepository extends JsonRepository<Sanction, UUID> implements SanctionRepository {
    public JsonSanctionRepository(JsonDatabase db) {
        super(db, db::getSanctions);
    }
}
