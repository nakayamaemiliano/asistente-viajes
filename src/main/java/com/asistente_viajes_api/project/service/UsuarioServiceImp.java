package com.asistente_viajes_api.project.service;

import com.asistente_viajes_api.project.dto.usuario.UsuarioRequestDTO;
import com.asistente_viajes_api.project.dto.usuario.UsuarioResponseDTO;
import com.asistente_viajes_api.project.entity.Usuario;
import com.asistente_viajes_api.project.exception.DuplicateResourceException;
import com.asistente_viajes_api.project.exception.ResourceNotFoundException;
import com.asistente_viajes_api.project.mapper.UsuarioMapper;
import com.asistente_viajes_api.project.repository.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UsuarioServiceImp implements IUsuarioService{
    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto) {
        if(usuarioRepository.existsByEmail(dto.email())){
            throw new DuplicateResourceException("Ya existe un usario con el emai: " + dto.email());
        }

        Usuario usuario = UsuarioMapper.toEntity(dto);
        usuario.setPassword(passwordEncoder.encode(dto.password()));

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return UsuarioMapper.toResponseDTO(usuarioGuardado);
    }

    @Override
    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioMapper::toResponseDTO)
                .toList();
    }

    @Override
    public UsuarioResponseDTO buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        return UsuarioMapper.toResponseDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        if (!usuario.getEmail().equalsIgnoreCase(dto.email()) && usuarioRepository.existsByEmail(dto.email())) {
            throw new DuplicateResourceException("Ya existe un usuario con el email: " + dto.email());
        }


        usuario.setNombre(dto.nombre());
        usuario.setEmail(dto.email());
        usuario.setPassword(passwordEncoder.encode(dto.password()));
        usuario.setRol(dto.rol());

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return UsuarioMapper.toResponseDTO(usuarioActualizado);
    }

    @Override
    public void eliminarUsuario(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        usuarioRepository.delete(usuario);

    }
}
