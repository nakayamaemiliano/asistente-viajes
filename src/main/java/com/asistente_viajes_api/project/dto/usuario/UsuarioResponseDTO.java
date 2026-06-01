package com.asistente_viajes_api.project.dto.usuario;

import com.asistente_viajes_api.project.enums.Rol;

public record UsuarioResponseDTO (
        Long usuarioId,
        String nombre,
        String email,
        Rol rol
) {
}
