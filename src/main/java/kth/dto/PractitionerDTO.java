package kth.dto;

public record PractitionerDTO(
        Long id,
        String firstName,
        String lastName,
        String type,
        String licenseNumber,
        String organization
) {}
