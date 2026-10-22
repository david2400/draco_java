package com.essenza.draco.modules.billing.domain.model;

/**
 * PENDING: numerada, aún no aceptada por el proveedor (o falló el envío, se reintenta);
 * ISSUED: validada (CUFE); REJECTED: el proveedor la rechazó (se corrige y reintenta);
 * VOIDED: anulada con una nota crédito.
 */
public enum InvoiceStatus {
    PENDING,
    ISSUED,
    REJECTED,
    VOIDED
}
