package com.essenza.draco.modules.inventory.domain.command;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductVariantCommand {
    Long id;
    String name;
    String description;
    Integer stock;
    BigDecimal unitPrice;
    String imageUrl;
    Boolean available;
}
