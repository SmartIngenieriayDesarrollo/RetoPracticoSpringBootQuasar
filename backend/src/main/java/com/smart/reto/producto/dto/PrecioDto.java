package com.smart.reto.producto.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record PrecioDto(

        @NotBlank(message = "La moneda es obligatoria")
        @Size(min = 3, max = 3, message = "La moneda debe tener 3 caracteres (ISO 4217)")
        String moneda,

        @NotNull(message = "El valor es obligatorio")
        @DecimalMin(value = "0.0", message = "El valor no puede ser negativo")
        BigDecimal valor
) {
}
