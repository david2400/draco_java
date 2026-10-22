package com.essenza.draco.modules.devolution.application.dto.flow;

import jakarta.validation.constraints.Size;

public record ApproveReturnDto(@Size(max = 100) String by, Long returnMethodId) {
}
