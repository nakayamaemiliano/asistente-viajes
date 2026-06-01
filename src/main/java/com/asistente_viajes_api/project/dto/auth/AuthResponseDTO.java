package com.asistente_viajes_api.project.dto.auth;

import com.asistente_viajes_api.project.enums.Rol;

public record AuthResponseDTO(
        String token,
        String tipo,
        String email,
        Rol rol
) {
}
