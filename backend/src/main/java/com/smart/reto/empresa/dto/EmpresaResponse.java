package com.smart.reto.empresa.dto;

public record EmpresaResponse(
        String nit,
        String nombre,
        String direccion,
        String telefono
) {
}
