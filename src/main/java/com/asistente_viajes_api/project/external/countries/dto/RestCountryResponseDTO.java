package com.asistente_viajes_api.project.external.countries.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RestCountryResponseDTO(
        Name name,
        List<String> capital,
        String region,
        String cca2,
        Map<String, Currency> currencies,
        Map<String, String> languages

) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Name(
            String common
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Currency(
            String name,
            String symbol
    ) {
    }

}
