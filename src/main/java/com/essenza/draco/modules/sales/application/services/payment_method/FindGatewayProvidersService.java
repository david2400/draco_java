package com.essenza.draco.modules.sales.application.services.payment_method;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.sales.application.dto.payment_method.GatewayProviderDto;
import com.essenza.draco.modules.sales.application.input.payment_method.FindGatewayProvidersUseCase;
import com.essenza.draco.modules.sales.application.output.gateway.PaymentGateways;

@Service
@RequiredArgsConstructor
public class FindGatewayProvidersService implements FindGatewayProvidersUseCase {

    private final PaymentGateways gateways;

    @Override
    public List<GatewayProviderDto> providers() {
        return gateways.all().stream()
                .map(gateway -> new GatewayProviderDto(gateway.provider().name(), gateway.configured(),
                        gateway.environment(), gateways.webhookUrl(gateway.provider())))
                .toList();
    }
}
