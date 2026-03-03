package com.essenza.draco.modules.shipping_logistics.dispatch.domain.dto.tracking;

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
public class UpdateTrackingDto extends CreateTrackingDto {
    @NotNull
    @Positive
    private Long id;
}
