package com.essenza.draco.modules.inventory.domain.command;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductBundleItemCommand {
    Long productId;
    Integer quantity;
    BigDecimal contributionPercentage;
}
