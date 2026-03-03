package com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingEstimateDto {
    private String carrierCode;
    private String originZip;
    private String destinationZip;
    private BigDecimal weight;
    private String selectedServiceType;
    
    // Información de costos
    private BigDecimal shippingCost;
    
    // Información de fechas
    private LocalDate estimatedDeliveryDate;
    
    // Opciones adicionales (otros tipos de servicio con sus fechas)
    private Map<String, LocalDate> availableServices;
}
