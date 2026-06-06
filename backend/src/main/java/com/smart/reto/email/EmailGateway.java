package com.smart.reto.email;

public interface EmailGateway {

    void enviarConAdjunto(String destinatario, String asunto, String cuerpo,
                          byte[] adjunto, String nombreAdjunto);
}
