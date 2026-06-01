package com.asistente_viajes_api.project.service;

import com.asistente_viajes_api.project.dto.recomendacionIa.RecomendacionViajeResponseDTO;

public interface IRecomendacionIAService {

    RecomendacionViajeResponseDTO generarRecomendacion(Long consultaViajeId);

    RecomendacionViajeResponseDTO obtenerRecomendacionPorConsulta(Long consultaViajeId);
}
