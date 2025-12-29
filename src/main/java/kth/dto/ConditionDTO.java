package kth.dto;

import java.time.LocalDateTime;

public record ConditionDTO(
        Long id,
        String diagnosis,
        String description,
        String status,
        LocalDateTime diagnosisDate,
        String diagnosedBy
) {}
