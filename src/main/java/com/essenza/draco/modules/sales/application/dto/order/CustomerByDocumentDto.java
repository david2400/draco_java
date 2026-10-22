package com.essenza.draco.modules.sales.application.dto.order;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Cliente por documento para la orden: identidad del registro central (access_control) y,
 * si la persona ya compró, el contacto y la dirección de su última orden.
 *
 * @param centralStatus FOUND, NOT_FOUND, UNAVAILABLE o DISABLED
 * @param source        CENTRAL (registro central), ORDERS (solo historial) o NONE
 */
public record CustomerByDocumentDto(
        String source,
        String centralStatus,
        @Schema(description = "El tipo de documento elegido coincide con el registrado") boolean typeMatches,
        @Schema(description = "Persona en access_control (id_client)") Long customerId,
        String docType,
        String docNumber,
        String name,
        String kind,
        String personStatus,
        Long userId,
        String username,
        String userStatus,
        String email,
        String phone,
        String billingAddress,
        String billingCity,
        String recipientName,
        String recipientPhone,
        String shipAddress,
        String shipAddressDetail,
        Long shipCountryId,
        Long shipStateId,
        Long shipCityId,
        String shipCity,
        String shipPostalCode,
        long ordersCount,
        Instant lastOrderAt) {
}
