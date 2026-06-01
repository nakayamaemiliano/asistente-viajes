package com.asistente_viajes_api.project.controller;

import com.asistente_viajes_api.project.dto.consultaViaje.ConsultaViajeRequestDTO;
import com.asistente_viajes_api.project.dto.consultaViaje.ConsultaViajeResponseDTO;
import com.asistente_viajes_api.project.service.IConsultaViajeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultas-viaje")
public class ConsultaViajeController {

    @Autowired
    private IConsultaViajeService consultaViajeService;


    @PostMapping
    public ResponseEntity<ConsultaViajeResponseDTO> crearConsultaViaje(
            @Valid @RequestBody ConsultaViajeRequestDTO dto
    ) {
        ConsultaViajeResponseDTO consultaCreada = consultaViajeService.crearConsultaViaje(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(consultaCreada);
    }

    @GetMapping
    public ResponseEntity<List<ConsultaViajeResponseDTO>> listarConsultasViaje() {
        List<ConsultaViajeResponseDTO> consultas = consultaViajeService.listarConsultasViaje();

        return ResponseEntity.ok(consultas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaViajeResponseDTO> buscarConsultaViajePorId(
            @PathVariable Long id
    ) {
        ConsultaViajeResponseDTO consulta = consultaViajeService.buscarConsultaViajePorId(id);

        return ResponseEntity.ok(consulta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultaViajeResponseDTO> actualizarConsultaViaje(
            @PathVariable Long id,
            @Valid @RequestBody ConsultaViajeRequestDTO dto
    ) {
        ConsultaViajeResponseDTO consultaActualizada = consultaViajeService.actualizarConsultaViaje(id, dto);

        return ResponseEntity.ok(consultaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarConsultaViaje(
            @PathVariable Long id
    ) {
        consultaViajeService.eliminarConsultaViaje(id);

        return ResponseEntity.noContent().build();
    }



}
