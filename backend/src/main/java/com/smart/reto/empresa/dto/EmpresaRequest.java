package com.smart.reto.empresa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmpresaRequest(

        @NotBlank(message = "El NIT es obligatorio")
        @Size(max = 20, message = "El NIT no puede superar 20 caracteres")
        String nit,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
        String nombre,

        @Size(max = 200, message = "La direccion no puede superar 200 caracteres")
        String direccion,

        @Size(max = 30, message = "El telefono no puede superar 30 caracteres")
        String telefono
) {
}
