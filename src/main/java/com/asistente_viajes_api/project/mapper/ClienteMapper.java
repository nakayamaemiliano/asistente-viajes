package com.asistente_viajes_api.project.mapper;

import com.asistente_viajes_api.project.dto.cliente.ClienteRequestDTO;
import com.asistente_viajes_api.project.dto.cliente.ClienteResponseDTO;
import com.asistente_viajes_api.project.entity.Cliente;

public class ClienteMapper {

    public static Cliente toEntity(ClienteRequestDTO dto){
        Cliente cliente = new Cliente();

        cliente.setNombre(dto.nombre());
        cliente.setApellido(dto.apellido());
        cliente.setEmail(dto.email());
        cliente.setTelefono(dto.telefono());
        cliente.setPaisOrigen(dto.paisOrigen());

        return cliente;

    }

    public static ClienteResponseDTO toResponseDTO(Cliente cliente){
        return new ClienteResponseDTO(
                cliente.getClienteId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.getPaisOrigen()
        );
    }

    public  static void updateEntityFromDTO(ClienteRequestDTO dto, Cliente cliente){
        cliente.setNombre(dto.nombre());
        cliente.setApellido(dto.apellido());
        cliente.setEmail(dto.email());
        cliente.setTelefono(dto.telefono());
        cliente.setPaisOrigen(dto.paisOrigen());
    }

}
