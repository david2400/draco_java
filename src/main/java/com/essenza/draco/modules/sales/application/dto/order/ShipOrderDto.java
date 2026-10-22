package com.essenza.draco.modules.sales.application.dto.order;

import jakarta.validation.constraints.Size;

/** Despacho: transportadora y guía (si hay transportadora la guía es obligatoria). */
public record ShipOrderDto(Long carrierId, @Size(max = 80) String trackingNumber, @Size(max = 500) String note,
                           @Size(max = 120) String actor) {
}
