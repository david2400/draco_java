package com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.dispatch_detail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDispatchDetailDto extends CreateDispatchDetailDto {
    @NotNull
    @Positive
    private Long id;
}
