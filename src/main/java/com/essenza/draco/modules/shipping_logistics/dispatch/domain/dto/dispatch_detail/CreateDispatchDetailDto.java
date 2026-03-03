package com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_detail;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDispatchDetailDto {
    @NotNull
    @Positive
    private Long dispatchProductId;
    @NotNull
    @Positive
    private Long productOrderId;
}
