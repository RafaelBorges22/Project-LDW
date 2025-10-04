package ldw.squad.project.Dto;

public record CreateClientDto(
        String name,
        String email,
        String password,
        String address,
        String phone
) { }

