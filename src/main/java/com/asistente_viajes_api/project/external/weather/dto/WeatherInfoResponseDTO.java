package com.asistente_viajes_api.project.external.weather.dto;

public record WeatherInfoResponseDTO(
        Long destinoId,
        String ciudad,
        String pais,
        Double temperatura,
        Double sensacionTermica,
        Integer humedad,
        Double precipitacion,
        Double velocidadViento,
        Integer direccionViento,
        Integer codigoClima,
        String descripcion,
        String hora
) {
}
