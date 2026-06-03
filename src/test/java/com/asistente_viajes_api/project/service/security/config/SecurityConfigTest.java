package com.asistente_viajes_api.project.service.security.config;


import com.asistente_viajes_api.project.controller.ClienteController;
import com.asistente_viajes_api.project.controller.DestinoController;
import com.asistente_viajes_api.project.controller.UsuarioController;
import com.asistente_viajes_api.project.dto.cliente.ClienteResponseDTO;
import com.asistente_viajes_api.project.security.CustomUserDetailsService;
import com.asistente_viajes_api.project.security.JwtAuthenticationFilter;
import com.asistente_viajes_api.project.security.JwtService;
import com.asistente_viajes_api.project.security.config.SecurityConfig;
import com.asistente_viajes_api.project.service.IClienteService;
import com.asistente_viajes_api.project.service.IDestinoService;
import com.asistente_viajes_api.project.service.IUsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;




@WebMvcTest(controllers = {
        UsuarioController.class,
        ClienteController.class,
        DestinoController.class
})
@AutoConfigureMockMvc
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUsuarioService usuarioService;

    @MockitoBean
    private IClienteService clienteService;

    @MockitoBean
    private IDestinoService destinoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void usuarios_deberiaBloquearAcceso_cuandoNoEstaAutenticado() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    void usuarios_deberiaPermitirAcceso_cuandoRolEsAdmin() throws Exception {
        when(usuarioService.listarUsuarios()).thenReturn(List.of());

        mockMvc.perform(get("/usuarios")
                        .with(user("admin@test.com").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void usuarios_deberiaDenegarAcceso_cuandoRolEsLector() throws Exception {
        mockMvc.perform(get("/usuarios")
                        .with(user("lector@test.com").roles("LECTOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void usuarios_deberiaDenegarAcceso_cuandoRolEsAsesor() throws Exception {
        mockMvc.perform(get("/usuarios")
                        .with(user("asesor@test.com").roles("ASESOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void clientes_deberiaPermitirCrear_cuandoRolEsAsesor() throws Exception {
        when(clienteService.crearCliente(any()))
                .thenReturn(new ClienteResponseDTO(
                        1L,
                        "Lucia",
                        "Gomez",
                        "lucia@test.com",
                        "111",
                        "Argentina"
                ));

        mockMvc.perform(post("/clientes")
                        .with(user("asesor@test.com").roles("ASESOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Lucia",
                                  "apellido": "Gomez",
                                  "email": "lucia@test.com",
                                  "telefono": "111",
                                  "paisOrigen": "Argentina"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void clientes_deberiaDenegarCrear_cuandoRolEsLector() throws Exception {
        mockMvc.perform(post("/clientes")
                        .with(user("lector@test.com").roles("LECTOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Lucia",
                                  "apellido": "Gomez",
                                  "email": "lucia@test.com",
                                  "telefono": "111",
                                  "paisOrigen": "Argentina"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void destinos_deberiaDenegarEliminar_cuandoRolEsAsesor() throws Exception {
        mockMvc.perform(delete("/destinos/1")
                        .with(user("asesor@test.com").roles("ASESOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void destinos_deberiaPermitirEliminar_cuandoRolEsAdmin() throws Exception {
        mockMvc.perform(delete("/destinos/1")
                        .with(user("admin@test.com").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }


}
