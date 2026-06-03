package com.asistente_viajes_api.project.service.security;

import com.asistente_viajes_api.project.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {
    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        String secret = "mi-clave-super-secreta-para-tests-jwt-de-minimo-48-caracteres";
        long expirationMinutes = 60;

        jwtService = new JwtService(secret, expirationMinutes);

        userDetails = User.builder()
                .username("admin@test.com")
                .password("123456")
                .roles("ADMIN")
                .build();
    }

    @Test
    void generarToken_deberiaCrearTokenConUsername() {
        // Act
        String token = jwtService.generarToken(userDetails);

        // Assert
        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals("admin@test.com", jwtService.extraerUsername(token));
    }

    @Test
    void extraerUsername_deberiaRetornarUsernameDelToken() {
        // Arrange
        String token = jwtService.generarToken(userDetails);

        // Act
        String username = jwtService.extraerUsername(token);

        // Assert
        assertEquals("admin@test.com", username);
    }

    @Test
    void tokenValido_deberiaRetornarTrue_cuandoTokenEsValido() {
        // Arrange
        String token = jwtService.generarToken(userDetails);

        // Act
        boolean valido = jwtService.tokenValido(token, userDetails);

        // Assert
        assertTrue(valido);
    }

    @Test
    void tokenValido_deberiaRetornarFalse_cuandoUsernameNoCoincide() {
        // Arrange
        String token = jwtService.generarToken(userDetails);

        UserDetails otroUsuario = User.builder()
                .username("lector@test.com")
                .password("123456")
                .roles("LECTOR")
                .build();

        // Act
        boolean valido = jwtService.tokenValido(token, otroUsuario);

        // Assert
        assertFalse(valido);
    }






}
