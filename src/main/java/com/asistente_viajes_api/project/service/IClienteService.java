package com.asistente_viajes_api.project.service;

import com.asistente_viajes_api.project.dto.cliente.ClienteRequestDTO;
import com.asistente_viajes_api.project.dto.cliente.ClienteResponseDTO;

import java.util.List;

public interface IClienteService {

    List<ClienteResponseDTO> listarClientes();

    ClienteResponseDTO crearCliente(ClienteRequestDTO dto);

    ClienteResponseDTO buscarClientePorId(Long id );

    ClienteResponseDTO actualizarCliente(Long id,ClienteRequestDTO dto);

    void eliminarCliente(Long id );
}
