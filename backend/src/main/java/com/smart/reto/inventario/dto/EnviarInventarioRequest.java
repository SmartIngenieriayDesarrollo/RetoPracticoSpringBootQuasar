package com.smart.reto.inventario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EnviarInventarioRequest(

        @NotBlank(message = "El correo destino es obligatorio")
        @Email(message = "El correo no tiene un formato valido")
        String destinatario,

        String empresaNit
) {
}
