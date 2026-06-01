package com.asistente_viajes_api.project.controller;

import com.asistente_viajes_api.project.dto.auth.AuthRequestDTO;
import com.asistente_viajes_api.project.dto.auth.AuthResponseDTO;
import com.asistente_viajes_api.project.entity.Usuario;
import com.asistente_viajes_api.project.exception.ResourceNotFoundException;
import com.asistente_viajes_api.project.repository.IUsuarioRepository;
import com.asistente_viajes_api.project.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;


    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.email(),
                        dto.password()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String token = jwtService.generarToken(userDetails);

        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con email: " + dto.email()
                ));

        AuthResponseDTO response = new AuthResponseDTO(
                token,
                "Bearer",
                usuario.getEmail(),
                usuario.getRol()
        );

        return ResponseEntity.ok(response);
    }
}
