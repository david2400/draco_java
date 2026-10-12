package com.essenza.draco.modules.sales.infrastructure.outbound.gateway;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import com.essenza.draco.modules.sales.application.output.gateway.PaymentGateway;
import com.essenza.draco.modules.sales.domain.model.payment.GatewayProvider;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentStatus;

/**
 * Mercado Pago: Checkout Pro (preferencia con {@code external_reference} = referencia del pago),
 * webhooks firmados con {@code x-signature} y consulta/reembolso de pagos por API.
 *
 * <ul>
 *   <li>Firma: HMAC-SHA256(secreto, "id:{data.id};request-id:{x-request-id};ts:{ts};") = v1.
 *       data.id va en minúsculas si es alfanumérico.</li>
 *   <li>La notificación solo trae el id del pago: el estado se consulta en /v1/payments/{id}.</li>
 *   <li>Token TEST-… usa el link de pruebas (sandbox_init_point).</li>
 * </ul>
 */
@Component
public class MercadoPagoGateway implements PaymentGateway {

    private final PaymentsSettings settings;
    private final GatewayHttp http;

    public MercadoPagoGateway(PaymentsSettings settings, GatewayHttp http) {
        this.settings = settings;
        this.http = http;
    }

    @Override
    public GatewayProvider provider() {
        return GatewayProvider.MERCADO_PAGO;
    }

    @Override
    public boolean configured() {
        return !settings.getMercadoPagoAccessToken().isEmpty() && !settings.getMercadoPagoWebhookSecret().isEmpty();
    }

    @Override
    public String environment() {
        return settings.getMercadoPagoAccessToken().startsWith("TEST-") ? "test" : "production";
    }

    private Map<String, String> auth() {
        return Map.of("Authorization", "Bearer " + settings.getMercadoPagoAccessToken());
    }

