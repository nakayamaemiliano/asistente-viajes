package com.asistente_viajes_api.project.external.ai.dto;

public record AiRecommendationResponseDTO(
        String resumen,
        String recomendaciones,
        String advertencias
) {
}
