package com.essenza.draco.modules.billing.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * CUFE (factura) y CUDE (nota crédito) según el anexo técnico de la DIAN:
 * SHA-384 de NumFac + FecFac + HorFac + ValFac + 01 + ValIVA + 04 + ValINC + 03 + ValICA
 * + ValTot + NitOFE + NumAdq + ClaveTécnica (o PIN del software en la nota) + TipoAmbiente.
 */
public final class Cufe {

    private Cufe() {
    }

    public static String compute(String number, String date, String time, BigDecimal net, BigDecimal iva, BigDecimal inc,
                                 BigDecimal total, String issuerNit, String buyerNumber, String keyOrPin,
                                 BillingEnvironment environment) {
        String chain = number + date + time + money(net) + "01" + money(iva) + "04" + money(inc) + "03" + money(BigDecimal.ZERO)
                + money(total) + Nit.digits(issuerNit) + buyerNumber + (keyOrPin == null ? "" : keyOrPin)
                + environment.dianCode();
        return sha384(chain);
    }

    static String money(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    static String sha384(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-384").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /** URL de consulta del documento en el catálogo de la DIAN (va en el código QR). */
    public static String verificationUrl(String cufe, BillingEnvironment environment) {
        String host = environment == BillingEnvironment.PRODUCTION ? "catalogo-vpfe.dian.gov.co" : "catalogo-vpfe-hab.dian.gov.co";
        return "https://" + host + "/document/searchqr?documentkey=" + cufe;
    }
}
