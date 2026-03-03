package com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDispatchProductDto extends CreateDispatchProductDto {
    @NotNull
    @Positive
    private Long id;
}
