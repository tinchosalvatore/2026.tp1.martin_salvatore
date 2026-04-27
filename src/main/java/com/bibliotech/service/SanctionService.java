package com.bibliotech.service;

import java.time.LocalDate;

public interface SanctionService extends LibraryService {
    void applySanction(String memberDni, long daysOfDelay);
    boolean isSanctioned(String memberDni);
}
