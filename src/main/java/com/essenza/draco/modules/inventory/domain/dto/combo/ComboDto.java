package com.essenza.draco.modules.inventory.domain.dto.combo;

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
public class ComboDto extends AuditInfoDto {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Boolean available;
    private BigDecimal realPrice;
    private BigDecimal price;
}
