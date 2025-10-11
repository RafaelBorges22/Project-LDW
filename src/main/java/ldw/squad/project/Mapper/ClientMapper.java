package ldw.squad.project.Mapper;

import ldw.squad.project.Dto.ClientDto;
import ldw.squad.project.Dto.CreateClientDto;
import ldw.squad.project.Dto.UpdateClientDto;
import ldw.squad.project.Entities.ClientModel;

public class ClientMapper {

    public static ClientDto toDto(ClientModel client) {
        return new ClientDto(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getAddress(),
                client.getPhone(),
                client.getRole()
        );
    }

    public static ClientModel toEntity(CreateClientDto dto) {
        ClientModel client = new ClientModel();
        client.setName(dto.name());
        client.setEmail(dto.email());
        client.setPassword(dto.password());
        client.setAddress(dto.address());
        client.setPhone(dto.phone());
        client.setRole("CLIENT");
        return client;
    }

    public static void updateEntity(ClientModel client, UpdateClientDto dto) {
        if (dto.name() != null) client.setName(dto.name());
        if (dto.email() != null) client.setEmail(dto.email());
        if (dto.address() != null) client.setAddress(dto.address());
        if (dto.phone() != null) client.setPhone(dto.phone());
    }
}
