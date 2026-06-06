package com.smart.reto.security.dto;

import java.util.List;

public record LoginResponse(
        String token,
        String tipo,
        String correo,
        List<String> roles
) {
    public static LoginResponse bearer(String token, String correo, List<String> roles) {
        return new LoginResponse(token, "Bearer", correo, roles);
    }
}
