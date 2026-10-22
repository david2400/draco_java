package com.essenza.draco.modules.inventory.supplier;

import com.essenza.draco.modules.inventory.domain.model.supplier.BankAccount;
import com.essenza.draco.modules.inventory.domain.model.supplier.CommercialTerms;
import com.essenza.draco.modules.inventory.domain.model.supplier.ContactArea;
import com.essenza.draco.modules.inventory.domain.model.supplier.PersonType;
import com.essenza.draco.modules.inventory.domain.model.supplier.Supplier;
import com.essenza.draco.modules.inventory.domain.model.supplier.SupplierContact;
import com.essenza.draco.modules.inventory.domain.model.supplier.SupplierProduct;
import com.essenza.draco.modules.inventory.domain.model.supplier.SupplierStatus;
import com.essenza.draco.modules.inventory.domain.model.supplier.TaxId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SupplierDomainTest {

    private static Supplier supplier(TaxId tax, PersonType type, SupplierStatus status, String reason,
                                     List<SupplierContact> contacts) {
        return new Supplier(null, "prov_1", tax, type, "Proveedor S.A.S.", null, null, false, false, null, null, null,
                null, null, null, null, null, status, reason, null, null, null, contacts);
    }

    @Test
    void nitNormalizaYCalculaDigito() {
        TaxId nit = TaxId.of("nit", "900.123.456");
        assertThat(nit.number()).isEqualTo("900123456");
        assertThat(nit.dv()).isEqualTo("8");
        assertThat(nit.formatted()).isEqualTo("900123456-8");
        assertThat(TaxId.of("CC", "1.020.304.050").dv()).isNull();
        assertThat(TaxId.of(null, " ")).isNull();
        assertThatThrownBy(() -> TaxId.of("NIT", null)).hasMessageContaining("tipo y el número");
        assertThatThrownBy(() -> TaxId.of("RUT", "123456")).hasMessageContaining("Tipo de documento");
        assertThatThrownBy(() -> TaxId.of("NIT", "12")).hasMessageContaining("entre 5 y 15");
    }

    @Test
    void tipoDePersonaSegunDocumento() {
        Supplier natural = supplier(TaxId.of("CC", "1020304050"), null, null, null, null);
        assertThat(natural.personType()).isEqualTo(PersonType.NATURAL);
        assertThat(natural.code()).isEqualTo("PROV_1");
        assertThat(supplier(TaxId.of("NIT", "900123456"), null, null, null, null).personType()).isEqualTo(PersonType.JURIDICA);
        assertThatThrownBy(() -> supplier(TaxId.of("CC", "1020304050"), PersonType.JURIDICA, null, null, null))
                .hasMessageContaining("jurídica");
        assertThatThrownBy(() -> supplier(null, null, null, null, null).requireDocument()).hasMessageContaining("documento");
    }

    @Test
    void bloqueoExigeMotivoYSeLimpiaAlActivar() {
        assertThatThrownBy(() -> supplier(null, null, SupplierStatus.BLOCKED, " ", null)).hasMessageContaining("motivo");
        Supplier blocked = supplier(null, null, SupplierStatus.BLOCKED, "Mora", null);
        assertThat(blocked.blockReason()).isEqualTo("Mora");
        assertThat(blocked.withStatus(SupplierStatus.ACTIVE, "x").blockReason()).isNull();
    }

    @Test
    void contactosUnPrincipal() {
        SupplierContact a = new SupplierContact(null, "Ana", null, null, "300", null, false);
        SupplierContact b = new SupplierContact(null, "Luis", null, ContactArea.CARTERA, null, "l@x.co", false);
        Supplier s = supplier(null, null, null, null, List.of(a, b));
        assertThat(s.primaryContact().name()).isEqualTo("Ana");
        assertThat(s.contacts().get(0).area()).isEqualTo(ContactArea.VENTAS);
        assertThatThrownBy(() -> supplier(null, null, null, null, List.of(a.asPrimary(true), b.asPrimary(true))))
                .hasMessageContaining("principal");
        assertThatThrownBy(() -> new SupplierContact(null, "Sin medio", null, null, null, null, false))
                .hasMessageContaining("teléfono o correo");
    }

    @Test
    void condicionesYBanco() {
        assertThatThrownBy(() -> new CommercialTerms(0, BigDecimal.TEN, null, null, null, null)).hasMessageContaining("contado");
        assertThatThrownBy(() -> new CommercialTerms(400, null, null, null, null, null)).hasMessageContaining("365");
        assertThatThrownBy(() -> new CommercialTerms(30, null, "pesos", null, null, null)).hasMessageContaining("ISO");
        assertThatThrownBy(() -> new CommercialTerms(30, null, null, null, null, BigDecimal.valueOf(120))).hasMessageContaining("100");
        assertThat(new CommercialTerms(30, BigDecimal.TEN, "usd", 5, null, null).currency()).isEqualTo("USD");
        assertThat(BankAccount.of(null, " ", null, null, null)).isNull();
        BankAccount bank = BankAccount.of("Bancolombia", "ahorros", "123-456789-01", null, null);
        assertThat(bank.accountType()).isEqualTo("AHORROS");
        assertThat(bank.masked()).isEqualTo("****8901");
        assertThatThrownBy(() -> BankAccount.of("Banco", null, null, null, null)).hasMessageContaining("banco, tipo y número");
    }

    @Test
    void productoDelProveedorYMargen() {
        SupplierProduct p = new SupplierProduct(null, 1L, 2L, 3L, " ", new BigDecimal("2000"), null, 12, true, true);
        assertThat(p.supplierCode()).isNull();
        assertThat(p.marginPercent(new BigDecimal("2500"))).isEqualByComparingTo("20.0");
        assertThat(p.marginPercent(null)).isNull();
        assertThatThrownBy(() -> new SupplierProduct(null, 1L, 2L, 3L, null, new BigDecimal("-1"), null, 1, false, true))
                .hasMessageContaining("costo");
        assertThatThrownBy(() -> new SupplierProduct(null, 1L, 2L, 3L, null, BigDecimal.ONE, null, 0, false, true))
                .hasMessageContaining("mínima");
        assertThatThrownBy(() -> new SupplierProduct(null, 1L, 2L, 3L, null, BigDecimal.ONE, null, 1, true, false))
                .hasMessageContaining("inactivo");
    }
}
