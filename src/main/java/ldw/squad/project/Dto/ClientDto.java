package ldw.squad.project.Dto;

public record ClientDto(
        Long id,
        String name,
        String email,
        String address,
        String phone,
        String role
) {}
