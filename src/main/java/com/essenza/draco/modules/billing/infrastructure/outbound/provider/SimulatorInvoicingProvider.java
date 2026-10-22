package com.essenza.draco.modules.billing.infrastructure.outbound.provider;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.essenza.draco.modules.billing.application.output.provider.ElectronicInvoicingProvider;
import com.essenza.draco.modules.billing.domain.model.BillingSettings;
import com.essenza.draco.modules.billing.domain.model.Cufe;
import com.essenza.draco.modules.billing.domain.model.DocumentKind;
import com.essenza.draco.modules.billing.domain.model.Invoice;
import com.essenza.draco.modules.billing.domain.model.NumberingResolution;
import com.essenza.draco.modules.billing.domain.model.TaxKind;

/**
 * Proveedor simulado: calcula el CUFE/CUDE con la fórmula de la DIAN y arma el contenido
 * del QR, sin enviar nada. {@code essenza.billing.simulator.mode} = ACCEPT (por defecto),
 * REJECT (rechaza) u OFFLINE (falla la comunicación) para probar reintentos.
 */
@Component
public class SimulatorInvoicingProvider implements ElectronicInvoicingProvider {

    static final ZoneId BOGOTA = ZoneId.of("America/Bogota");
    static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ssXXX");

    private final boolean enabled;
    private final String mode;

    public SimulatorInvoicingProvider(@Value("${essenza.billing.simulator.enabled:true}") boolean enabled,
                                      @Value("${essenza.billing.simulator.mode:ACCEPT}") String mode) {
        this.enabled = enabled;
        this.mode = mode == null ? "ACCEPT" : mode.trim().toUpperCase(Locale.ROOT);
    }

    @Override
    public String code() {
        return "SIMULATOR";
    }

    @Override
    public boolean configured() {
        return enabled;
    }

    @Override
    public Result send(Invoice document, NumberingResolution resolution, BillingSettings issuer, Invoice related) {
        if ("OFFLINE".equals(mode)) {
            throw new ProviderException("el simulador está configurado sin conexión (OFFLINE)", null);
        }
        if ("REJECT".equals(mode)) {
            return new Result(false, null, null, null, "Rechazado por el simulador (modo REJECT).");
        }
        ZonedDateTime issued = document.issuedAt().atZone(BOGOTA);
        String date = DATE.format(issued);
        String time = TIME.format(issued);
        // Factura: clave técnica de la resolución. Nota crédito: PIN del software.
        String key = document.kind() == DocumentKind.INVOICE ? resolution.technicalKey() : issuer.softwarePin();
        String cufe = Cufe.compute(document.fullNumber(), date, time, document.netAmount(), document.taxOf(TaxKind.IVA),
                document.taxOf(TaxKind.INC), document.total(), issuer.nit(), document.buyer().docNumber(), key,
                document.environment());
        String qr = String.join("\n",
                "NumFac: " + document.fullNumber(),
                "FecFac: " + date,
                "HorFac: " + time,
                "NitFac: " + issuer.nit(),
                "DocAdq: " + document.buyer().docNumber(),
                "ValFac: " + document.netAmount().toPlainString(),
                "ValIva: " + document.taxOf(TaxKind.IVA).toPlainString(),
                "ValOtroIm: " + document.taxOf(TaxKind.INC).toPlainString(),
                "ValTolFac: " + document.total().toPlainString(),
                (document.kind() == DocumentKind.INVOICE ? "CUFE: " : "CUDE: ") + cufe,
                "QRCode: " + Cufe.verificationUrl(cufe, document.environment()));
        String what = document.kind() == DocumentKind.INVOICE ? "Factura" : "Nota crédito";
        return new Result(true, cufe, qr, "SIM-" + document.fullNumber(),
                what + " validada por el simulador (" + document.environment() + "): no se envió a la DIAN.");
    }
}
