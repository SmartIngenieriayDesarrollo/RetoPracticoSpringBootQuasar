package com.smart.reto.empresa;

import com.smart.reto.empresa.dto.EmpresaRequest;
import com.smart.reto.empresa.dto.EmpresaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/empresas")
@Tag(name = "Empresas", description = "Gestion y consulta de empresas")
public class EmpresaController {

    private final EmpresaService service;

    public EmpresaController(EmpresaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar empresas (publico)")
    public List<EmpresaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{nit}")
    @Operation(summary = "Obtener empresa por NIT (publico)")
    public EmpresaResponse obtener(@PathVariable String nit) {
        return service.obtener(nit);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear empresa (ADMIN)")
    public ResponseEntity<EmpresaResponse> crear(@Valid @RequestBody EmpresaRequest request) {
        EmpresaResponse creada = service.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{nit}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Editar empresa (ADMIN)")
    public EmpresaResponse editar(@PathVariable String nit,
                                  @Valid @RequestBody EmpresaRequest request) {
        return service.editar(nit, request);
    }

    @DeleteMapping("/{nit}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar empresa (ADMIN)")
    public ResponseEntity<Void> eliminar(@PathVariable String nit) {
        service.eliminar(nit);
        return ResponseEntity.noContent().build();
    }
}
