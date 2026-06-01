package com.asistente_viajes_api.project.external.countries.dto;

import java.util.List;

public record CountryInfoResponseDTO(
        String nombre,
        String capital,
        String region,
        String codigoPais,
        List<String> monedas,
        List<String> idiomas
) {
}
