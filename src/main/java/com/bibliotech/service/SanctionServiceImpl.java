package com.bibliotech.service;

import com.bibliotech.model.Sanction;
import com.bibliotech.repository.SanctionRepository;
import java.time.LocalDate;
import java.util.UUID;

public class SanctionServiceImpl implements SanctionService {
    private final SanctionRepository sanctionRepository;

    public SanctionServiceImpl(SanctionRepository sanctionRepository) {
        this.sanctionRepository = sanctionRepository;
    }

    @Override
    public void applySanction(String memberDni, long daysOfDelay) {
        LocalDate releaseDate = LocalDate.now().plusDays(daysOfDelay * 2);
        Sanction sanction = new Sanction(UUID.randomUUID(), memberDni, releaseDate);
        sanctionRepository.save(sanction);
    }

    @Override
    public boolean isSanctioned(String memberDni) {
        LocalDate today = LocalDate.now();
        return sanctionRepository.findAll().stream()
            .filter(s -> s.memberDni().equals(memberDni))
            .anyMatch(s -> s.releaseDate().isAfter(today));
    }
}
