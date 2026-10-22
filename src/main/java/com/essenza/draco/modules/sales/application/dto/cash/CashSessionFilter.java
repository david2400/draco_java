package com.essenza.draco.modules.sales.application.dto.cash;

import java.time.Instant;

public record CashSessionFilter(Long cashRegisterId, String status, Instant from, Instant to) {
}
