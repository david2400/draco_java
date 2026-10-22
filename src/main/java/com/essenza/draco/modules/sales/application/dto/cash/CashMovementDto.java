package com.essenza.draco.modules.sales.application.dto.cash;

import java.math.BigDecimal;
import java.time.Instant;

public record CashMovementDto(Long id, Long cashSessionId, String type, BigDecimal amount, String reason, String reference,
                              String createdBy, Instant createdAt) {
}
