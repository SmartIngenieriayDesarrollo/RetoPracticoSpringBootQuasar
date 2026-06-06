package com.smart.reto.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.smart.reto.inventario.dto.InventarioResponse;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PdfService {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public byte[] generarInventario(List<InventarioResponse> items, String titulo) {

        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

            Document documento = new Document(PageSize.A4, 36, 36, 54, 36);

            PdfWriter.getInstance(documento, salida);

            documento.open();

            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
            Paragraph parrafoTitulo = new Paragraph(titulo, fuenteTitulo);
            parrafoTitulo.setAlignment(Element.ALIGN_CENTER);
            parrafoTitulo.setSpacingAfter(8);
            documento.add(parrafoTitulo);

            Font fuenteFecha = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY);
            Paragraph fecha = new Paragraph(
                    "Generado: " + LocalDateTime.now().format(FORMATO_FECHA), fuenteFecha);
            fecha.setAlignment(Element.ALIGN_RIGHT);
            fecha.setSpacingAfter(12);
            documento.add(fecha);

            PdfPTable tabla = new PdfPTable(5);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{2f, 4f, 4f, 4f, 2f});

            agregarCeldaCabecera(tabla, "Codigo");
            agregarCeldaCabecera(tabla, "Producto");
            agregarCeldaCabecera(tabla, "Empresa");
            agregarCeldaCabecera(tabla, "NIT");
            agregarCeldaCabecera(tabla, "Cantidad");

            Font fuenteCelda = FontFactory.getFont(FontFactory.HELVETICA, 10);
            for (InventarioResponse item : items) {
                tabla.addCell(new Phrase(item.codigoProducto(), fuenteCelda));
                tabla.addCell(new Phrase(item.nombreProducto(), fuenteCelda));
                tabla.addCell(new Phrase(item.empresaNombre(), fuenteCelda));
                tabla.addCell(new Phrase(item.empresaNit(), fuenteCelda));

                PdfPCell celdaCantidad = new PdfPCell(
                        new Phrase(String.valueOf(item.cantidad()), fuenteCelda));
                celdaCantidad.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tabla.addCell(celdaCantidad);
            }

            documento.add(tabla);

            int totalUnidades = items.stream().mapToInt(InventarioResponse::cantidad).sum();
            Paragraph total = new Paragraph(
                    "Total de unidades: " + totalUnidades,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11));
            total.setSpacingBefore(12);
            documento.add(total);

            documento.close();

            return salida.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException("No se pudo generar el PDF del inventario", e);
        }
    }

    private void agregarCeldaCabecera(PdfPTable tabla, String texto) {
        Font fuente = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setBackgroundColor(new Color(60, 60, 60));
        celda.setPadding(5);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(celda);
    }
}
