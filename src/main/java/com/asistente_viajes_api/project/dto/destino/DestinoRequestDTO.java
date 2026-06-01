package com.asistente_viajes_api.project.dto.destino;

import jakarta.validation.constraints.*;

public record DestinoRequestDTO(
        @NotBlank(message = "La ciudad es obligatoria")
        @Size(max = 80, message = "La ciudad no puede superar los 80 caracteres")
        String ciudad,

        @NotBlank(message = "El país es obligatorio")
        @Size(max = 80, message = "El país no puede superar los 80 caracteres")
        String pais,

        @NotBlank(message = "El código de país es obligatorio")
        @Size(max = 10, message = "El código de país no puede superar los 10 caracteres")
        String codigoPais,

        @Size(max = 50, message = "La moneda no puede superar los 50 caracteres")
        String moneda,

        @Size(max = 50, message = "El idioma principal no puede superar los 50 caracteres")
        String idiomaPrincipal,

        @Size(max = 50, message = "La región no puede superar los 50 caracteres")
        String region,

        @NotNull(message = "La latitud es obligatoria")
        @DecimalMin(value = "-90.0", message = "La latitud no puede ser menor a -90")
        @DecimalMax(value = "90.0", message = "La latitud no puede ser mayor a 90")
        Double latitud,

        @NotNull(message = "La longitud es obligatoria")
        @DecimalMin(value = "-180.0", message = "La longitud no puede ser menor a -180")
        @DecimalMax(value = "180.0", message = "La longitud no puede ser mayor a 180")
        Double longitud

) {
}
