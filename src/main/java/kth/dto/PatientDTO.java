package kth.dto;

import java.time.LocalDate;

public record PatientDTO(
        Long id,
        String firstName,
        String lastName,
        String socialSecurityNumber,
        LocalDate dateOfBirth
) {}
