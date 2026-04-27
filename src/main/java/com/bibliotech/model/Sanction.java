package com.bibliotech.model;

import java.time.LocalDate;
import java.util.UUID;

public record Sanction(
    UUID id,
    String memberDni,
    LocalDate releaseDate
) implements Identifiable<UUID> {}
