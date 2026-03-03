package com.essenza.draco.modules.inventory.domain.dto.product;

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
public class ProductDto extends AuditInfoDto {
    private Long id;
    private String name;
    private String description;
    private Integer stock;
    private BigDecimal realPrice;
    private BigDecimal unitPrice;
    private Double length;
    private Double width;
    private Double height;
    private Double weight;
    private String imageUrl;
    private Boolean available;
    private Long brandId;
    private Long categoryId;
    private Long subcategoryId;
    private Long supplierId;
    private Boolean isCombo = false;
}
