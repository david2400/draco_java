package com.essenza.draco.modules.sales.infrastructure.inbound.rest;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.sales.application.input.payment.HandleGatewayWebhookUseCase;
import com.essenza.draco.modules.sales.infrastructure.outbound.gateway.PaymentsSettings;
import com.essenza.draco.modules.sales.infrastructure.outbound.gateway.SandboxGateway;

/**
 * Página de pago del simulador: muestra el monto y deja aprobar o rechazar. La decisión entra por
 * el mismo camino que un webhook real (firmado), y luego vuelve a la URL de retorno.
 */
@Hidden
@RestController
@RequestMapping("/sales/payments/sandbox")
@RequiredArgsConstructor
public class SandboxCheckoutController {

    private static final String REFERENCE = "^[A-Z0-9-]{4,60}$";

    private final SandboxGateway sandbox;
    private final PaymentsSettings settings;
    private final HandleGatewayWebhookUseCase handleWebhook;

    @GetMapping(value = "/{reference}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> page(@PathVariable String reference,
                                       @RequestParam(value = "amount", required = false) BigDecimal amount,
                                       @RequestParam(value = "order", required = false) Long order,
                                       @RequestParam(value = "return", required = false) String returnUrl) {
        if (!settings.isSandboxEnabled() || !reference.matches(REFERENCE)) {
            return ResponseEntity.notFound().build();
        }
        String action = settings.getPublicBaseUrl() + "/api/shop/sales/payments/sandbox/" + reference;
        String hiddenAmount = amount == null ? "" : "<input type='hidden' name='amount' value='" + amount.toPlainString() + "'>";
        String hiddenReturn = safeUrl(returnUrl) == null ? ""
                : "<input type='hidden' name='return' value='" + escape(returnUrl) + "'>";
        String html = """
                <!doctype html><html lang="es"><head><meta charset="utf-8">
                <meta name="viewport" content="width=device-width,initial-scale=1">
                <title>Pasarela de pruebas</title>
                <style>
                 body{font-family:system-ui,sans-serif;background:#f4f5f7;margin:0;display:grid;place-items:center;min-height:100vh;color:#111}
                 main{background:#fff;border-radius:16px;box-shadow:0 10px 30px rgba(0,0,0,.08);padding:32px;max-width:380px;width:calc(100%% - 32px)}
                 h1{font-size:18px;margin:0 0 4px} p{color:#555;margin:0 0 20px;font-size:14px}
                 .amount{font-size:32px;font-weight:700;margin:12px 0 24px}
                 .tag{display:inline-block;background:#fff4e5;color:#8a5300;border-radius:999px;padding:2px 10px;font-size:12px;font-weight:600}
                 form{display:flex;gap:12px} button{flex:1;border:0;border-radius:999px;padding:12px;font-size:15px;font-weight:600;cursor:pointer}
                 .ok{background:#16a34a;color:#fff} .ko{background:#e5e7eb;color:#111}
                 small{display:block;margin-top:16px;color:#888;font-size:12px;word-break:break-all}
                </style></head><body><main>
                <span class="tag">Modo de pruebas · sin dinero real</span>
                <h1>Pagar orden %s</h1><p>Elige el resultado que quieres simular.</p>
                <div class="amount">$ %s COP</div>
                <form method="post" action="%s">%s%s
                 <button class="ok" name="decision" value="APPROVED">Aprobar</button>
                 <button class="ko" name="decision" value="DECLINED">Rechazar</button>
                </form><small>Referencia %s</small></main></body></html>
                """.formatted(order == null ? "" : "#" + order, amount == null ? "—" : amount.toPlainString(), escape(action),
                hiddenAmount, hiddenReturn, reference);
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }

    @PostMapping("/{reference}")
    public ResponseEntity<String> decide(@PathVariable String reference,
                                         @RequestParam("decision") String decision,
                                         @RequestParam(value = "amount", required = false) BigDecimal amount,
                                         @RequestParam(value = "return", required = false) String returnUrl) {
        if (!settings.isSandboxEnabled() || !reference.matches(REFERENCE)) {
            return ResponseEntity.notFound().build();
        }
        String status = "APPROVED".equals(decision) ? "APPROVED" : "DECLINED";
        String body = "{\"reference\":\"" + reference + "\",\"status\":\"" + status + "\",\"transaction_id\":\"SBX-"
                + UUID.randomUUID() + "\"" + (amount == null ? "" : ",\"amount\":\"" + amount.toPlainString() + "\"") + "}";
        handleWebhook.handle("sandbox", Map.of("x-sandbox-signature", sandbox.sign(body)), Map.of(), body);
        String target = safeUrl(returnUrl);
        if (target == null) {
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML)
                    .body("<!doctype html><meta charset='utf-8'><p style='font-family:sans-serif'>Pago "
                            + ("APPROVED".equals(status) ? "aprobado" : "rechazado") + ". Ya puedes cerrar esta ventana.</p>");
        }
        String location = target + (target.contains("?") ? "&" : "?") + "payment_reference="
                + URLEncoder.encode(reference, StandardCharsets.UTF_8) + "&status=" + status;
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(location));
        return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
    }

    private static String safeUrl(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://")) && !url.contains("<")
                && !url.contains("\"") && !url.contains("'") ? url : null;
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&#39;").replace("\"", "&quot;");
    }
}
