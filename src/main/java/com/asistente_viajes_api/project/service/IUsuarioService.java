package com.asistente_viajes_api.project.service;

import com.asistente_viajes_api.project.dto.usuario.UsuarioRequestDTO;
import com.asistente_viajes_api.project.dto.usuario.UsuarioResponseDTO;

import java.util.List;

public interface IUsuarioService {
    UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto);

    List<UsuarioResponseDTO> listarUsuarios();

    UsuarioResponseDTO buscarUsuarioPorId(Long id);

    UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO dto);

    void eliminarUsuario(Long id);
}
