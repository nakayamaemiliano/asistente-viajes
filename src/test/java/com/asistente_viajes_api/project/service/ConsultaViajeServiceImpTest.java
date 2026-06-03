package com.asistente_viajes_api.project.service;

import com.asistente_viajes_api.project.dto.consultaViaje.ConsultaViajeRequestDTO;
import com.asistente_viajes_api.project.dto.consultaViaje.ConsultaViajeResponseDTO;
import com.asistente_viajes_api.project.entity.Cliente;
import com.asistente_viajes_api.project.entity.ConsultaViaje;
import com.asistente_viajes_api.project.entity.Destino;
import com.asistente_viajes_api.project.enums.Estado;
import com.asistente_viajes_api.project.enums.Presupuesto;
import com.asistente_viajes_api.project.exception.BadRequestException;
import com.asistente_viajes_api.project.exception.ResourceNotFoundException;
import com.asistente_viajes_api.project.repository.IClienteRepository;
import com.asistente_viajes_api.project.repository.IConsultaViajeRepository;
import com.asistente_viajes_api.project.repository.IDestinoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConsultaViajeServiceImpTest {

    @Mock
    private IConsultaViajeRepository consultaViajeRepository;

    @Mock
    private IClienteRepository clienteRepository;

    @Mock
    private IDestinoRepository destinoRepository;

    @InjectMocks
    private ConsultaViajeServiceImp consultaViajeService;

    @Test
    void crearConsultaViaje_deberiaGuardarConsulta_cuandoDatosValidos() {
        // Arrange
        Long clienteId = 1L;
        Long destinoId = 2L;

        Cliente cliente = new Cliente();
        cliente.setClienteId(clienteId);
        cliente.setNombre("Lucia");
        cliente.setApellido("Gomez");
        cliente.setEmail("lucia@test.com");
        cliente.setTelefono("111");
        cliente.setPaisOrigen("Argentina");

        Destino destino = new Destino();
        destino.setDestinoId(destinoId);
        destino.setCiudad("Roma");
        destino.setPais("Italia");
        destino.setCodigoPais("IT");
        destino.setMoneda("EUR");
        destino.setIdiomaPrincipal("Italiano");
        destino.setRegion("Europa");
        destino.setLatitud(41.9028);
        destino.setLongitud(12.4964);

        ConsultaViajeRequestDTO request = new ConsultaViajeRequestDTO(
                clienteId,
                destinoId,
                LocalDate.of(2027, 7, 10),
                LocalDate.of(2027, 7, 18),
                2,
                Presupuesto.MEDIO,
                "museos, comida, arquitectura"
        );

        ConsultaViaje consultaGuardada = new ConsultaViaje();
        consultaGuardada.setConsultaViajeId(1L);
        consultaGuardada.setCliente(cliente);
        consultaGuardada.setDestino(destino);
        consultaGuardada.setFechaInicio(LocalDate.of(2027, 7, 10));
        consultaGuardada.setFechaFin(LocalDate.of(2027, 7, 18));
        consultaGuardada.setCantidadPersonas(2);
        consultaGuardada.setPresupuesto(Presupuesto.MEDIO);
        consultaGuardada.setIntereses("museos, comida, arquitectura");
        consultaGuardada.setEstado(Estado.PENDIENTE);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(destinoRepository.findById(destinoId)).thenReturn(Optional.of(destino));
        when(consultaViajeRepository.save(any(ConsultaViaje.class))).thenReturn(consultaGuardada);

        // Act
        ConsultaViajeResponseDTO response = consultaViajeService.crearConsultaViaje(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.consultaViajeId());
        assertEquals(clienteId, response.clienteId());
        assertEquals("Lucia", response.nombreCliente());
        assertEquals("Gomez", response.apellidoCliente());
        assertEquals(destinoId, response.destinoId());
        assertEquals("Roma", response.ciudadDestino());
        assertEquals("Italia", response.paisDestino());
        assertEquals(LocalDate.of(2027, 7, 10), response.fechaInicio());
        assertEquals(LocalDate.of(2027, 7, 18), response.fechaFin());
        assertEquals(2, response.cantidadPersonas());
        assertEquals(Presupuesto.MEDIO, response.presupuesto());
        assertEquals("museos, comida, arquitectura", response.intereses());
        assertEquals(Estado.PENDIENTE, response.estado());

        verify(clienteRepository).findById(clienteId);
        verify(destinoRepository).findById(destinoId);
        verify(consultaViajeRepository).save(any(ConsultaViaje.class));
    }

    @Test
    void crearConsultaViaje_deberiaLanzarBadRequestException_cuandoFechaFinEsAnteriorAFechaInicio() {
        // Arrange
        ConsultaViajeRequestDTO request = new ConsultaViajeRequestDTO(
                1L,
                2L,
                LocalDate.of(2027, 7, 18),
                LocalDate.of(2027, 7, 10),
                2,
                Presupuesto.MEDIO,
                "museos, comida, arquitectura"
        );

        // Act & Assert
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> consultaViajeService.crearConsultaViaje(request)
        );

        assertEquals(
                "La fecha de fin no puede ser anterior a la fecha de inicio",
                exception.getMessage()
        );

        verify(clienteRepository, never()).findById(any());
        verify(destinoRepository, never()).findById(any());
        verify(consultaViajeRepository, never()).save(any(ConsultaViaje.class));
    }

    @Test
    void crearConsultaViaje_deberiaLanzarResourceNotFoundException_cuandoClienteNoExiste() {
        // Arrange
        Long clienteId = 99L;
        Long destinoId = 2L;

        ConsultaViajeRequestDTO request = new ConsultaViajeRequestDTO(
                clienteId,
                destinoId,
                LocalDate.of(2027, 7, 10),
                LocalDate.of(2027, 7, 18),
                2,
                Presupuesto.MEDIO,
                "museos, comida, arquitectura"
        );

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> consultaViajeService.crearConsultaViaje(request)
        );

        assertEquals(
                "Cliente no encontrado con id: " + clienteId,
                exception.getMessage()
        );

        verify(clienteRepository).findById(clienteId);
        verify(destinoRepository, never()).findById(any());
        verify(consultaViajeRepository, never()).save(any(ConsultaViaje.class));
    }

    @Test
    void crearConsultaViaje_deberiaLanzarResourceNotFoundException_cuandoDestinoNoExiste() {
        // Arrange
        Long clienteId = 1L;
        Long destinoId = 99L;

        Cliente cliente = new Cliente();
        cliente.setClienteId(clienteId);
        cliente.setNombre("Lucia");
        cliente.setApellido("Gomez");
        cliente.setEmail("lucia@test.com");
        cliente.setTelefono("111");
        cliente.setPaisOrigen("Argentina");

        ConsultaViajeRequestDTO request = new ConsultaViajeRequestDTO(
                clienteId,
                destinoId,
                LocalDate.of(2027, 7, 10),
                LocalDate.of(2027, 7, 18),
                2,
                Presupuesto.MEDIO,
                "museos, comida, arquitectura"
        );

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(destinoRepository.findById(destinoId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> consultaViajeService.crearConsultaViaje(request)
        );

        assertEquals(
                "Destino no encontrado con id: " + destinoId,
                exception.getMessage()
        );

        verify(clienteRepository).findById(clienteId);
        verify(destinoRepository).findById(destinoId);
        verify(consultaViajeRepository, never()).save(any(ConsultaViaje.class));
    }

    @Test
    void listarConsultasViaje_deberiaRetornarListaDeConsultas() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setClienteId(1L);
        cliente.setNombre("Lucia");
        cliente.setApellido("Gomez");
        cliente.setEmail("lucia@test.com");
        cliente.setTelefono("111");
        cliente.setPaisOrigen("Argentina");

        Destino destino = new Destino();
        destino.setDestinoId(2L);
        destino.setCiudad("Roma");
        destino.setPais("Italia");
        destino.setCodigoPais("IT");
        destino.setMoneda("EUR");
        destino.setIdiomaPrincipal("Italiano");
        destino.setRegion("Europa");
        destino.setLatitud(41.9028);
        destino.setLongitud(12.4964);

        ConsultaViaje consulta1 = new ConsultaViaje();
        consulta1.setConsultaViajeId(1L);
        consulta1.setCliente(cliente);
        consulta1.setDestino(destino);
        consulta1.setFechaInicio(LocalDate.of(2027, 7, 10));
        consulta1.setFechaFin(LocalDate.of(2027, 7, 18));
        consulta1.setCantidadPersonas(2);
        consulta1.setPresupuesto(Presupuesto.MEDIO);
        consulta1.setIntereses("museos");
        consulta1.setEstado(Estado.PENDIENTE);

        ConsultaViaje consulta2 = new ConsultaViaje();
        consulta2.setConsultaViajeId(2L);
        consulta2.setCliente(cliente);
        consulta2.setDestino(destino);
        consulta2.setFechaInicio(LocalDate.of(2027, 8, 5));
        consulta2.setFechaFin(LocalDate.of(2027, 8, 12));
        consulta2.setCantidadPersonas(3);
        consulta2.setPresupuesto(Presupuesto.ALTO);
        consulta2.setIntereses("gastronomia");
        consulta2.setEstado(Estado.ANALIZADA);

        when(consultaViajeRepository.findAll()).thenReturn(List.of(consulta1, consulta2));

        // Act
        List<ConsultaViajeResponseDTO> response = consultaViajeService.listarConsultasViaje();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(1L, response.get(0).consultaViajeId());
        assertEquals("Lucia", response.get(0).nombreCliente());
        assertEquals("Roma", response.get(0).ciudadDestino());
        assertEquals(Estado.PENDIENTE, response.get(0).estado());

        assertEquals(2L, response.get(1).consultaViajeId());
        assertEquals(Presupuesto.ALTO, response.get(1).presupuesto());
        assertEquals(Estado.ANALIZADA, response.get(1).estado());

        verify(consultaViajeRepository).findAll();
    }

    @Test
    void buscarConsultaViajePorId_deberiaRetornarConsulta_cuandoExiste() {
        // Arrange
        Long consultaId = 1L;

        Cliente cliente = new Cliente();
        cliente.setClienteId(1L);
        cliente.setNombre("Lucia");
        cliente.setApellido("Gomez");
        cliente.setEmail("lucia@test.com");
        cliente.setTelefono("111");
        cliente.setPaisOrigen("Argentina");

        Destino destino = new Destino();
        destino.setDestinoId(2L);
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
        consulta.setIntereses("museos");
        consulta.setEstado(Estado.PENDIENTE);

        when(consultaViajeRepository.findById(consultaId)).thenReturn(Optional.of(consulta));

        // Act
        ConsultaViajeResponseDTO response = consultaViajeService.buscarConsultaViajePorId(consultaId);

        // Assert
        assertNotNull(response);
        assertEquals(consultaId, response.consultaViajeId());
        assertEquals(1L, response.clienteId());
        assertEquals("Lucia", response.nombreCliente());
        assertEquals("Gomez", response.apellidoCliente());
        assertEquals(2L, response.destinoId());
        assertEquals("Roma", response.ciudadDestino());
        assertEquals("Italia", response.paisDestino());
        assertEquals(LocalDate.of(2027, 7, 10), response.fechaInicio());
        assertEquals(LocalDate.of(2027, 7, 18), response.fechaFin());
        assertEquals(2, response.cantidadPersonas());
        assertEquals(Presupuesto.MEDIO, response.presupuesto());
        assertEquals("museos", response.intereses());
        assertEquals(Estado.PENDIENTE, response.estado());

        verify(consultaViajeRepository).findById(consultaId);
    }

    @Test
    void buscarConsultaViajePorId_deberiaLanzarResourceNotFoundException_cuandoNoExiste() {
        // Arrange
        Long consultaId = 99L;

        when(consultaViajeRepository.findById(consultaId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> consultaViajeService.buscarConsultaViajePorId(consultaId)
        );

        assertEquals(
                "Consulta de viaje no encontrada con id: " + consultaId,
                exception.getMessage()
        );

        verify(consultaViajeRepository).findById(consultaId);
    }


    @Test
    void actualizarConsultaViaje_deberiaActualizarConsulta_cuandoDatosValidos() {
        // Arrange
        Long consultaId = 1L;
        Long clienteId = 1L;
        Long destinoId = 2L;

        Cliente cliente = new Cliente();
        cliente.setClienteId(clienteId);
        cliente.setNombre("Lucia");
        cliente.setApellido("Gomez");
        cliente.setEmail("lucia@test.com");
        cliente.setTelefono("111");
        cliente.setPaisOrigen("Argentina");

        Destino destino = new Destino();
        destino.setDestinoId(destinoId);
        destino.setCiudad("Roma");
        destino.setPais("Italia");
        destino.setCodigoPais("IT");
        destino.setMoneda("EUR");
        destino.setIdiomaPrincipal("Italiano");
        destino.setRegion("Europa");
        destino.setLatitud(41.9028);
        destino.setLongitud(12.4964);

        ConsultaViaje consultaExistente = new ConsultaViaje();
        consultaExistente.setConsultaViajeId(consultaId);
        consultaExistente.setCliente(cliente);
        consultaExistente.setDestino(destino);
        consultaExistente.setFechaInicio(LocalDate.of(2027, 6, 1));
        consultaExistente.setFechaFin(LocalDate.of(2027, 6, 10));
        consultaExistente.setCantidadPersonas(1);
        consultaExistente.setPresupuesto(Presupuesto.BAJO);
        consultaExistente.setIntereses("playa");
        consultaExistente.setEstado(Estado.PENDIENTE);

        ConsultaViajeRequestDTO request = new ConsultaViajeRequestDTO(
                clienteId,
                destinoId,
                LocalDate.of(2027, 7, 10),
                LocalDate.of(2027, 7, 18),
                2,
                Presupuesto.MEDIO,
                "museos, comida"
        );

        ConsultaViaje consultaActualizada = new ConsultaViaje();
        consultaActualizada.setConsultaViajeId(consultaId);
        consultaActualizada.setCliente(cliente);
        consultaActualizada.setDestino(destino);
        consultaActualizada.setFechaInicio(LocalDate.of(2027, 7, 10));
        consultaActualizada.setFechaFin(LocalDate.of(2027, 7, 18));
        consultaActualizada.setCantidadPersonas(2);
        consultaActualizada.setPresupuesto(Presupuesto.MEDIO);
        consultaActualizada.setIntereses("museos, comida");
        consultaActualizada.setEstado(Estado.PENDIENTE);

        when(consultaViajeRepository.findById(consultaId)).thenReturn(Optional.of(consultaExistente));
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(destinoRepository.findById(destinoId)).thenReturn(Optional.of(destino));
        when(consultaViajeRepository.save(any(ConsultaViaje.class))).thenReturn(consultaActualizada);

        // Act
        ConsultaViajeResponseDTO response = consultaViajeService.actualizarConsultaViaje(consultaId, request);

        // Assert
        assertNotNull(response);
        assertEquals(consultaId, response.consultaViajeId());
        assertEquals(clienteId, response.clienteId());
        assertEquals("Lucia", response.nombreCliente());
        assertEquals("Gomez", response.apellidoCliente());
        assertEquals(destinoId, response.destinoId());
        assertEquals("Roma", response.ciudadDestino());
        assertEquals("Italia", response.paisDestino());
        assertEquals(LocalDate.of(2027, 7, 10), response.fechaInicio());
        assertEquals(LocalDate.of(2027, 7, 18), response.fechaFin());
        assertEquals(2, response.cantidadPersonas());
        assertEquals(Presupuesto.MEDIO, response.presupuesto());
        assertEquals("museos, comida", response.intereses());
        assertEquals(Estado.PENDIENTE, response.estado());

        verify(consultaViajeRepository).findById(consultaId);
        verify(clienteRepository).findById(clienteId);
        verify(destinoRepository).findById(destinoId);
        verify(consultaViajeRepository).save(any(ConsultaViaje.class));
    }

    @Test
    void actualizarConsultaViaje_deberiaLanzarBadRequestException_cuandoFechaFinEsAnteriorAFechaInicio() {
        // Arrange
        Long consultaId = 1L;

        ConsultaViajeRequestDTO request = new ConsultaViajeRequestDTO(
                1L,
                2L,
                LocalDate.of(2027, 7, 18),
                LocalDate.of(2027, 7, 10),
                2,
                Presupuesto.MEDIO,
                "museos, comida"
        );

        // Act & Assert
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> consultaViajeService.actualizarConsultaViaje(consultaId, request)
        );

        assertEquals(
                "La fecha de fin no puede ser anterior a la fecha de inicio",
                exception.getMessage()
        );

        verify(consultaViajeRepository, never()).findById(any());
        verify(clienteRepository, never()).findById(any());
        verify(destinoRepository, never()).findById(any());
        verify(consultaViajeRepository, never()).save(any(ConsultaViaje.class));
    }
    @Test
    void actualizarConsultaViaje_deberiaLanzarResourceNotFoundException_cuandoConsultaNoExiste() {
        // Arrange
        Long consultaId = 99L;

        ConsultaViajeRequestDTO request = new ConsultaViajeRequestDTO(
                1L,
                2L,
                LocalDate.of(2027, 7, 10),
                LocalDate.of(2027, 7, 18),
                2,
                Presupuesto.MEDIO,
                "museos, comida"
        );

        when(consultaViajeRepository.findById(consultaId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> consultaViajeService.actualizarConsultaViaje(consultaId, request)
        );

        assertEquals(
                "Consulta de viaje no encontrada con id: " + consultaId,
                exception.getMessage()
        );

        verify(consultaViajeRepository).findById(consultaId);
        verify(clienteRepository, never()).findById(any());
        verify(destinoRepository, never()).findById(any());
        verify(consultaViajeRepository, never()).save(any(ConsultaViaje.class));
    }

    @Test
    void actualizarConsultaViaje_deberiaLanzarResourceNotFoundException_cuandoClienteNoExiste() {
        // Arrange
        Long consultaId = 1L;
        Long clienteId = 99L;
        Long destinoId = 2L;

        ConsultaViaje consultaExistente = new ConsultaViaje();
        consultaExistente.setConsultaViajeId(consultaId);

        ConsultaViajeRequestDTO request = new ConsultaViajeRequestDTO(
                clienteId,
                destinoId,
                LocalDate.of(2027, 7, 10),
                LocalDate.of(2027, 7, 18),
                2,
                Presupuesto.MEDIO,
                "museos, comida"
        );

        when(consultaViajeRepository.findById(consultaId)).thenReturn(Optional.of(consultaExistente));
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> consultaViajeService.actualizarConsultaViaje(consultaId, request)
        );

        assertEquals(
                "Cliente no encontrado con id: " + clienteId,
                exception.getMessage()
        );

        verify(consultaViajeRepository).findById(consultaId);
        verify(clienteRepository).findById(clienteId);
        verify(destinoRepository, never()).findById(any());
        verify(consultaViajeRepository, never()).save(any(ConsultaViaje.class));
    }

    @Test
    void actualizarConsultaViaje_deberiaLanzarResourceNotFoundException_cuandoDestinoNoExiste() {
        // Arrange
        Long consultaId = 1L;
        Long clienteId = 1L;
        Long destinoId = 99L;

        ConsultaViaje consultaExistente = new ConsultaViaje();
        consultaExistente.setConsultaViajeId(consultaId);

        Cliente cliente = new Cliente();
        cliente.setClienteId(clienteId);
        cliente.setNombre("Lucia");
        cliente.setApellido("Gomez");
        cliente.setEmail("lucia@test.com");
        cliente.setTelefono("111");
        cliente.setPaisOrigen("Argentina");

        ConsultaViajeRequestDTO request = new ConsultaViajeRequestDTO(
                clienteId,
                destinoId,
                LocalDate.of(2027, 7, 10),
                LocalDate.of(2027, 7, 18),
                2,
                Presupuesto.MEDIO,
                "museos, comida"
        );

        when(consultaViajeRepository.findById(consultaId)).thenReturn(Optional.of(consultaExistente));
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(destinoRepository.findById(destinoId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> consultaViajeService.actualizarConsultaViaje(consultaId, request)
        );

        assertEquals(
                "Destino no encontrado con id: " + destinoId,
                exception.getMessage()
        );

        verify(consultaViajeRepository).findById(consultaId);
        verify(clienteRepository).findById(clienteId);
        verify(destinoRepository).findById(destinoId);
        verify(consultaViajeRepository, never()).save(any(ConsultaViaje.class));
    }

    @Test
    void eliminarConsultaViaje_deberiaEliminarConsulta_cuandoExiste() {
        // Arrange
        Long consultaId = 1L;

        ConsultaViaje consulta = new ConsultaViaje();
        consulta.setConsultaViajeId(consultaId);

        when(consultaViajeRepository.findById(consultaId)).thenReturn(Optional.of(consulta));

        // Act
        consultaViajeService.eliminarConsultaViaje(consultaId);

        // Assert
        verify(consultaViajeRepository).findById(consultaId);
        verify(consultaViajeRepository).delete(consulta);
    }

    @Test
    void eliminarConsultaViaje_deberiaLanzarResourceNotFoundException_cuandoNoExiste() {
        // Arrange
        Long consultaId = 99L;

        when(consultaViajeRepository.findById(consultaId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> consultaViajeService.eliminarConsultaViaje(consultaId)
        );

        assertEquals(
                "Consulta de viaje no encontrada con id: " + consultaId,
                exception.getMessage()
        );

        verify(consultaViajeRepository).findById(consultaId);
        verify(consultaViajeRepository, never()).delete(any(ConsultaViaje.class));
    }
}