    @Override
    public CheckoutSession createCheckout(CheckoutRequest request) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", request.paymentReference());
        item.put("title", request.description());
        item.put("quantity", 1);
        item.put("currency_id", request.currency());
        item.put("unit_price", request.amount());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("items", List.of(item));
        body.put("external_reference", request.paymentReference());
        body.put("notification_url", PaymentGatewaysAdapter.webhookUrl(settings, GatewayProvider.MERCADO_PAGO));
        if (request.returnUrl() != null) {
            body.put("back_urls", Map.of("success", request.returnUrl(), "failure", request.returnUrl(),
                    "pending", request.returnUrl()));
            body.put("auto_return", "approved");
        }
        if (request.payerEmail() != null) {
            body.put("payer", Map.of("email", request.payerEmail()));
        }
        if (request.expiresAt() != null) {
            body.put("expires", true);
            body.put("expiration_date_to", request.expiresAt().truncatedTo(ChronoUnit.MILLIS).toString());
        }
        body.put("statement_descriptor", "ESSENZA");
        GatewayHttp.Response response = http.post(settings.getMercadoPagoApiUrl() + "/checkout/preferences", body, auth());
        if (!response.ok()) {
            throw new GatewayException("Mercado Pago respondió " + response.status() + " al crear el link de pago: "
                    + response.body().path("message").asText(""));
        }
        String url = "test".equals(environment()) && response.body().hasNonNull("sandbox_init_point")
                ? response.body().get("sandbox_init_point").asText()
                : response.body().path("init_point").asText(null);
        if (url == null || url.isBlank()) {
            throw new GatewayException("Mercado Pago no devolvió el link de pago.");
        }
        // El id de la preferencia no es el del pago: el pago se asocia por external_reference.
        return new CheckoutSession(url, null);
    }

    @Override
    public Optional<GatewayStatus> fetchStatus(String paymentReference, String providerReference) {
        JsonNode payment;
        if (providerReference != null && !providerReference.isBlank()) {
            GatewayHttp.Response response = http.get(settings.getMercadoPagoApiUrl() + "/v1/payments/"
                    + URLEncoder.encode(providerReference, StandardCharsets.UTF_8), auth());
            if (response.status() == 404) {
                return Optional.empty();
            }
            ensureOk(response, "consultar el pago");
            payment = response.body();
        } else {
            GatewayHttp.Response response = http.get(settings.getMercadoPagoApiUrl()
                    + "/v1/payments/search?sort=date_created&criteria=desc&external_reference="
                    + URLEncoder.encode(paymentReference, StandardCharsets.UTF_8), auth());
            ensureOk(response, "buscar el pago");
            JsonNode results = response.body().path("results");
            if (!results.isArray() || results.isEmpty()) {
                return Optional.empty();
            }
            payment = results.get(0);
        }
        return Optional.of(toStatus(payment));
    }

    @Override
    public WebhookEvent parseWebhook(Map<String, String> headers, Map<String, String> query, String body) {
        if (!configured()) {
            throw new InvalidWebhookException("Mercado Pago no está configurado.");
        }
        JsonNode node;
        try {
            node = body == null || body.isBlank() ? http.json().createObjectNode() : http.json().readTree(body);
        } catch (Exception ex) {
            throw new InvalidWebhookException("Notificación de Mercado Pago mal formada.");
        }
        String type = query.getOrDefault("type", node.path("type").asText(query.getOrDefault("topic", "")));
        String dataId = query.get("data.id");
        if (dataId == null || dataId.isBlank()) {
            dataId = node.path("data").path("id").asText(null);
        }
        String signature = SandboxGateway.header(headers, "x-signature");
        String requestId = SandboxGateway.header(headers, "x-request-id");
        if (dataId == null || signature == null) {
            throw new InvalidWebhookException("Notificación de Mercado Pago sin id o sin firma.");
        }
        if (!validSignature(signature, requestId, dataId)) {
            throw new InvalidWebhookException("Firma de Mercado Pago inválida.");
        }
        if (!"payment".equalsIgnoreCase(type)) {
            return new WebhookEvent(type + ":" + dataId + ":" + requestId, null, null, null, null, null, false);
        }
        return new WebhookEvent("payment:" + dataId, null, dataId, null, null, null, true);
    }

    /** x-signature: "ts=...,v1=..."; manifiesto "id:{id};request-id:{req};ts:{ts};". */
    boolean validSignature(String header, String requestId, String dataId) {
        String ts = null;
        String v1 = null;
        for (String part : header.split(",")) {
            String[] pair = part.trim().split("=", 2);
            if (pair.length == 2 && pair[0].trim().equals("ts")) {
                ts = pair[1].trim();
            } else if (pair.length == 2 && pair[0].trim().equals("v1")) {
                v1 = pair[1].trim();
            }
        }
        if (ts == null || v1 == null) {
            return false;
        }
        String id = dataId.matches(".*[A-Za-z].*") ? dataId.toLowerCase(Locale.ROOT) : dataId;
        StringBuilder manifest = new StringBuilder("id:").append(id).append(';');
        if (requestId != null && !requestId.isBlank()) {
            manifest.append("request-id:").append(requestId).append(';');
        }
        manifest.append("ts:").append(ts).append(';');
        return Signatures.sameHex(Signatures.hmacSha256Hex(settings.getMercadoPagoWebhookSecret(), manifest.toString()), v1);
    }

    static GatewayStatus toStatus(JsonNode payment) {
        String raw = payment.path("status").asText("pending");
        PaymentStatus status = switch (raw) {
            case "approved" -> PaymentStatus.APPROVED;
            case "rejected" -> PaymentStatus.DECLINED;
            case "cancelled" -> PaymentStatus.EXPIRED;
            case "refunded", "charged_back" -> PaymentStatus.REFUNDED;
            default -> PaymentStatus.PENDING; // pending, in_process, authorized, in_mediation
        };
        BigDecimal amount = payment.hasNonNull("transaction_amount") ? new BigDecimal(payment.get("transaction_amount").asText()) : null;
        String method = payment.path("payment_method_id").asText("");
        String detail = ("Mercado Pago " + raw + (payment.hasNonNull("status_detail") ? " · " + payment.get("status_detail").asText() : "")
                + (method.isEmpty() ? "" : " · " + method)).trim();
        return new GatewayStatus(payment.path("external_reference").asText(null), payment.path("id").asText(null), status,
                amount, detail);
    }

    @Override
    public boolean supportsRefund() {
        return true;
    }

    @Override
    public void refund(String providerReference, BigDecimal amount, String idempotencyKey) {
        GatewayHttp.Response response = http.post(settings.getMercadoPagoApiUrl() + "/v1/payments/"
                        + URLEncoder.encode(providerReference, StandardCharsets.UTF_8) + "/refunds",
                Map.of("amount", amount),
                Map.of("Authorization", "Bearer " + settings.getMercadoPagoAccessToken(), "X-Idempotency-Key", idempotencyKey));
        if (!response.ok()) {
            throw new GatewayException("Mercado Pago no aceptó el reembolso (" + response.status() + "): "
                    + response.body().path("message").asText(""));
        }
    }

    private static void ensureOk(GatewayHttp.Response response, String action) {
        if (!response.ok()) {
            throw new GatewayException("Mercado Pago respondió " + response.status() + " al " + action + ".");
        }
    }
}
