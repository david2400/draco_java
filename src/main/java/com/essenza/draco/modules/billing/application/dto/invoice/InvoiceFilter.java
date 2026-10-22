package com.essenza.draco.modules.billing.application.dto.invoice;

import java.time.Instant;

/** Filtros del listado de facturas. */
public record InvoiceFilter(String q, String kind, String status, Long orderId, Instant from, Instant to) {
}
