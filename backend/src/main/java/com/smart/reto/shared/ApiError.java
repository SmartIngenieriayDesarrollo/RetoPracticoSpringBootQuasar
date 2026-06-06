package com.smart.reto.shared;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiError(
        OffsetDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String ruta,
        List<String> detalles
) {

    public static ApiError de(int status, String error, String mensaje, String ruta) {
        return new ApiError(OffsetDateTime.now(), status, error, mensaje, ruta, List.of());
    }
}
