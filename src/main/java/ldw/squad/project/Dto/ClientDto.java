package ldw.squad.project.Dto;

import java.util.UUID;

public record ClientDto(
        UUID id,
        String name,
        String email,
        String address,
        String phone,
        String password,
        String role
) {}
