package com.essenza.draco.modules.sales.application.services.payment_method;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.sales.application.dto.payment_method.PaymentMethodDto;
import com.essenza.draco.modules.sales.application.dto.payment_method.SavePaymentMethodDto;
import com.essenza.draco.modules.sales.application.output.gateway.PaymentGateways;
import com.essenza.draco.modules.sales.application.output.repository.PaymentMethodRepository;
import com.essenza.draco.modules.sales.domain.model.payment.GatewayProvider;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentChannel;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentMethod;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentMethodKind;
import com.essenza.draco.shared.exceptions.ConflictException;

/** Código compartido por los casos de uso de medios de pago. */
@Component
@RequiredArgsConstructor
class PaymentMethodSoporte {

    private final PaymentMethodRepository methods;
    private final PaymentGateways gateways;

    PaymentMethod toDomain(Long id, SavePaymentMethodDto input) {
        PaymentMethodKind kind = PaymentMethodKind.parse(input.kind());
        GatewayProvider provider = kind == PaymentMethodKind.GATEWAY && input.provider() != null && !input.provider().isBlank()
                ? GatewayProvider.parse(input.provider()) : null;
        String code = input.code() == null || input.code().isBlank() ? freeCode(deriveCode(input.name()), id) : input.code();
        return new PaymentMethod(id, code, input.name(), input.description(), input.instructions(),
                PaymentChannel.parse(input.channel()), kind, provider,
                input.active() == null || input.active(), input.position() == null ? 0 : input.position(),
                input.feePercent(), input.feeFixed(), input.minAmount(), input.maxAmount(),
                Boolean.TRUE.equals(input.requiresReference()), Boolean.TRUE.equals(input.allowsChange()));
    }

    void checkUniqueCode(PaymentMethod method) {
        if (methods.existsActiveCode(method.code(), method.id())) {
            throw new ConflictException("Ya existe un medio de pago con el código \"" + method.code() + "\".");
        }
    }

    PaymentMethodDto toDto(PaymentMethod method, Map<Long, Long> counts) {
        boolean configured = method.provider() == null
                || gateways.find(method.provider()).map(gateway -> gateway.configured()).orElse(false);
        return new PaymentMethodDto(method.id(), method.code(), method.name(), method.description(), method.instructions(),
                method.channel().name(), method.kind().name(), method.provider() == null ? null : method.provider().name(),
                method.active(), method.position(), plain(method.feePercent()), plain(method.feeFixed()),
                plain(method.minAmount()), plain(method.maxAmount()), method.requiresReference(), method.allowsChange(),
                configured, method.id() == null ? 0 : counts.getOrDefault(method.id(), 0L));
    }

    /** Código derivado libre: BONO, BONO_2, BONO_3… */
    private String freeCode(String base, Long id) {
        String candidate = base;
        for (int suffix = 2; methods.existsActiveCode(candidate, id); suffix++) {
            String tail = "_" + suffix;
            candidate = (base.length() + tail.length() > 40 ? base.substring(0, 40 - tail.length()) : base) + tail;
        }
        return candidate;
    }

    /** "Nequi / Daviplata" → NEQUI_DAVIPLATA. */
    static String deriveCode(String name) {
        String base = name == null ? "" : Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        base = base.toUpperCase().replaceAll("[^A-Z0-9]+", "_").replaceAll("^_+|_+$", "");
        if (base.isEmpty() || !Character.isLetter(base.charAt(0))) {
            base = "M_" + base;
        }
        if (base.length() < 2) {
            base = base + "_M";
        }
        return base.length() > 40 ? base.substring(0, 40) : base;
    }

    private static BigDecimal plain(BigDecimal value) {
        if (value == null) {
            return null;
        }
        BigDecimal stripped = value.stripTrailingZeros();
        return stripped.scale() < 0 ? stripped.setScale(0) : stripped;
    }
}
