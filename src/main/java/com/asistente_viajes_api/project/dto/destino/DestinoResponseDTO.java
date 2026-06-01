package com.asistente_viajes_api.project.dto.destino;

public record DestinoResponseDTO(
        Long destinoId,
        String ciudad,
        String pais,
        String codigoPais,
        String moneda,
        String idiomaPrincipal,
        String region,
        Double latitud,
        Double longitud
) {
}
