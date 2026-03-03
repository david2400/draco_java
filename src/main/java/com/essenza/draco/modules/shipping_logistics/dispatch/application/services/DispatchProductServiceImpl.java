package com.essenza.draco.modules.shipping_logistics.dispatch.application.services;

//import com.essenza.draco.modules.dispatch.application.input.dispatch_product.*;
import com.essenza.draco.modules.shipping_logistics.dispatch.application.input.dispatch_product.*;
import com.essenza.draco.modules.shipping_logistics.dispatch.application.output.shipping.DeliveryEstimateProvider;
import com.essenza.draco.modules.shipping_logistics.dispatch.application.output.shipping.ShippingRateProvider;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product.CreateDispatchProductDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product.DispatchProductDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product.ShippingEstimateDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product.UpdateDispatchProductDto;
import com.essenza.draco.modules.shipping_logistics.dispatch.infrastructure.outbound.repositories.dispatch_product.DispatchProductRepositoryAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class DispatchProductServiceImpl implements CreateDispatchProductUseCase,
        UpdateDispatchProductUseCase,
        DeleteDispatchProductUseCase,
        FindDispatchProductByIdUseCase,
        FindDispatchProductsUseCase,
        GetShippingEstimateUseCase {

    private final DispatchProductRepositoryAdapter repository;
    private final ShippingRateProvider shippingRateProvider;
    private final DeliveryEstimateProvider deliveryEstimateProvider;

    public DispatchProductServiceImpl(
            DispatchProductRepositoryAdapter repository,
            ShippingRateProvider shippingRateProvider,
            DeliveryEstimateProvider deliveryEstimateProvider) {
        this.repository = repository;
        this.shippingRateProvider = shippingRateProvider;
        this.deliveryEstimateProvider = deliveryEstimateProvider;
    }

    @Override
    public DispatchProductDto create(CreateDispatchProductDto input) {
        return repository.create(input);
    }

    @Override
    public DispatchProductDto update(Long id, UpdateDispatchProductDto input) {
        return repository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DispatchProductDto> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DispatchProductDto> findAll() {
        return repository.findAll();
    }
    
    @Override
    public CompletableFuture<ShippingEstimateDto> getShippingEstimate(
            String carrierCode,
            String originZip,
            String destinationZip,
            BigDecimal weight,
            String serviceType) {
        
        // Verificar disponibilidad del servicio
        if (!shippingRateProvider.isServiceAvailable(carrierCode)) {
            return CompletableFuture.completedFuture(ShippingEstimateDto.builder()
                    .carrierCode(carrierCode)
                    .originZip(originZip)
                    .destinationZip(destinationZip)
                    .weight(weight)
                    .selectedServiceType(serviceType)
                    .build());
        }
        
        // Consultar costos y fechas en paralelo
        CompletableFuture<BigDecimal> costFuture = shippingRateProvider.getShippingRate(
                carrierCode, originZip, destinationZip, weight);
                
        CompletableFuture<Map<String, LocalDate>> datesFuture = deliveryEstimateProvider.getDeliveryEstimates(
                carrierCode, originZip, destinationZip);
                
        // Combinar resultados cuando ambos estén disponibles
        return CompletableFuture.allOf(costFuture, datesFuture)
                .thenApply(v -> {
                    BigDecimal cost = costFuture.join();
                    Map<String, LocalDate> dates = datesFuture.join();
                    
                    // Si no existe el tipo de servicio solicitado, usar el primero disponible
                    LocalDate estimatedDate = dates.getOrDefault(serviceType, 
                            !dates.isEmpty() ? dates.values().iterator().next() : null);
                    
                    // Construir respuesta
                    return ShippingEstimateDto.builder()
                            .carrierCode(carrierCode)
                            .originZip(originZip)
                            .destinationZip(destinationZip)
                            .weight(weight)
                            .selectedServiceType(serviceType)
                            .shippingCost(cost)
                            .estimatedDeliveryDate(estimatedDate)
                            .availableServices(dates)
                            .build();
                })
                .exceptionally(ex -> {
                    // Manejar errores
                    return ShippingEstimateDto.builder()
                            .carrierCode(carrierCode)
                            .originZip(originZip)
                            .destinationZip(destinationZip)
                            .weight(weight)
                            .selectedServiceType(serviceType)
                            .build();
                });
    }
}
