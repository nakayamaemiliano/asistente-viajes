package com.asistente_viajes_api.project.dto.cliente;

public record ClienteResponseDTO(

        Long clienteId,
        String nombre,
        String apellido,
        String email,
        String telefono,
        String paisOrigen

) {
}
