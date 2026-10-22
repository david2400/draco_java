package com.essenza.draco.modules.sales.application.dto.order;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

/** Cabecera de la orden: cliente, entrega, origen, descuento y notas. */
public record SaveOrderDetailsDto(
        @Valid Customer customer,
        @Valid Delivery delivery,
        @Valid Discount discount,
        @Schema(description = "BACKOFFICE, POS, ONLINE, PHONE, WHATSAPP o MARKETPLACE") String channel,
        @Schema(description = "Tienda de origen") Long storeId,
        @Schema(description = "Bodega de despacho; vacío = la de la tienda o el reparto normal") Long warehouseId,
        @Size(max = 120) @Schema(description = "Vendedor o responsable") String assignedTo,
        @Schema(description = "LOW, NORMAL, HIGH o URGENT") String priority,
        @Size(max = 255) @Schema(description = "Referencia externa (pedido del cliente, marketplace…)") String complementaryOrder,
        @Size(max = 2000) String internalNotes,
        @Size(max = 2000) String customerNotes,
        @Size(max = 120) @Schema(description = "Quién hace el cambio (para la línea de tiempo)") String actor) {

    @Schema(name = "OrderCustomerInput")
    public record Customer(
            @Schema(description = "Usuario de la tienda; vacío = invitado") Long customerId,
            @Schema(description = "PERSON o COMPANY") String kind,
            @Schema(description = "CC, CE, NIT, PP, TI o PEP") String docType,
            @Size(max = 30) String docNumber,
            @Size(max = 200) String name,
            @Size(max = 160) String email,
            @Size(max = 40) String phone,
            @Size(max = 200) String billingAddress,
            @Size(max = 120) String billingCity) {
    }

    @Schema(name = "OrderDeliveryInput")
    public record Delivery(
            @Schema(description = "SHIPPING o PICKUP") String method,
            @Schema(description = "Tienda de retiro (PICKUP)") Long pickupStoreId,
            @Size(max = 160) String recipientName,
            @Size(max = 40) String recipientPhone,
            @Size(max = 200) String address,
            @Size(max = 200) String addressDetail,
            Long countryId,
            Long stateId,
            Long cityId,
            @Size(max = 80) String country,
            @Size(max = 80) String state,
            @Size(max = 120) String city,
            @Size(max = 20) String postalCode,
            @Size(max = 500) String notes,
            Long carrierId,
            @Schema(description = "Costo de envío (se suma al total)") BigDecimal shippingAmount,
            LocalDate promisedDate) {
    }

    @Schema(name = "OrderDiscountInput")
    public record Discount(
            @Schema(description = "NONE, PERCENT, AMOUNT o COUPON") String type,
            @Schema(description = "Porcentaje (PERCENT) o valor (AMOUNT)") BigDecimal value,
            @Size(max = 50) @Schema(description = "Código del cupón (COUPON)") String couponCode) {
    }
}
