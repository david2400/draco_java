package com.essenza.draco.modules.sales.application.dto.order;

import java.time.Instant;

/** Cliente de órdenes anteriores, con su última dirección de envío, para reutilizarlo. */
public record CustomerLookupDto(Long customerId, String kind, String docType, String docNumber, String name, String email,
                                String phone, String billingAddress, String billingCity, String recipientName,
                                String recipientPhone, String shipAddress, String shipAddressDetail, Long shipCountryId,
                                Long shipStateId, Long shipCityId, String shipCountry, String shipState, String shipCity,
                                String shipPostalCode, long ordersCount, Instant lastOrderAt) {
}
