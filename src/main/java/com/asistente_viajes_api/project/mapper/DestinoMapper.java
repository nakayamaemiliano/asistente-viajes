package com.asistente_viajes_api.project.mapper;

import com.asistente_viajes_api.project.dto.destino.DestinoRequestDTO;
import com.asistente_viajes_api.project.dto.destino.DestinoResponseDTO;
import com.asistente_viajes_api.project.entity.Destino;

public class DestinoMapper {
    public static Destino toEntity(DestinoRequestDTO dto) {
        Destino destino = new Destino();

        destino.setCiudad(dto.ciudad());
        destino.setPais(dto.pais());
        destino.setCodigoPais(dto.codigoPais());
        destino.setMoneda(dto.moneda());
        destino.setIdiomaPrincipal(dto.idiomaPrincipal());
        destino.setRegion(dto.region());
        destino.setLatitud(dto.latitud());
        destino.setLongitud(dto.longitud());

        return destino;
    }

    public static DestinoResponseDTO toResponseDTO(Destino destino) {
        return new DestinoResponseDTO(
                destino.getDestinoId(),
                destino.getCiudad(),
                destino.getPais(),
                destino.getCodigoPais(),
                destino.getMoneda(),
                destino.getIdiomaPrincipal(),
                destino.getRegion(),
                destino.getLatitud(),
                destino.getLongitud()
        );
    }

    public static void updateEntityFromDTO(DestinoRequestDTO dto, Destino destino) {
        destino.setCiudad(dto.ciudad());
        destino.setPais(dto.pais());
        destino.setCodigoPais(dto.codigoPais());
        destino.setMoneda(dto.moneda());
        destino.setIdiomaPrincipal(dto.idiomaPrincipal());
        destino.setRegion(dto.region());
        destino.setLatitud(dto.latitud());
        destino.setLongitud(dto.longitud());
    }
}
