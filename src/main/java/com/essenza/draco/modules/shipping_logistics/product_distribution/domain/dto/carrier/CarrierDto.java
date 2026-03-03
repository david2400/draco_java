package com.essenza.draco.modules.shipping_logistics.product_distribution.domain.dto.carrier;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierDto extends AuditInfoDto {
    private Long id;
    private String name;
    private String code;
    private String contactEmail;
    private String contactPhone;
    private String website;
    private BigDecimal baseRate;
    private BigDecimal ratePerKm;
    private Integer maxDeliveryDays;
    private Boolean isActive;
}
