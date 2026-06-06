package com.smart.reto.cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150)
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato valido")
        String correo,

        @Size(max = 30)
        String telefono
) {
}
