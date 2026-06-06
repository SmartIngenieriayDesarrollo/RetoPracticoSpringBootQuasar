package com.smart.reto.inventario.dto;

public record InventarioResponse(
        Long id,
        Long productoId,
        String codigoProducto,
        String nombreProducto,
        String empresaNit,
        String empresaNombre,
        Integer cantidad
) {
}
