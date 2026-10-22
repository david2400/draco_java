package com.essenza.draco.modules.billing.application.output.provider;

import com.essenza.draco.modules.billing.domain.model.BillingSettings;
import com.essenza.draco.modules.billing.domain.model.Invoice;
import com.essenza.draco.modules.billing.domain.model.NumberingResolution;

/**
 * Proveedor tecnológico de facturación electrónica (simulador hoy; Factus, Alegra…
 * mañana). Recibe el documento numerado y devuelve si la DIAN lo validó.
 */
public interface ElectronicInvoicingProvider {

    /** SIMULATOR, FACTUS, ALEGRA… (coincide con billing_settings.provider). */
    String code();

    boolean configured();

    /**
     * @param related factura que anula (solo notas crédito)
     */
    Result send(Invoice document, NumberingResolution resolution, BillingSettings issuer, Invoice related);

    /**
     * @param accepted validada por la DIAN
     * @param cufe     CUFE/CUDE asignado
     * @param qrData   contenido del código QR
     */
    record Result(boolean accepted, String cufe, String qrData, String providerReference, String message) {
    }

    /** Fallo de comunicación (el documento queda PENDING para reintentar). */
    class ProviderException extends RuntimeException {
        public ProviderException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
