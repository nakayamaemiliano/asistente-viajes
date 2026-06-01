package com.asistente_viajes_api.project.dto.consultaViaje;

import com.asistente_viajes_api.project.enums.Presupuesto;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ConsultaViajeRequestDTO(
        @NotNull(message = "El cliente es obligatorio")
        Long clienteId,

        @NotNull(message = "El destino es obligatorio")
        Long destinoId,

        @NotNull(message = "La fecha de inicio es obligatoria")
        @FutureOrPresent(message = "La fecha de inicio no puede ser anterior a la fecha actual")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDate fechaFin,

        @NotNull(message = "La cantidad de personas es obligatoria")
        @Min(value = 1, message = "La cantidad de personas debe ser al menos 1")
        Integer cantidadPersonas,

        @NotNull(message = "El presupuesto es obligatorio")
        Presupuesto presupuesto,

        @NotBlank(message = "Los intereses son obligatorios")
        String intereses
) {
}
