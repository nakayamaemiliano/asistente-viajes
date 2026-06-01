package com.asistente_viajes_api.project.dto.recomendacionIa;

import java.time.LocalDateTime;

public record RecomendacionViajeResponseDTO(
        Long recomendacionIaId,
        Long consultaViajeId,
        String cliente,
        String destino,
        String resumen,
        String recomendaciones,
        String advertencias,
        LocalDateTime fechaGeneracion
) {
}
