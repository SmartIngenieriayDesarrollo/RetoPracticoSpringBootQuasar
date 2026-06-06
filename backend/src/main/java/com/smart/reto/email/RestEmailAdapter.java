package com.smart.reto.email;

import java.util.Base64;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(name = "app.email.modo", havingValue = "rest")
public class RestEmailAdapter implements EmailGateway {

    private static final Logger log = LoggerFactory.getLogger(RestEmailAdapter.class);

    private final RestClient restClient;
    private final String remitente;

    public RestEmailAdapter(@Value("${app.email.api-url}") String apiUrl,
                            @Value("${app.email.api-key}") String apiKey,
                            @Value("${app.email.remitente}") String remitente) {
        this.remitente = remitente;
        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    @Override
    public void enviarConAdjunto(String destinatario, String asunto, String cuerpo,
                                 byte[] adjunto, String nombreAdjunto) {

        String adjuntoBase64 = Base64.getEncoder().encodeToString(adjunto);

        Map<String, Object> payload = Map.of(
                "from", remitente,
                "to", destinatario,
                "subject", asunto,
                "text", cuerpo,
                "attachments", java.util.List.of(Map.of(
                        "filename", nombreAdjunto,
                        "content", adjuntoBase64,
                        "type", "application/pdf"
                ))
        );

        restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();

        log.info("Correo enviado a {} con adjunto {} ({} bytes) via API REST externa",
                destinatario, nombreAdjunto, adjunto.length);
    }
}
