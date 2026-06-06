package com.smart.reto.email;

import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.email.modo", havingValue = "simulado", matchIfMissing = true)
public class SimuladoEmailAdapter implements EmailGateway {

    private static final Logger log = LoggerFactory.getLogger(SimuladoEmailAdapter.class);

    @Override
    public void enviarConAdjunto(String destinatario, String asunto, String cuerpo,
                                 byte[] adjunto, String nombreAdjunto) {

        int tamano = adjunto != null ? adjunto.length : 0;
        String muestraBase64 = adjunto != null
                ? Base64.getEncoder().encodeToString(adjunto).substring(0, Math.min(40, tamano))
                : "";

        log.info("[EMAIL SIMULADO] Se habria enviado un correo:");
        log.info("  -> Para: {}", destinatario);
        log.info("  -> Asunto: {}", asunto);
        log.info("  -> Cuerpo: {}", cuerpo);
        log.info("  -> Adjunto: {} ({} bytes), inicio base64: {}...",
                nombreAdjunto, tamano, muestraBase64);
    }
}
