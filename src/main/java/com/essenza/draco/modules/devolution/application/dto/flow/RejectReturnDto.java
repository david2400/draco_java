package com.essenza.draco.modules.devolution.application.dto.flow;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectReturnDto(@Size(max = 100) String by, @NotBlank @Size(max = 255) String reason) {
}
