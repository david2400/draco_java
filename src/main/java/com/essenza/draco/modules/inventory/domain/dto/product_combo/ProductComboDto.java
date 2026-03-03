package com.essenza.draco.modules.inventory.domain.dto.product_combo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.essenza.draco.shared.common.domain.dto.AuditInfoDto;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductComboDto extends AuditInfoDto {
    private Long id;
    private Integer quantity;
    private Long productId;
    private Long comboId;
}
