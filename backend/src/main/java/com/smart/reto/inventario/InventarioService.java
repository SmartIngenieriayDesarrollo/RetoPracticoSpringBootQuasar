package com.smart.reto.inventario;

import com.smart.reto.email.EmailGateway;
import com.smart.reto.inventario.dto.InventarioResponse;
import com.smart.reto.pdf.PdfService;
import com.smart.reto.shared.RecursoNoEncontradoException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventarioService {

    private final InventarioRepository repository;
    private final PdfService pdfService;
    private final EmailGateway emailGateway;

    public InventarioService(InventarioRepository repository,
                             PdfService pdfService,
                             EmailGateway emailGateway) {
        this.repository = repository;
        this.pdfService = pdfService;
        this.emailGateway = emailGateway;
    }

    @Transactional(readOnly = true)
    public List<InventarioResponse> listar(String empresaNit) {
        List<Inventario> datos = (empresaNit == null || empresaNit.isBlank())
                ? repository.findAllConProducto()
                : repository.findByEmpresaNit(empresaNit);
        return datos.stream().map(this::aResponse).toList();
    }

    @Transactional(readOnly = true)
    public byte[] generarPdf(String empresaNit) {
        List<InventarioResponse> items = listar(empresaNit);
        String titulo = (empresaNit == null || empresaNit.isBlank())
                ? "Reporte de Inventario General"
                : "Reporte de Inventario - Empresa " + empresaNit;
        return pdfService.generarInventario(items, titulo);
    }

    @Transactional(readOnly = true)
    public void enviarPdfPorCorreo(String destinatario, String empresaNit) {
        byte[] pdf = generarPdf(empresaNit);
        String asunto = "Reporte de inventario";
        String cuerpo = "Adjunto encontrara el reporte de inventario solicitado.";
        emailGateway.enviarConAdjunto(destinatario, asunto, cuerpo, pdf, "inventario.pdf");
    }

    @Transactional
    public InventarioResponse actualizarCantidad(Long productoId, Integer cantidad) {
        Inventario inv = repository.findByProductoId(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No hay inventario para el producto " + productoId));
        inv.setCantidad(cantidad);
        return aResponse(inv);
    }

    private InventarioResponse aResponse(Inventario inv) {
        var producto = inv.getProducto();
        var empresa = producto.getEmpresa();
        return new InventarioResponse(
                inv.getId(),
                producto.getId(),
                producto.getCodigo(),
                producto.getNombre(),
                empresa.getNit(),
                empresa.getNombre(),
                inv.getCantidad()
        );
    }
}
