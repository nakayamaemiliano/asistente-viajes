package com.asistente_viajes_api.project.mapper;

import com.asistente_viajes_api.project.dto.recomendacionIa.RecomendacionViajeResponseDTO;
import com.asistente_viajes_api.project.entity.Cliente;
import com.asistente_viajes_api.project.entity.ConsultaViaje;
import com.asistente_viajes_api.project.entity.Destino;
import com.asistente_viajes_api.project.entity.RecomendacionIA;

public class RecomendacionIAMapper {

    public static RecomendacionViajeResponseDTO toResponseDTO(RecomendacionIA recomendacionIA) {

        ConsultaViaje consulta = recomendacionIA.getConsultaViaje();
        Cliente cliente = consulta.getCliente();
        Destino destino = consulta.getDestino();

        String nombreCliente = cliente.getNombre() + " " + cliente.getApellido();
        String nombreDestino = destino.getCiudad() + ", " + destino.getPais();

        return new RecomendacionViajeResponseDTO(
                recomendacionIA.getRecomendacionIaId(),
                consulta.getConsultaViajeId(),
                nombreCliente,
                nombreDestino,
                recomendacionIA.getResumen(),
                recomendacionIA.getRecomendaciones(),
                recomendacionIA.getAdvertencias(),
                recomendacionIA.getFechaGeneracion()
        );
    }
}
