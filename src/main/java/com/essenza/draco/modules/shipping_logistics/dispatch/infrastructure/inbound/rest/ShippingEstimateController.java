package com.essenza.draco.modules.shipping_logistics.dispatch.infrastructure.inbound.rest;

import com.essenza.draco.modules.shipping_logistics.dispatch.application.input.dispatch_product.GetShippingEstimateUseCase;
import com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product.ShippingEstimateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/shipping_logistics/shipping")
@Tag(name = "Shipping Estimates", description = "API para obtener estimaciones de envío")
public class ShippingEstimateController {

    private final GetShippingEstimateUseCase shippingEstimateUseCase;

    public ShippingEstimateController(GetShippingEstimateUseCase shippingEstimateUseCase) {
        this.shippingEstimateUseCase = shippingEstimateUseCase;
    }

    @GetMapping("/estimate")
    @Operation(summary = "Obtiene una estimación de envío", 
               description = "Devuelve información de costos y fechas estimadas de entrega")
    public CompletableFuture<ResponseEntity<ShippingEstimateDto>> getShippingEstimate(
            @RequestParam String carrierCode,
            @RequestParam String originZip,
            @RequestParam String destinationZip,
            @RequestParam(required = false, defaultValue = "1.0") BigDecimal weight,
            @RequestParam(required = false, defaultValue = "standard") String serviceType) {
        
        return shippingEstimateUseCase.getShippingEstimate(carrierCode, originZip, destinationZip, weight, serviceType)
                .thenApply(estimate -> {
                    if (estimate != null && estimate.getShippingCost() != null) {
                        return ResponseEntity.ok(estimate);
                    } else {
                        return ResponseEntity.notFound().build();
                    }
                });
    }
}
