package com.asistente_viajes_api.project.service;

import com.asistente_viajes_api.project.dto.consultaViaje.ConsultaViajeRequestDTO;
import com.asistente_viajes_api.project.dto.consultaViaje.ConsultaViajeResponseDTO;

import java.util.List;

public interface IConsultaViajeService {

    ConsultaViajeResponseDTO crearConsultaViaje(ConsultaViajeRequestDTO dto);

    List<ConsultaViajeResponseDTO> listarConsultasViaje();

    ConsultaViajeResponseDTO buscarConsultaViajePorId(Long id);

    ConsultaViajeResponseDTO actualizarConsultaViaje(Long id, ConsultaViajeRequestDTO dto);

    void eliminarConsultaViaje(Long id);
}
