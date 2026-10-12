package com.essenza.draco.modules.sales.application.input.payment_method;

import java.util.List;

import com.essenza.draco.modules.sales.application.dto.payment_method.GatewayProviderDto;

/** Pasarelas disponibles y si tienen credenciales configuradas. */
public interface FindGatewayProvidersUseCase {

    List<GatewayProviderDto> providers();
}
