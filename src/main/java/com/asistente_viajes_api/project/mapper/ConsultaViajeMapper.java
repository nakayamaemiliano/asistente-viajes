package com.asistente_viajes_api.project.mapper;

import com.asistente_viajes_api.project.dto.consultaViaje.ConsultaViajeRequestDTO;
import com.asistente_viajes_api.project.dto.consultaViaje.ConsultaViajeResponseDTO;
import com.asistente_viajes_api.project.entity.Cliente;
import com.asistente_viajes_api.project.entity.ConsultaViaje;
import com.asistente_viajes_api.project.entity.Destino;
import com.asistente_viajes_api.project.enums.Estado;

public class ConsultaViajeMapper {
    public static ConsultaViaje toEntity(
            ConsultaViajeRequestDTO dto,
            Cliente cliente,
            Destino destino
    ) {
        ConsultaViaje consultaViaje = new ConsultaViaje();

        consultaViaje.setCliente(cliente);
        consultaViaje.setDestino(destino);
        consultaViaje.setFechaInicio(dto.fechaInicio());
        consultaViaje.setFechaFin(dto.fechaFin());
        consultaViaje.setCantidadPersonas(dto.cantidadPersonas());
        consultaViaje.setPresupuesto(dto.presupuesto());
        consultaViaje.setIntereses(dto.intereses());

        // El estado inicial lo define el sistema, no el usuario
        consultaViaje.setEstado(Estado.PENDIENTE);

        return consultaViaje;
    }

    public static ConsultaViajeResponseDTO toResponseDTO(ConsultaViaje consultaViaje) {
        return new ConsultaViajeResponseDTO(
                consultaViaje.getConsultaViajeId(),

                consultaViaje.getCliente().getClienteId(),
                consultaViaje.getCliente().getNombre(),
                consultaViaje.getCliente().getApellido(),

                consultaViaje.getDestino().getDestinoId(),
                consultaViaje.getDestino().getCiudad(),
                consultaViaje.getDestino().getPais(),

                consultaViaje.getFechaInicio(),
                consultaViaje.getFechaFin(),
                consultaViaje.getCantidadPersonas(),
                consultaViaje.getPresupuesto(),
                consultaViaje.getIntereses(),
                consultaViaje.getEstado()
        );
    }

    public static void updateEntityFromDTO(
            ConsultaViajeRequestDTO dto,
            ConsultaViaje consultaViaje,
            Cliente cliente,
            Destino destino
    ) {
        consultaViaje.setCliente(cliente);
        consultaViaje.setDestino(destino);
        consultaViaje.setFechaInicio(dto.fechaInicio());
        consultaViaje.setFechaFin(dto.fechaFin());
        consultaViaje.setCantidadPersonas(dto.cantidadPersonas());
        consultaViaje.setPresupuesto(dto.presupuesto());
        consultaViaje.setIntereses(dto.intereses());

        // No tocamos el estado acá.
        // El estado debería cambiarse con otra lógica específica si después querés.
    }
}
