package kth.dto;

import java.time.LocalDateTime;

public record EncounterDTO(
        Long id,
        LocalDateTime encounterDate,
        String notes,
        String patientName,
        String diagnosis
) {}
