package com.smart.reto.producto;

import com.smart.reto.categoria.dto.CategoriaResponse;
import com.smart.reto.producto.dto.PrecioDto;
import com.smart.reto.producto.dto.ProductoResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public ProductoResponse aResponse(Producto p) {

        var precios = p.getPrecios().stream()
                .map(pr -> new PrecioDto(pr.getMoneda(), pr.getValor()))
                .toList();

        var categorias = p.getCategorias().stream()
                .map(c -> new CategoriaResponse(c.getId(), c.getNombre(), c.getDescripcion()))
                .toList();

        return new ProductoResponse(
                p.getId(),
                p.getCodigo(),
                p.getNombre(),
                p.getCaracteristicas(),
                p.getEmpresa().getNit(),
                p.getEmpresa().getNombre(),
                precios,
                categorias
        );
    }
}
