package com.asistente_viajes_api.project.dto.consultaViaje;

import com.asistente_viajes_api.project.enums.Estado;
import com.asistente_viajes_api.project.enums.Presupuesto;

import java.time.LocalDate;

public record ConsultaViajeResponseDTO(
        Long consultaViajeId,
        Long clienteId,
        String nombreCliente,
        String apellidoCliente,
        Long destinoId,
        String ciudadDestino,
        String paisDestino,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Integer cantidadPersonas,
        Presupuesto presupuesto,
        String intereses,
        Estado estado
) {
}
