package com.essenza.draco.modules.sales.infrastructure.inbound.rest;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.essenza.draco.modules.sales.application.input.payment.HandleGatewayWebhookUseCase;
import com.essenza.draco.modules.sales.application.output.gateway.PaymentGateway;

/**
 * Webhooks de las pasarelas. Debe ser público (sin token): si se exige autenticación, agregar
 * {@code /api/shop/sales/payments/webhooks/**} a las rutas públicas. La autenticidad la da la firma.
 */
@Slf4j
@RestController
@RequestMapping("/sales/payments/webhooks")
@Tag(name = "Payments")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final HandleGatewayWebhookUseCase handleWebhook;

    @Operation(summary = "Webhook", description = "Notificación de la pasarela (wompi, mercado_pago, sandbox). 401 si la firma no es válida.")
    @PostMapping("/{provider}")
    public ResponseEntity<Map<String, Object>> receive(@PathVariable String provider,
                                                       @RequestBody(required = false) String body,
                                                       HttpServletRequest request) {
        Map<String, String> headers = new LinkedHashMap<>();
        for (String name : Collections.list(request.getHeaderNames())) {
            headers.put(name.toLowerCase(), request.getHeader(name));
        }
        Map<String, String> query = new LinkedHashMap<>();
        request.getParameterMap().forEach((key, values) -> query.put(key, values.length == 0 ? null : values[0]));
        try {
            boolean applied = handleWebhook.handle(provider, headers, query, body);
            return ResponseEntity.ok(Map.of("received", true, "applied", applied));
        } catch (PaymentGateway.InvalidWebhookException ex) {
            log.warn("Webhook {} rechazado: {}", provider, ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("received", false, "error", ex.getMessage()));
        }
    }
}
