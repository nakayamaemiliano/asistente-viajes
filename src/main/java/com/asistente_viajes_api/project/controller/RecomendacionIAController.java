package com.asistente_viajes_api.project.controller;

import com.asistente_viajes_api.project.dto.recomendacionIa.RecomendacionViajeResponseDTO;
import com.asistente_viajes_api.project.service.IRecomendacionIAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/consultas-viaje")
public class RecomendacionIAController {
    @Autowired
    private IRecomendacionIAService recomendacionIAService;

    @PostMapping("/{id}/generar-recomendacion")
    public ResponseEntity<RecomendacionViajeResponseDTO> generarRecomendacion(
            @PathVariable Long id
    ) {
        RecomendacionViajeResponseDTO recomendacion =
                recomendacionIAService.generarRecomendacion(id);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(recomendacion);
    }

    @GetMapping("/{id}/recomendacion")
    public ResponseEntity<RecomendacionViajeResponseDTO> obtenerRecomendacion(
            @PathVariable Long id
    ) {
        RecomendacionViajeResponseDTO recomendacion =
                recomendacionIAService.obtenerRecomendacionPorConsulta(id);

        return ResponseEntity.ok(recomendacion);
    }

}
