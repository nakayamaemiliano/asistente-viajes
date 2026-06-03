package com.asistente_viajes_api.project.service;

import com.asistente_viajes_api.project.dto.recomendacionIa.RecomendacionViajeResponseDTO;
import com.asistente_viajes_api.project.entity.Cliente;
import com.asistente_viajes_api.project.entity.ConsultaViaje;
import com.asistente_viajes_api.project.entity.Destino;
import com.asistente_viajes_api.project.entity.RecomendacionIA;
import com.asistente_viajes_api.project.enums.Estado;
import com.asistente_viajes_api.project.enums.Presupuesto;
import com.asistente_viajes_api.project.exception.BadRequestException;
import com.asistente_viajes_api.project.exception.ResourceNotFoundException;
import com.asistente_viajes_api.project.external.ai.AiClient;
import com.asistente_viajes_api.project.external.ai.dto.AiRecommendationResponseDTO;
import com.asistente_viajes_api.project.repository.IConsultaViajeRepository;
import com.asistente_viajes_api.project.repository.IRecomendacionIARepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecomendacionIAServiceImpTest {
    @Mock
    private AiClient aiClient;

    @Mock
    private IRecomendacionIARepository recomendacionIARepository;

    @Mock
    private IConsultaViajeRepository consultaViajeRepository;

    @InjectMocks
    private RecomendacionIAServiceImp recomendacionIAService;
    @Test
    void generarRecomendacion_deberiaGenerarGuardarYCambiarEstado_cuandoConsultaValidaSinRecomendacion() {
        // Arrange
        Long consultaId = 1L;

        Cliente cliente = new Cliente();
        cliente.setClienteId(10L);
        cliente.setNombre("Lucia");
        cliente.setApellido("Gomez");
        cliente.setEmail("lucia@test.com");
        cliente.setTelefono("111");
        cliente.setPaisOrigen("Argentina");

        Destino destino = new Destino();
        destino.setDestinoId(20L);
        destino.setCiudad("Roma");
        destino.setPais("Italia");
        destino.setCodigoPais("IT");
        destino.setMoneda("EUR");
        destino.setIdiomaPrincipal("Italiano");
        destino.setRegion("Europa");
        destino.setLatitud(41.9028);
        destino.setLongitud(12.4964);

        ConsultaViaje consulta = new ConsultaViaje();
        consulta.setConsultaViajeId(consultaId);
        consulta.setCliente(cliente);
        consulta.setDestino(destino);
        consulta.setFechaInicio(LocalDate.of(2027, 7, 10));
        consulta.setFechaFin(LocalDate.of(2027, 7, 18));
        consulta.setCantidadPersonas(2);
        consulta.setPresupuesto(Presupuesto.MEDIO);
        consulta.setIntereses("museos, comida");
        consulta.setEstado(Estado.PENDIENTE);

        AiRecommendationResponseDTO respuestaIA = new AiRecommendationResponseDTO(
                "Resumen generado",
                "Recomendaciones generadas",
                "Advertencias generadas"
        );

        RecomendacionIA recomendacionGuardada = new RecomendacionIA();
        recomendacionGuardada.setRecomendacionIaId(100L);
        recomendacionGuardada.setConsultaViaje(consulta);
        recomendacionGuardada.setResumen("Resumen generado");
        recomendacionGuardada.setRecomendaciones("Recomendaciones generadas");
        recomendacionGuardada.setAdvertencias("Advertencias generadas");
        recomendacionGuardada.setFechaGeneracion(LocalDateTime.of(2026, 6, 3, 17, 0));

        when(consultaViajeRepository.findById(consultaId)).thenReturn(Optional.of(consulta));
        when(recomendacionIARepository.existsByConsultaViaje(consulta)).thenReturn(false);
        when(aiClient.generarRecomendacion(any())).thenReturn(respuestaIA);
        when(recomendacionIARepository.save(any(RecomendacionIA.class))).thenReturn(recomendacionGuardada);

        // Act
        RecomendacionViajeResponseDTO response =
                recomendacionIAService.generarRecomendacion(consultaId);

        // Assert
        assertNotNull(response);
        assertEquals(100L, response.recomendacionIaId());
        assertEquals(consultaId, response.consultaViajeId());
        assertEquals("Lucia Gomez", response.cliente());
        assertEquals("Roma, Italia", response.destino());
        assertEquals("Resumen generado", response.resumen());
        assertEquals("Recomendaciones generadas", response.recomendaciones());
        assertEquals("Advertencias generadas", response.advertencias());
        assertEquals(LocalDateTime.of(2026, 6, 3, 17, 0), response.fechaGeneracion());

        assertEquals(Estado.ANALIZADA, consulta.getEstado());

        verify(consultaViajeRepository).findById(consultaId);
        verify(recomendacionIARepository).existsByConsultaViaje(consulta);
        verify(aiClient).generarRecomendacion(any());
        verify(consultaViajeRepository).save(consulta);
        verify(recomendacionIARepository).save(any(RecomendacionIA.class));
    }


    @Test
    void generarRecomendacion_deberiaDevolverRecomendacionExistente_cuandoYaExiste() {
        // Arrange
        Long consultaId = 1L;

        Cliente cliente = new Cliente();
        cliente.setClienteId(10L);
        cliente.setNombre("Lucia");
        cliente.setApellido("Gomez");
        cliente.setEmail("lucia@test.com");
        cliente.setTelefono("111");
        cliente.setPaisOrigen("Argentina");

        Destino destino = new Destino();
        destino.setDestinoId(20L);
        destino.setCiudad("Roma");
        destino.setPais("Italia");
        destino.setCodigoPais("IT");
        destino.setMoneda("EUR");
        destino.setIdiomaPrincipal("Italiano");
        destino.setRegion("Europa");
        destino.setLatitud(41.9028);
        destino.setLongitud(12.4964);

        ConsultaViaje consulta = new ConsultaViaje();
        consulta.setConsultaViajeId(consultaId);
        consulta.setCliente(cliente);
        consulta.setDestino(destino);
        consulta.setFechaInicio(LocalDate.of(2027, 7, 10));
        consulta.setFechaFin(LocalDate.of(2027, 7, 18));
        consulta.setCantidadPersonas(2);
        consulta.setPresupuesto(Presupuesto.MEDIO);
        consulta.setIntereses("museos, comida");
        consulta.setEstado(Estado.ANALIZADA);

        RecomendacionIA recomendacionExistente = new RecomendacionIA();
        recomendacionExistente.setRecomendacionIaId(100L);
        recomendacionExistente.setConsultaViaje(consulta);
        recomendacionExistente.setResumen("Resumen existente");
        recomendacionExistente.setRecomendaciones("Recomendaciones existentes");
        recomendacionExistente.setAdvertencias("Advertencias existentes");
        recomendacionExistente.setFechaGeneracion(LocalDateTime.of(2026, 6, 3, 17, 0));

        when(consultaViajeRepository.findById(consultaId)).thenReturn(Optional.of(consulta));
        when(recomendacionIARepository.existsByConsultaViaje(consulta)).thenReturn(true);
        when(recomendacionIARepository.findByConsultaViaje(consulta))
                .thenReturn(Optional.of(recomendacionExistente));

        // Act
        RecomendacionViajeResponseDTO response =
                recomendacionIAService.generarRecomendacion(consultaId);

        // Assert
        assertNotNull(response);
        assertEquals(100L, response.recomendacionIaId());
        assertEquals(consultaId, response.consultaViajeId());
        assertEquals("Lucia Gomez", response.cliente());
        assertEquals("Roma, Italia", response.destino());
        assertEquals("Resumen existente", response.resumen());
        assertEquals("Recomendaciones existentes", response.recomendaciones());
        assertEquals("Advertencias existentes", response.advertencias());
        assertEquals(LocalDateTime.of(2026, 6, 3, 17, 0), response.fechaGeneracion());

        verify(consultaViajeRepository).findById(consultaId);
        verify(recomendacionIARepository).existsByConsultaViaje(consulta);
        verify(recomendacionIARepository).findByConsultaViaje(consulta);
        verify(aiClient, never()).generarRecomendacion(any());
        verify(recomendacionIARepository, never()).save(any(RecomendacionIA.class));
        verify(consultaViajeRepository, never()).save(any(ConsultaViaje.class));
    }

    @Test
    void generarRecomendacion_deberiaLanzarResourceNotFoundException_cuandoConsultaNoExiste() {
        // Arrange
        Long consultaId = 99L;

        when(consultaViajeRepository.findById(consultaId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> recomendacionIAService.generarRecomendacion(consultaId)
        );

        assertEquals(
                "Consulta de viaje no encontrada con id: " + consultaId,
                exception.getMessage()
        );

        verify(consultaViajeRepository).findById(consultaId);
        verify(recomendacionIARepository, never()).existsByConsultaViaje(any());
        verify(aiClient, never()).generarRecomendacion(any());
        verify(recomendacionIARepository, never()).save(any(RecomendacionIA.class));
    }

    @Test
    void generarRecomendacion_deberiaLanzarBadRequestException_cuandoConsultaCancelada() {
        // Arrange
        Long consultaId = 1L;

        ConsultaViaje consulta = new ConsultaViaje();
        consulta.setConsultaViajeId(consultaId);
        consulta.setEstado(Estado.CANCELADA);

        when(consultaViajeRepository.findById(consultaId)).thenReturn(Optional.of(consulta));

        // Act & Assert
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> recomendacionIAService.generarRecomendacion(consultaId)
        );

        assertEquals(
                "No se puede generar una recomendación para una consulta cancelada",
                exception.getMessage()
        );

        verify(consultaViajeRepository).findById(consultaId);
        verify(recomendacionIARepository, never()).existsByConsultaViaje(any());
        verify(aiClient, never()).generarRecomendacion(any());
        verify(recomendacionIARepository, never()).save(any(RecomendacionIA.class));
        verify(consultaViajeRepository, never()).save(any(ConsultaViaje.class));
    }

    @Test
    void obtenerRecomendacionPorConsulta_deberiaRetornarRecomendacion_cuandoExiste() {
        // Arrange
        Long consultaId = 1L;

        Cliente cliente = new Cliente();
        cliente.setClienteId(10L);
        cliente.setNombre("Lucia");
        cliente.setApellido("Gomez");
        cliente.setEmail("lucia@test.com");
        cliente.setTelefono("111");
        cliente.setPaisOrigen("Argentina");

        Destino destino = new Destino();
        destino.setDestinoId(20L);
        destino.setCiudad("Roma");
        destino.setPais("Italia");
        destino.setCodigoPais("IT");
        destino.setMoneda("EUR");
        destino.setIdiomaPrincipal("Italiano");
        destino.setRegion("Europa");
        destino.setLatitud(41.9028);
        destino.setLongitud(12.4964);

        ConsultaViaje consulta = new ConsultaViaje();
        consulta.setConsultaViajeId(consultaId);
        consulta.setCliente(cliente);
        consulta.setDestino(destino);
        consulta.setFechaInicio(LocalDate.of(2027, 7, 10));
        consulta.setFechaFin(LocalDate.of(2027, 7, 18));
        consulta.setCantidadPersonas(2);
        consulta.setPresupuesto(Presupuesto.MEDIO);
        consulta.setIntereses("museos, comida");
        consulta.setEstado(Estado.ANALIZADA);

        RecomendacionIA recomendacion = new RecomendacionIA();
        recomendacion.setRecomendacionIaId(100L);
        recomendacion.setConsultaViaje(consulta);
        recomendacion.setResumen("Resumen guardado");
        recomendacion.setRecomendaciones("Recomendaciones guardadas");
        recomendacion.setAdvertencias("Advertencias guardadas");
        recomendacion.setFechaGeneracion(LocalDateTime.of(2026, 6, 3, 17, 0));

        when(consultaViajeRepository.findById(consultaId)).thenReturn(Optional.of(consulta));
        when(recomendacionIARepository.findByConsultaViaje(consulta))
                .thenReturn(Optional.of(recomendacion));

        // Act
        RecomendacionViajeResponseDTO response =
                recomendacionIAService.obtenerRecomendacionPorConsulta(consultaId);

        // Assert
        assertNotNull(response);
        assertEquals(100L, response.recomendacionIaId());
        assertEquals(consultaId, response.consultaViajeId());
        assertEquals("Lucia Gomez", response.cliente());
        assertEquals("Roma, Italia", response.destino());
        assertEquals("Resumen guardado", response.resumen());
        assertEquals("Recomendaciones guardadas", response.recomendaciones());
        assertEquals("Advertencias guardadas", response.advertencias());
        assertEquals(LocalDateTime.of(2026, 6, 3, 17, 0), response.fechaGeneracion());

        verify(consultaViajeRepository).findById(consultaId);
        verify(recomendacionIARepository).findByConsultaViaje(consulta);
    }

    @Test
    void obtenerRecomendacionPorConsulta_deberiaLanzarResourceNotFoundException_cuandoConsultaNoExiste() {
        // Arrange
        Long consultaId = 99L;

        when(consultaViajeRepository.findById(consultaId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> recomendacionIAService.obtenerRecomendacionPorConsulta(consultaId)
        );

        assertEquals(
                "Consulta de viaje no encontrada con id: " + consultaId,
                exception.getMessage()
        );

        verify(consultaViajeRepository).findById(consultaId);
        verify(recomendacionIARepository, never()).findByConsultaViaje(any());
    }

    @Test
    void obtenerRecomendacionPorConsulta_deberiaLanzarResourceNotFoundException_cuandoNoTieneRecomendacion() {
        // Arrange
        Long consultaId = 1L;

        ConsultaViaje consulta = new ConsultaViaje();
        consulta.setConsultaViajeId(consultaId);

        when(consultaViajeRepository.findById(consultaId)).thenReturn(Optional.of(consulta));
        when(recomendacionIARepository.findByConsultaViaje(consulta)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> recomendacionIAService.obtenerRecomendacionPorConsulta(consultaId)
        );

        assertEquals(
                "No existe una recomendación para la consulta con id: " + consultaId,
                exception.getMessage()
        );

        verify(consultaViajeRepository).findById(consultaId);
        verify(recomendacionIARepository).findByConsultaViaje(consulta);
    }
}
