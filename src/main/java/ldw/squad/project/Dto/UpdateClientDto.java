package ldw.squad.project.Dto;

public record UpdateClientDto(
        String name,
        String email,
        String address,
        String phone,
        String role
) {}

