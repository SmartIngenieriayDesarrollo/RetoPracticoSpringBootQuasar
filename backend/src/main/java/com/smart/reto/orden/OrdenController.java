package com.smart.reto.orden;

import com.smart.reto.orden.dto.OrdenRequest;
import com.smart.reto.orden.dto.OrdenResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ordenes")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Ordenes", description = "Gestion de ordenes de clientes")
public class OrdenController {

    private final OrdenService service;

    public OrdenController(OrdenService service) {
        this.service = service;
    }

    @GetMapping
    public List<OrdenResponse> listarPorCliente(@RequestParam Long clienteId) {
        return service.listarPorCliente(clienteId);
    }

    @PostMapping
    public ResponseEntity<OrdenResponse> crear(@Valid @RequestBody OrdenRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }
}
