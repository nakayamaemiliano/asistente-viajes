package com.asistente_viajes_api.project.controller;

import com.asistente_viajes_api.project.dto.cliente.ClienteRequestDTO;
import com.asistente_viajes_api.project.dto.cliente.ClienteResponseDTO;
import com.asistente_viajes_api.project.service.IClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    @Autowired
    private IClienteService clienteService;


    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarClientes() {
        List<ClienteResponseDTO> clientes = clienteService.listarClientes();

        return ResponseEntity.ok(clientes);
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> crearCliente(@Valid @RequestBody ClienteRequestDTO dto){
        ClienteResponseDTO clienteCreado = clienteService.crearCliente(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clienteCreado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarClientePorId(@PathVariable Long id ){
        ClienteResponseDTO cliente = clienteService.buscarClientePorId(id);

        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO dto
    ) {
        ClienteResponseDTO clienteActualizado = clienteService.actualizarCliente(id, dto);

        return ResponseEntity.ok(clienteActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(
            @PathVariable Long id
    ) {
        clienteService.eliminarCliente(id);

        return ResponseEntity.noContent().build();
    }






}
