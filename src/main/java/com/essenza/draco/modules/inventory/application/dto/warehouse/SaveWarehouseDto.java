package com.essenza.draco.modules.inventory.application.dto.warehouse;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Alta o edición de la bodega. */
public record SaveWarehouseDto(
        @Size(max = 50) @Schema(description = "Código; vacío = BOD_<n>") String code,
        @NotBlank @Size(max = 150) String name,
        @Schema(description = "ALMACEN, TIENDA, TRANSITO, DEVOLUCIONES o CONSIGNACION (por defecto ALMACEN)") String warehouseType,
        @Schema(description = "Por defecto activa al crear; se conserva al editar") Boolean active,
        @Size(max = 255) String address,
        @Schema(description = "País (catálogo del servicio parametros)") Long countryId,
        @Schema(description = "Departamento (catálogo del servicio parametros)") Long stateId,
        @Schema(description = "Ciudad (catálogo del servicio parametros)") Long cityId,
        @Size(max = 80) @Schema(description = "Nombre de la ciudad") String city,
        @Schema(description = "Vende (POS y tiendas)") Boolean allowSales,
        @Schema(description = "Despacha pedidos de la tienda en línea") Boolean allowOnline,
        @Schema(description = "Recibe compras a proveedores") Boolean allowReceiving,
        @Schema(description = "Orden para despachar: 1 = primero (por defecto 10)") Integer fulfillmentPriority,
        @Size(max = 120) String managerName,
        @Size(max = 40) String managerPhone,
        @Email @Size(max = 120) String managerEmail,
        @Size(max = 255) String openingHours,
        @Size(max = 1000) String notes,
        @Schema(description = "Área en m²") BigDecimal areaM2,
        @Schema(description = "Capacidad en unidades (para la ocupación)") Integer capacityUnits,
        @Schema(description = "Capacidad en posiciones de almacenamiento") Integer capacityPositions) {
}
