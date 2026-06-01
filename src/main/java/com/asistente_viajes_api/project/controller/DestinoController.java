package com.asistente_viajes_api.project.controller;

import com.asistente_viajes_api.project.dto.destino.DestinoRequestDTO;
import com.asistente_viajes_api.project.dto.destino.DestinoResponseDTO;
import com.asistente_viajes_api.project.external.countries.dto.CountryInfoResponseDTO;
import com.asistente_viajes_api.project.external.weather.dto.WeatherInfoResponseDTO;
import com.asistente_viajes_api.project.service.IDestinoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/destinos")
public class DestinoController {

    @Autowired
    private IDestinoService destinoService;

    @PostMapping
    public ResponseEntity<DestinoResponseDTO> crearDestino(@Valid @RequestBody DestinoRequestDTO dto) {
        DestinoResponseDTO destinoCreado = destinoService.crearDestino(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(destinoCreado);
    }

    @GetMapping
    public ResponseEntity<List<DestinoResponseDTO>> listarDestinos() {
        List<DestinoResponseDTO> destinos = destinoService.listarDestinos();

        return ResponseEntity.ok(destinos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DestinoResponseDTO> buscarDestinoPorId(@PathVariable Long id) {

        DestinoResponseDTO destino = destinoService.buscarDestinoPorId(id);

        return ResponseEntity.ok(destino);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DestinoResponseDTO> actualizarDestino(@PathVariable Long id, @Valid @RequestBody DestinoRequestDTO dto) {

        DestinoResponseDTO destinoActualizado = destinoService.actualizarDestino(id, dto);

        return ResponseEntity.ok(destinoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDestino(@PathVariable Long id) {

        destinoService.eliminarDestino(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar-pais/{pais}")
    public ResponseEntity<CountryInfoResponseDTO> buscarPais(@PathVariable String pais) {

        CountryInfoResponseDTO response = destinoService.buscarPais(pais);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/clima")
    public ResponseEntity<WeatherInfoResponseDTO> obtenerClimaPorDestino(
            @PathVariable Long id
    ) {
        WeatherInfoResponseDTO response = destinoService.obtenerClimaPorDestino(id);

        return ResponseEntity.ok(response);
    }



}
