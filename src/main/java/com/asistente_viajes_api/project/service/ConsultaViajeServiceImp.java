package com.asistente_viajes_api.project.service;

import com.asistente_viajes_api.project.dto.consultaViaje.ConsultaViajeRequestDTO;
import com.asistente_viajes_api.project.dto.consultaViaje.ConsultaViajeResponseDTO;
import com.asistente_viajes_api.project.entity.Cliente;
import com.asistente_viajes_api.project.entity.ConsultaViaje;
import com.asistente_viajes_api.project.entity.Destino;
import com.asistente_viajes_api.project.exception.BadRequestException;
import com.asistente_viajes_api.project.exception.ResourceNotFoundException;
import com.asistente_viajes_api.project.mapper.ConsultaViajeMapper;
import com.asistente_viajes_api.project.repository.IClienteRepository;
import com.asistente_viajes_api.project.repository.IConsultaViajeRepository;
import com.asistente_viajes_api.project.repository.IDestinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultaViajeServiceImp implements IConsultaViajeService {
    @Autowired
    private IConsultaViajeRepository consultaViajeRepository;

    @Autowired
    private IClienteRepository clienteRepository;

    @Autowired
    private IDestinoRepository destinoRepository;


    @Override
    public ConsultaViajeResponseDTO crearConsultaViaje(ConsultaViajeRequestDTO dto) {

        if (dto.fechaFin().isBefore(dto.fechaInicio())) {
            throw new BadRequestException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con id: " + dto.clienteId()
                ));

        Destino destino = destinoRepository.findById(dto.destinoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Destino no encontrado con id: " + dto.destinoId()
                ));

        ConsultaViaje consultaViaje = ConsultaViajeMapper.toEntity(dto, cliente, destino);

        ConsultaViaje consultaGuardada = consultaViajeRepository.save(consultaViaje);

        return ConsultaViajeMapper.toResponseDTO(consultaGuardada);
    }

    @Override
    public List<ConsultaViajeResponseDTO> listarConsultasViaje() {
        return consultaViajeRepository.findAll()
                .stream()
                .map(ConsultaViajeMapper::toResponseDTO)
                .toList();
    }

    @Override
    public ConsultaViajeResponseDTO buscarConsultaViajePorId(Long id) {
        ConsultaViaje consultaViaje = consultaViajeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Consulta de viaje no encontrada con id: " + id
                ));

        return ConsultaViajeMapper.toResponseDTO(consultaViaje);
    }

    @Override
    public ConsultaViajeResponseDTO actualizarConsultaViaje(Long id, ConsultaViajeRequestDTO dto) {

        if (dto.fechaFin().isBefore(dto.fechaInicio())) {
            throw new BadRequestException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }


        ConsultaViaje consultaViaje = consultaViajeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Consulta de viaje no encontrada con id: " + id
                ));

        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con id: " + dto.clienteId()
                ));

        Destino destino = destinoRepository.findById(dto.destinoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Destino no encontrado con id: " + dto.destinoId()
                ));

        ConsultaViajeMapper.updateEntityFromDTO(dto, consultaViaje, cliente, destino);

        ConsultaViaje consultaActualizada = consultaViajeRepository.save(consultaViaje);

        return ConsultaViajeMapper.toResponseDTO(consultaActualizada);
    }

    @Override
    public void eliminarConsultaViaje(Long id) {

        ConsultaViaje consultaViaje = consultaViajeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Consulta de viaje no encontrada con id: " + id
                ));

        consultaViajeRepository.delete(consultaViaje);

    }
}
