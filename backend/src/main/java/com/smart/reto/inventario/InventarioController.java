package com.smart.reto.inventario;

import com.smart.reto.inventario.dto.EnviarInventarioRequest;
import com.smart.reto.inventario.dto.InventarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventario")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Inventario", description = "Consulta, PDF y envio por correo del inventario")
public class InventarioController {

    private final InventarioService service;

    public InventarioController(InventarioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar inventario (opcionalmente filtrado por empresa)")
    public List<InventarioResponse> listar(@RequestParam(required = false) String empresaNit) {
        return service.listar(empresaNit);
    }

    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Descargar el inventario en PDF")
    public ResponseEntity<byte[]> descargarPdf(@RequestParam(required = false) String empresaNit) {
        byte[] pdf = service.generarPdf(empresaNit);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventario.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/enviar")
    @Operation(summary = "Enviar el PDF del inventario a un correo")
    public ResponseEntity<String> enviar(@Valid @RequestBody EnviarInventarioRequest request) {
        service.enviarPdfPorCorreo(request.destinatario(), request.empresaNit());
        return ResponseEntity.ok("Correo encolado/enviado a " + request.destinatario());
    }

    @PostMapping("/cantidad")
    @Operation(summary = "Actualizar la cantidad de un producto en inventario")
    public InventarioResponse actualizar(@RequestParam Long productoId,
                                         @RequestParam Integer cantidad) {
        return service.actualizarCantidad(productoId, cantidad);
    }
}
