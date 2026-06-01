package com.asistente_viajes_api.project.service;

import com.asistente_viajes_api.project.dto.cliente.ClienteRequestDTO;
import com.asistente_viajes_api.project.dto.cliente.ClienteResponseDTO;
import com.asistente_viajes_api.project.entity.Cliente;
import com.asistente_viajes_api.project.exception.DuplicateResourceException;
import com.asistente_viajes_api.project.exception.ResourceNotFoundException;
import com.asistente_viajes_api.project.mapper.ClienteMapper;
import com.asistente_viajes_api.project.repository.IClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteServiceImp implements IClienteService {

    @Autowired
    private IClienteRepository clienteRepository;

    @Override
    public List<ClienteResponseDTO> listarClientes() {
        return clienteRepository.findAll()
                .stream()
                .map(ClienteMapper::toResponseDTO)
                .toList();
    }

    @Override
    public ClienteResponseDTO crearCliente(ClienteRequestDTO dto) {
        if (clienteRepository.existsByEmail(dto.email())) {
            throw new DuplicateResourceException("Ya existe un cliente con el email: " + dto.email());
        }

        Cliente cliente = ClienteMapper.toEntity(dto);
        Cliente clienteGuardado = clienteRepository.save(cliente);
        return  ClienteMapper.toResponseDTO(clienteGuardado);
    }

    @Override
    public ClienteResponseDTO buscarClientePorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        return ClienteMapper.toResponseDTO(cliente);
    }

    @Override
    public ClienteResponseDTO actualizarCliente(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        if (!cliente.getEmail().equals(dto.email()) && clienteRepository.existsByEmail(dto.email())){
            throw new DuplicateResourceException("Ya existe un cliente con el email: " + dto.email());
        }

        ClienteMapper.updateEntityFromDTO(dto, cliente);

        Cliente clienteActualizado = clienteRepository.save(cliente);

        return ClienteMapper.toResponseDTO(clienteActualizado);



    }

    @Override
    public void eliminarCliente(Long id) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        clienteRepository.delete(cliente);

    }
}
