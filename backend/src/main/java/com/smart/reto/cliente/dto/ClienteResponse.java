package com.smart.reto.cliente.dto;

public record ClienteResponse(
        Long id,
        String nombre,
        String correo,
        String telefono
) {
}
