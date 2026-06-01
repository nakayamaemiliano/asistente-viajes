package com.asistente_viajes_api.project.service;

import com.asistente_viajes_api.project.dto.recomendacionIa.RecomendacionViajeResponseDTO;
import com.asistente_viajes_api.project.entity.ConsultaViaje;
import com.asistente_viajes_api.project.entity.RecomendacionIA;
import com.asistente_viajes_api.project.enums.Estado;
import com.asistente_viajes_api.project.exception.BadRequestException;
import com.asistente_viajes_api.project.exception.ResourceNotFoundException;
import com.asistente_viajes_api.project.external.ai.AiClient;
import com.asistente_viajes_api.project.external.ai.dto.AiRecommendationResponseDTO;
import com.asistente_viajes_api.project.external.ai.dto.AiResponseDTO;
import com.asistente_viajes_api.project.mapper.RecomendacionIAMapper;
import com.asistente_viajes_api.project.repository.IConsultaViajeRepository;
import com.asistente_viajes_api.project.repository.IRecomendacionIARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RecomendacionIAServiceImp implements IRecomendacionIAService {

    @Autowired
    private AiClient aiClient;
    @Autowired
    private IRecomendacionIARepository recomendacionIARepository;

    @Autowired
    private IConsultaViajeRepository consultaViajeRepository;

    @Override
    public RecomendacionViajeResponseDTO generarRecomendacion(Long consultaViajeId) {
        ConsultaViaje consultaViaje = consultaViajeRepository.findById(consultaViajeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Consulta de viaje no encontrada con id: " + consultaViajeId
                ));

        if (consultaViaje.getEstado() == Estado.CANCELADA) {
            throw new BadRequestException("No se puede generar una recomendación para una consulta cancelada");
        }

        if (recomendacionIARepository.existsByConsultaViaje(consultaViaje)) {
            RecomendacionIA recomendacionExistente = recomendacionIARepository.findByConsultaViaje(consultaViaje)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No se encontró la recomendación existente para la consulta con id: " + consultaViajeId
                    ));

            return RecomendacionIAMapper.toResponseDTO(recomendacionExistente);
        }

        RecomendacionIA recomendacionIA = new RecomendacionIA();

        recomendacionIA.setConsultaViaje(consultaViaje);

        String prompt = crearPromptParaIA(consultaViaje);

        AiRecommendationResponseDTO respuestaIA = aiClient.generarRecomendacion(prompt);

        recomendacionIA.setResumen(respuestaIA.resumen());
        recomendacionIA.setRecomendaciones(respuestaIA.recomendaciones());
        recomendacionIA.setAdvertencias(respuestaIA.advertencias());



        recomendacionIA.setFechaGeneracion(LocalDateTime.now());

        consultaViaje.setEstado(Estado.ANALIZADA);

        consultaViajeRepository.save(consultaViaje);

        RecomendacionIA recomendacionGuardada = recomendacionIARepository.save(recomendacionIA);

        return RecomendacionIAMapper.toResponseDTO(recomendacionGuardada);

    }

    private String crearPromptParaIA(ConsultaViaje consultaViaje) {
        return """
            Sos un asistente experto en viajes.
            Generá una recomendación clara, útil y realista para una consulta de viaje.

            Datos de la consulta:
            Cliente: %s %s
            Destino: %s, %s
            Fecha de inicio: %s
            Fecha de fin: %s
            Cantidad de personas: %d
            Presupuesto: %s
            Intereses: %s

            Reglas:
            - Respondé solamente en JSON válido.
            - No uses markdown.
            - No uses ```json.
            - No inventes datos imposibles.
            - Sé concreto y útil.
            - El JSON debe tener exactamente estas claves:
              resumen
              recomendaciones
              advertencias

            Formato esperado:
            {
              "resumen": "texto breve",
              "recomendaciones": "texto con recomendaciones concretas",
              "advertencias": "texto con advertencias útiles"
            }
            """.formatted(
                consultaViaje.getCliente().getNombre(),
                consultaViaje.getCliente().getApellido(),
                consultaViaje.getDestino().getCiudad(),
                consultaViaje.getDestino().getPais(),
                consultaViaje.getFechaInicio(),
                consultaViaje.getFechaFin(),
                consultaViaje.getCantidadPersonas(),
                consultaViaje.getPresupuesto(),
                consultaViaje.getIntereses()
        );
    }

    @Override
    public RecomendacionViajeResponseDTO obtenerRecomendacionPorConsulta(Long consultaViajeId) {
        ConsultaViaje consultaViaje = consultaViajeRepository.findById(consultaViajeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Consulta de viaje no encontrada con id: " + consultaViajeId
                ));

        RecomendacionIA recomendacionIA = recomendacionIARepository.findByConsultaViaje(consultaViaje)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe una recomendación para la consulta con id: " + consultaViajeId
                ));

        return RecomendacionIAMapper.toResponseDTO(recomendacionIA);
    }


}
