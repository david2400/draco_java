package com.essenza.draco.modules.inventory.domain.model.supplier;

import java.util.Locale;

/** Cuenta para pagarle al proveedor. Todo o nada: banco, tipo y número son obligatorios juntos. */
public record BankAccount(String bankName, String accountType, String accountNumber, String holderName,
                          String holderDocument) {

    /** null si no llega ningún dato bancario. */
    public static BankAccount of(String bankName, String accountType, String accountNumber, String holderName,
                                 String holderDocument) {
        if (Supplier.blank(bankName) == null && Supplier.blank(accountType) == null && Supplier.blank(accountNumber) == null
                && Supplier.blank(holderName) == null && Supplier.blank(holderDocument) == null) {
            return null;
        }
        return new BankAccount(bankName, accountType, accountNumber, holderName, holderDocument);
    }

    public BankAccount {
        bankName = Supplier.blank(bankName);
        accountType = Supplier.blank(accountType);
        accountNumber = accountNumber == null ? null : accountNumber.replaceAll("[\\s-]", "");
        holderName = Supplier.blank(holderName);
        holderDocument = Supplier.blank(holderDocument);
        if (bankName == null || accountType == null || accountNumber == null || accountNumber.isEmpty()) {
            throw new SupplierRuleViolation("La cuenta bancaria necesita banco, tipo y número.");
        }
        accountType = accountType.toUpperCase(Locale.ROOT);
        if (!accountType.equals("AHORROS") && !accountType.equals("CORRIENTE")) {
            throw new SupplierRuleViolation("Tipo de cuenta no válido: " + accountType + " (AHORROS o CORRIENTE).");
        }
        if (!accountNumber.matches("[0-9]{6,20}")) {
            throw new SupplierRuleViolation("El número de cuenta tiene entre 6 y 20 dígitos.");
        }
    }

    /** Últimos 4 dígitos para listados: ****1234. */
    public String masked() {
        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }
}
