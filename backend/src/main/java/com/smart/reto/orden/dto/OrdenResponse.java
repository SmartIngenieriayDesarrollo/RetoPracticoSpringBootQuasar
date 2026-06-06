package com.smart.reto.orden.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrdenResponse(
        Long id,
        Long clienteId,
        String clienteNombre,
        OffsetDateTime fecha,
        String estado,
        List<OrdenItemResponse> items,
        BigDecimal total
) {

    public record OrdenItemResponse(
            Long productoId,
            String nombreProducto,
            Integer cantidad,
            BigDecimal precioUnitario,
            String moneda,
            BigDecimal subtotal
    ) {
    }
}
