package com.asistente_viajes_api.project.service;

import com.asistente_viajes_api.project.dto.destino.DestinoRequestDTO;
import com.asistente_viajes_api.project.dto.destino.DestinoResponseDTO;
import com.asistente_viajes_api.project.external.countries.dto.CountryInfoResponseDTO;
import com.asistente_viajes_api.project.external.weather.dto.WeatherInfoResponseDTO;

import java.util.List;

public interface IDestinoService {

    DestinoResponseDTO crearDestino(DestinoRequestDTO dto);

    List<DestinoResponseDTO> listarDestinos();

    DestinoResponseDTO buscarDestinoPorId(Long id);

    DestinoResponseDTO actualizarDestino(Long id, DestinoRequestDTO dto);

    void eliminarDestino(Long id);


    CountryInfoResponseDTO buscarPais(String pais);


    WeatherInfoResponseDTO obtenerClimaPorDestino(Long id);
}
