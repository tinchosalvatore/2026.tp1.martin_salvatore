package com.bibliotech.repository;

import com.bibliotech.model.Sanction;
import java.util.UUID;

public class InMemorySanctionRepository extends InMemoryRepository<Sanction, UUID> implements SanctionRepository {
}
