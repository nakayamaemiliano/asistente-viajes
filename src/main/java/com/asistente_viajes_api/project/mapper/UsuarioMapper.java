package com.asistente_viajes_api.project.mapper;

import com.asistente_viajes_api.project.dto.usuario.UsuarioRequestDTO;
import com.asistente_viajes_api.project.dto.usuario.UsuarioResponseDTO;
import com.asistente_viajes_api.project.entity.Usuario;

public class UsuarioMapper {
    public static Usuario toEntity(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();

        usuario.setNombre(dto.nombre());
        usuario.setEmail(dto.email());
        usuario.setPassword(dto.password());
        usuario.setRol(dto.rol());

        return usuario;
    }

    public static UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getUsuarioId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol()
        );
    }
}
