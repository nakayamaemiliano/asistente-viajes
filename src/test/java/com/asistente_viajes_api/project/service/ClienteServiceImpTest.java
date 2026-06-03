package com.asistente_viajes_api.project.service;

import com.asistente_viajes_api.project.dto.cliente.ClienteRequestDTO;
import com.asistente_viajes_api.project.dto.cliente.ClienteResponseDTO;
import com.asistente_viajes_api.project.entity.Cliente;
import com.asistente_viajes_api.project.exception.DuplicateResourceException;
import com.asistente_viajes_api.project.exception.ResourceNotFoundException;
import com.asistente_viajes_api.project.repository.IClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceImpTest {
    @Mock
    private IClienteRepository clienteRepository;

    @InjectMocks
    private ClienteServiceImp clienteService;

    @Test
    void crearCliente_deberiaGuardarCliente_cuandoEmailNoExiste() {
        // Arrange
        ClienteRequestDTO request = new ClienteRequestDTO(
                "Lucia",
                "Gomez",
                "lucia@test.com",
                "1122334455",
                "Argentina"
        );

        Cliente clienteGuardado = new Cliente();
        clienteGuardado.setClienteId(1L);
        clienteGuardado.setNombre("Lucia");
        clienteGuardado.setApellido("Gomez");
        clienteGuardado.setEmail("lucia@test.com");
        clienteGuardado.setTelefono("1122334455");
        clienteGuardado.setPaisOrigen("Argentina");

        when(clienteRepository.existsByEmail("lucia@test.com")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteGuardado);

        // Act
        ClienteResponseDTO response = clienteService.crearCliente(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.clienteId());
        assertEquals("Lucia", response.nombre());
        assertEquals("Gomez", response.apellido());
        assertEquals("lucia@test.com", response.email());
        assertEquals("1122334455", response.telefono());
        assertEquals("Argentina", response.paisOrigen());

        verify(clienteRepository).existsByEmail("lucia@test.com");
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void crearCliente_deberiaLanzarDuplicateResourceException_cuandoEmailYaExiste() {
        // Arrange
        ClienteRequestDTO request = new ClienteRequestDTO(
                "Lucia",
                "Gomez",
                "lucia@test.com",
                "1122334455",
                "Argentina"
        );

        when(clienteRepository.existsByEmail("lucia@test.com")).thenReturn(true);

        // Act & Assert
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> clienteService.crearCliente(request)
        );

        assertEquals(
                "Ya existe un cliente con el email: lucia@test.com",
                exception.getMessage()
        );

        verify(clienteRepository).existsByEmail("lucia@test.com");
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void listarClientes_deberiaRetornarListaDeClientes() {
        // Arrange
        Cliente cliente1 = new Cliente();
        cliente1.setClienteId(1L);
        cliente1.setNombre("Lucia");
        cliente1.setApellido("Gomez");
        cliente1.setEmail("lucia@test.com");
        cliente1.setTelefono("111");
        cliente1.setPaisOrigen("Argentina");

        Cliente cliente2 = new Cliente();
        cliente2.setClienteId(2L);
        cliente2.setNombre("Marco");
        cliente2.setApellido("Rossi");
        cliente2.setEmail("marco@test.com");
        cliente2.setTelefono("222");
        cliente2.setPaisOrigen("Italia");

        when(clienteRepository.findAll()).thenReturn(List.of(cliente1, cliente2));

        // Act
        List<ClienteResponseDTO> response = clienteService.listarClientes();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(1L, response.get(0).clienteId());
        assertEquals("Lucia", response.get(0).nombre());

        assertEquals(2L, response.get(1).clienteId());
        assertEquals("Marco", response.get(1).nombre());

        verify(clienteRepository).findAll();
    }

    @Test
    void buscarClientePorId_deberiaRetornarCliente_cuandoExiste() {
        // Arrange
        Long clienteId = 1L;

        Cliente cliente = new Cliente();
        cliente.setClienteId(clienteId);
        cliente.setNombre("Lucia");
        cliente.setApellido("Gomez");
        cliente.setEmail("lucia@test.com");
        cliente.setTelefono("111");
        cliente.setPaisOrigen("Argentina");

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));

        // Act
        ClienteResponseDTO response = clienteService.buscarClientePorId(clienteId);

        // Assert
        assertNotNull(response);
        assertEquals(clienteId, response.clienteId());
        assertEquals("Lucia", response.nombre());
        assertEquals("Gomez", response.apellido());
        assertEquals("lucia@test.com", response.email());
        assertEquals("111", response.telefono());
        assertEquals("Argentina", response.paisOrigen());

        verify(clienteRepository).findById(clienteId);
    }

    @Test
    void buscarClientePorId_deberiaLanzarResourceNotFoundException_cuandoNoExiste() {
        // Arrange
        Long clienteId = 99L;

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> clienteService.buscarClientePorId(clienteId)
        );

        assertEquals(
                "Cliente no encontrado con id: " + clienteId,
                exception.getMessage()
        );

        verify(clienteRepository).findById(clienteId);
    }

    @Test
    void actualizarCliente_deberiaActualizarCliente_cuandoExisteYEmailDisponible() {
        // Arrange
        Long clienteId = 1L;

        ClienteRequestDTO request = new ClienteRequestDTO(
                "Lucia",
                "Perez",
                "lucia.nueva@test.com",
                "999",
                "Uruguay"
        );

        Cliente clienteExistente = new Cliente();
        clienteExistente.setClienteId(clienteId);
        clienteExistente.setNombre("Lucia");
        clienteExistente.setApellido("Gomez");
        clienteExistente.setEmail("lucia@test.com");
        clienteExistente.setTelefono("111");
        clienteExistente.setPaisOrigen("Argentina");

        Cliente clienteActualizado = new Cliente();
        clienteActualizado.setClienteId(clienteId);
        clienteActualizado.setNombre("Lucia");
        clienteActualizado.setApellido("Perez");
        clienteActualizado.setEmail("lucia.nueva@test.com");
        clienteActualizado.setTelefono("999");
        clienteActualizado.setPaisOrigen("Uruguay");

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.existsByEmail("lucia.nueva@test.com")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteActualizado);

        // Act
        ClienteResponseDTO response = clienteService.actualizarCliente(clienteId, request);

        // Assert
        assertNotNull(response);
        assertEquals(clienteId, response.clienteId());
        assertEquals("Lucia", response.nombre());
        assertEquals("Perez", response.apellido());
        assertEquals("lucia.nueva@test.com", response.email());
        assertEquals("999", response.telefono());
        assertEquals("Uruguay", response.paisOrigen());

        verify(clienteRepository).findById(clienteId);
        verify(clienteRepository).existsByEmail("lucia.nueva@test.com");
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void actualizarCliente_deberiaLanzarResourceNotFoundException_cuandoNoExiste() {
        // Arrange
        Long clienteId = 99L;

        ClienteRequestDTO request = new ClienteRequestDTO(
                "Lucia",
                "Perez",
                "lucia.nueva@test.com",
                "999",
                "Uruguay"
        );

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> clienteService.actualizarCliente(clienteId, request)
        );

        assertEquals(
                "Cliente no encontrado con id: " + clienteId,
                exception.getMessage()
        );

        verify(clienteRepository).findById(clienteId);
        verify(clienteRepository, never()).existsByEmail(any());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void eliminarCliente_deberiaEliminarCliente_cuandoExiste() {
        // Arrange
        Long clienteId = 1L;

        Cliente cliente = new Cliente();
        cliente.setClienteId(clienteId);
        cliente.setNombre("Lucia");
        cliente.setApellido("Gomez");
        cliente.setEmail("lucia@test.com");
        cliente.setTelefono("111");
        cliente.setPaisOrigen("Argentina");

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));

        // Act
        clienteService.eliminarCliente(clienteId);

        // Assert
        verify(clienteRepository).findById(clienteId);
        verify(clienteRepository).delete(cliente);
    }

    @Test
    void eliminarCliente_deberiaLanzarResourceNotFoundException_cuandoNoExiste() {
        // Arrange
        Long clienteId = 99L;

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> clienteService.eliminarCliente(clienteId)
        );

        assertEquals(
                "Cliente no encontrado con id: " + clienteId,
                exception.getMessage()
        );

        verify(clienteRepository).findById(clienteId);
        verify(clienteRepository, never()).delete(any(Cliente.class));
    }
}
