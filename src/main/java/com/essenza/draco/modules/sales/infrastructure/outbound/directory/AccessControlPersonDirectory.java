package com.essenza.draco.modules.sales.infrastructure.outbound.directory;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.essenza.draco.modules.sales.application.output.gateway.PersonDirectory;

/**
 * Consulta de personas en access_control con un token de servicio (client_credentials,
 * scope {@code persons.read}). El token se reutiliza hasta 30 s antes de vencer. Sin
 * secreto configurado la integración queda deshabilitada y la orden se captura a mano.
 *
 * <p>access_control solo devuelve personas cuyo usuario está asignado a la empresa de la tienda
 * (la que tiene ligada este cliente de servicio). Cada consulta envía el tipo de documento
 * ({@code typeId}) y la empresa ({@code companyId}) que indica el panel; sin empresa la consulta
 * no se hace (DISABLED). Un 403 persistente significa que access_control no permite esa empresa
 * a este cliente y también se informa como DISABLED.
 */
@Component
public class AccessControlPersonDirectory implements PersonDirectory {

    private static final Logger log = LoggerFactory.getLogger(AccessControlPersonDirectory.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    static final String SCOPE = "persons.read";
    static final String LOOKUP_PATH = "/api/access_control/persons/lookup";

    private final String baseUrl;
    private final String tokenUrl;
    private final String clientId;
    private final String clientSecret;
    private final HttpClient http;
    private final ObjectMapper json = new ObjectMapper();

    private String token;
    private Instant tokenExpiresAt = Instant.EPOCH;

    public AccessControlPersonDirectory(
            @Value("${essenza.access-control.base-url:${essenza.security.issuer:http://localhost:8000}}") String baseUrl,
            @Value("${essenza.access-control.token-url:}") String tokenUrl,
            @Value("${essenza.access-control.client-id:essenza-tienda-service}") String clientId,
            @Value("${essenza.access-control.client-secret:}") String clientSecret) {
        this.baseUrl = trimSlash(baseUrl);
        this.tokenUrl = tokenUrl == null || tokenUrl.isBlank() ? this.baseUrl + "/oauth2/token" : tokenUrl.trim();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.http = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
    }

    @Override
    public Result findByDocument(Long companyId, String docType, String docNumber) {
        if (clientSecret == null || clientSecret.isBlank()) {
            return Result.of(Status.DISABLED, "Falta essenza.access-control.client-secret");
        }
        if (companyId == null || companyId <= 0) {
            return Result.of(Status.DISABLED, "Falta la empresa (companyId)");
        }
        try {
            String url = lookupUrl(companyId, docType, docNumber);
            HttpResponse<String> response = lookup(url, false);
            if (response.statusCode() == 401 || response.statusCode() == 403) {
                response = lookup(url, true);
            }
            int status = response.statusCode();
            if (status == 403) {
                log.warn("access_control rechazó la consulta de la empresa {} para {}", companyId, clientId);
                return Result.of(Status.DISABLED, "Empresa " + companyId + " no permitida para " + clientId);
            }
            if (status == 404) {
                return Result.of(Status.NOT_FOUND, null);
            }
            if (status < 200 || status >= 300) {
                log.warn("access_control respondió {} a la consulta de personas", status);
                return Result.of(Status.UNAVAILABLE, "HTTP " + status);
            }
            return new Result(Status.FOUND, toPerson(json.readTree(response.body())), null);
        } catch (IOException ex) {
            log.warn("No se pudo consultar access_control: {}", ex.getMessage());
            return Result.of(Status.UNAVAILABLE, ex.getMessage());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return Result.of(Status.UNAVAILABLE, "interrumpido");
        }
    }

    private HttpResponse<String> lookup(String url, boolean renewToken) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(TIMEOUT)
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token(renewToken))
                .GET().build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    String lookupUrl(Long companyId, String docType, String docNumber) {
        StringBuilder url = new StringBuilder(baseUrl).append(LOOKUP_PATH)
                .append("?cardId=").append(URLEncoder.encode(docNumber, StandardCharsets.UTF_8))
                .append("&companyId=").append(companyId);
        if (docType != null && !docType.isBlank()) {
            url.append("&typeId=").append(URLEncoder.encode(docType.trim(), StandardCharsets.UTF_8));
        }
        return url.toString();
    }

    private synchronized String token(boolean renew) throws IOException, InterruptedException {
        if (!renew && token != null && Instant.now().isBefore(tokenExpiresAt)) {
            return token;
        }
        String basic = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder(URI.create(tokenUrl)).timeout(TIMEOUT)
                .header("Authorization", "Basic " + basic)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials&scope="
                        + URLEncoder.encode(SCOPE, StandardCharsets.UTF_8)))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("access_control no entregó el token de servicio (HTTP " + response.statusCode() + ")");
        }
        JsonNode body = json.readTree(response.body());
        token = body.path("access_token").asText(null);
        if (token == null || token.isBlank()) {
            throw new IOException("access_control respondió sin access_token");
        }
        long expiresIn = body.path("expires_in").asLong(300);
        tokenExpiresAt = Instant.now().plusSeconds(Math.max(30, expiresIn - 30));
        return token;
    }

    static Person toPerson(JsonNode n) {
        JsonNode user = n.path("user");
        boolean hasUser = user.isObject();
        return new Person(longOrNull(n.path("client_id"), n.path("clientId")), text(n, "type_id", "typeId"),
                text(n, "card_id", "cardId"), text(n, "first_name", "firstName"), text(n, "second_name", "secondName"),
                text(n, "first_last_name", "firstLastName"), text(n, "second_last_name", "secondLastName"),
                text(n, "full_name", "fullName"), text(n, "status", "status"),
                hasUser ? longOrNull(user.path("user_id"), user.path("userId")) : null,
                hasUser ? text(user, "username", "username") : null,
                hasUser ? text(user, "status", "status") : null);
    }

    /** access_control puede serializar en camelCase o snake_case: se aceptan ambos. */
    private static String text(JsonNode node, String snake, String camel) {
        JsonNode value = node.hasNonNull(snake) ? node.get(snake) : node.get(camel);
        return value == null || value.isNull() || value.asText().isBlank() ? null : value.asText().trim();
    }

    private static Long longOrNull(JsonNode a, JsonNode b) {
        JsonNode value = a != null && !a.isMissingNode() && !a.isNull() ? a : b;
        return value == null || value.isMissingNode() || value.isNull() ? null : value.asLong();
    }

    private static String trimSlash(String url) {
        String value = url == null ? "" : url.trim();
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
