package com.essenza.draco.modules.sales.infrastructure.outbound.gateway;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.essenza.draco.modules.sales.application.output.gateway.PaymentGateway;
import com.essenza.draco.modules.sales.application.output.gateway.PaymentGateways;
import com.essenza.draco.modules.sales.domain.model.payment.GatewayProvider;

@Component
public class PaymentGatewaysAdapter implements PaymentGateways {

    private final List<PaymentGateway> gateways;
    private final PaymentsSettings settings;

    public PaymentGatewaysAdapter(List<PaymentGateway> gateways, PaymentsSettings settings) {
        this.gateways = gateways.stream().sorted(Comparator.comparing(PaymentGateway::provider)).toList();
        this.settings = settings;
    }

    @Override
    public Optional<PaymentGateway> find(GatewayProvider provider) {
        return gateways.stream().filter(gateway -> gateway.provider() == provider).findFirst();
    }

    @Override
    public List<PaymentGateway> all() {
        return gateways;
    }

    @Override
    public String webhookUrl(GatewayProvider provider) {
        return webhookUrl(settings, provider);
    }

    @Override
    public String defaultReturnUrl() {
        return settings.getDefaultReturnUrl();
    }

    static String webhookUrl(PaymentsSettings settings, GatewayProvider provider) {
        return settings.getPublicBaseUrl() + "/api/shop/sales/payments/webhooks/" + provider.name().toLowerCase(Locale.ROOT);
    }
}
