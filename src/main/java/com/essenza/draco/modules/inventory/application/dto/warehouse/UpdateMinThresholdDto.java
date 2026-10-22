package com.essenza.draco.modules.inventory.application.dto.warehouse;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateMinThresholdDto(@NotNull @Min(0) @Max(1000000) Integer minThreshold) {
}
