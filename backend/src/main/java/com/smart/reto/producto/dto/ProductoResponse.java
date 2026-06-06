package com.smart.reto.producto.dto;

import com.smart.reto.categoria.dto.CategoriaResponse;
import java.util.List;

public record ProductoResponse(
        Long id,
        String codigo,
        String nombre,
        String caracteristicas,
        String empresaNit,
        String empresaNombre,
        List<PrecioDto> precios,
        List<CategoriaResponse> categorias
) {
}
