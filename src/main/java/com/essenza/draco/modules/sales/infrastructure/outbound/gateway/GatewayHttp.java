package com.essenza.draco.modules.sales.infrastructure.outbound.gateway;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.essenza.draco.modules.sales.application.output.gateway.PaymentGateway.GatewayException;

/** Cliente HTTP mínimo para las APIs de las pasarelas (JSON, timeouts cortos). */
@Component
public class GatewayHttp {

    private static final Duration TIMEOUT = Duration.ofSeconds(15);

    private final HttpClient client;
    private final ObjectMapper json = new ObjectMapper();

    public GatewayHttp() {
        this(HttpClient.newBuilder().connectTimeout(TIMEOUT).build());
    }

    GatewayHttp(HttpClient client) {
        this.client = client;
    }

    ObjectMapper json() {
        return json;
    }

    /** Respuesta: código HTTP y cuerpo JSON (nodo vacío si no hay cuerpo). */
    record Response(int status, JsonNode body) {
        boolean ok() {
            return status >= 200 && status < 300;
        }
    }

    Response get(String url, Map<String, String> headers) {
        return send(HttpRequest.newBuilder(URI.create(url)).GET(), headers);
    }

    Response post(String url, Object body, Map<String, String> headers) {
        try {
            String payload = json.writeValueAsString(body);
            return send(HttpRequest.newBuilder(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload)), headers);
        } catch (IOException ex) {
            throw new GatewayException("No se pudo serializar la petición a la pasarela", ex);
        }
    }

    private Response send(HttpRequest.Builder builder, Map<String, String> headers) {
        headers.forEach(builder::header);
        try {
            HttpResponse<String> response = client.send(builder.timeout(TIMEOUT).build(), HttpResponse.BodyHandlers.ofString());
            String text = response.body();
            JsonNode node = text == null || text.isBlank() ? json.createObjectNode() : json.readTree(text);
            return new Response(response.statusCode(), node);
        } catch (IOException ex) {
            throw new GatewayException("No se pudo comunicar con la pasarela: " + ex.getMessage(), ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new GatewayException("Comunicación con la pasarela interrumpida", ex);
        }
    }
}
