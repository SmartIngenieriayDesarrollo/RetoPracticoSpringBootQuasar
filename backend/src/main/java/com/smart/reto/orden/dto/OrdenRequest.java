package com.smart.reto.orden.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record OrdenRequest(

        @NotNull(message = "El id de cliente es obligatorio")
        Long clienteId,

        @Size(min = 3, max = 3, message = "La moneda debe tener 3 caracteres (ISO 4217)")
        String moneda,

        @NotEmpty(message = "La orden debe tener al menos un item")
        @Valid
        List<OrdenItemRequest> items
) {
}
