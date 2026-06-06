package com.smart.reto.producto.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;

public record ProductoRequest(

        @NotBlank(message = "El codigo es obligatorio")
        @Size(max = 50)
        String codigo,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150)
        String nombre,

        String caracteristicas,

        @NotBlank(message = "El NIT de la empresa es obligatorio")
        String empresaNit,

        @NotEmpty(message = "Debe indicar al menos un precio")
        @Valid
        List<PrecioDto> precios,

        Set<Long> categoriaIds
) {
}
