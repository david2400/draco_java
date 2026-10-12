package com.essenza.draco.modules.sales.infrastructure.outbound.repositories.payment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.sales.application.output.repository.PaymentMethodRepository;
import com.essenza.draco.modules.sales.domain.model.payment.GatewayProvider;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentChannel;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentMethod;
import com.essenza.draco.modules.sales.domain.model.payment.PaymentMethodKind;
import com.essenza.draco.modules.sales.infrastructure.outbound.persistence.mysql.shop.PaymentMethodEntity;

@Repository
@RequiredArgsConstructor
public class PaymentMethodRepositoryAdapter implements PaymentMethodRepository {

    private final JpaPaymentMethodRepository jpa;
    private final JpaPaymentRepository payments;

    @Override
    public List<PaymentMethod> findAll() {
        return jpa.findAll().stream().map(PaymentMethodRepositoryAdapter::toDomain).toList();
    }

    @Override
    public Optional<PaymentMethod> findById(Long id) {
        return jpa.findById(id).map(PaymentMethodRepositoryAdapter::toDomain);
    }

    @Override
    public PaymentMethod save(PaymentMethod method) {
        PaymentMethodEntity entity = method.id() == null ? new PaymentMethodEntity()
                : jpa.findById(method.id()).orElseGet(PaymentMethodEntity::new);
        entity.setId(method.id());
        entity.setCode(method.code());
        entity.setName(method.name());
        entity.setDescription(method.description());
        entity.setInstructions(method.instructions());
        entity.setChannel(method.channel().name());
        entity.setKind(method.kind().name());
        entity.setProvider(method.provider() == null ? null : method.provider().name());
        entity.setActive(method.active());
        entity.setPosition(method.position());
        entity.setFeePercent(method.feePercent());
        entity.setFeeFixed(method.feeFixed());
        entity.setMinAmount(method.minAmount());
        entity.setMaxAmount(method.maxAmount());
        entity.setRequiresReference(method.requiresReference());
        entity.setAllowsChange(method.allowsChange());
        return toDomain(jpa.save(entity));
    }

    @Override
    public boolean existsActiveCode(String code, Long excludeId) {
        return jpa.existsCode(code, excludeId);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public Map<Long, Long> paymentCounts() {
        Map<Long, Long> counts = new HashMap<>();
        for (Object[] row : payments.countByMethod()) {
            counts.put(((Number) row[0]).longValue(), ((Number) row[1]).longValue());
        }
        return counts;
    }

    static PaymentMethod toDomain(PaymentMethodEntity e) {
        PaymentMethodKind kind = parseKind(e.getKind());
        PaymentChannel channel = "VIRTUAL".equalsIgnoreCase(e.getChannel()) ? PaymentChannel.VIRTUAL : PaymentChannel.PHYSICAL;
        if (!kind.allows(channel)) {
            kind = PaymentMethodKind.OTHER;
        }
        GatewayProvider provider = GatewayProvider.tryParse(e.getProvider()).orElse(null);
        if (kind == PaymentMethodKind.GATEWAY && provider == null) {
            kind = PaymentMethodKind.OTHER;
        }
        boolean allowsChange = e.isAllowsChange()
                && (kind == PaymentMethodKind.CASH || kind == PaymentMethodKind.CASH_ON_DELIVERY);
        String code = e.getCode() == null || e.getCode().isBlank() ? "M_" + e.getId() : e.getCode();
        return new PaymentMethod(e.getId(), code, e.getName(), e.getDescription(), e.getInstructions(), channel, kind,
                provider, e.isActive(), e.getPosition(), e.getFeePercent(), e.getFeeFixed(), e.getMinAmount(),
                e.getMaxAmount(), e.isRequiresReference(), allowsChange);
    }

    private static PaymentMethodKind parseKind(String value) {
        try {
            return value == null ? PaymentMethodKind.OTHER : PaymentMethodKind.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return PaymentMethodKind.OTHER;
        }
    }
}
