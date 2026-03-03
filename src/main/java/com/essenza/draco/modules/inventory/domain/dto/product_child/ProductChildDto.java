package com.essenza.draco.modules.inventory.domain.dto.product_child;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductChildDto extends AuditInfoDto {
    private Long id;
    private Long productId;
    private String name;
    private String description;
    private Integer stock;
    private BigDecimal unitPrice;
    private String imageUrl;
    private Boolean available;
}
