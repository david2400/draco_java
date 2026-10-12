package com.essenza.draco.modules.sales.infrastructure.outbound.gateway;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** Firmas de las pasarelas: SHA-256 (Wompi) y HMAC-SHA256 (Mercado Pago, sandbox). */
final class Signatures {

    private Signatures() {
    }

    static String sha256Hex(String value) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

    static String hmacSha256Hex(String secret, String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /** Comparación en tiempo constante, sin distinguir mayúsculas (hex). */
    static boolean sameHex(String expected, String received) {
        if (expected == null || received == null) {
            return false;
        }
        return MessageDigest.isEqual(expected.toLowerCase().getBytes(StandardCharsets.UTF_8),
                received.trim().toLowerCase().getBytes(StandardCharsets.UTF_8));
    }
}
