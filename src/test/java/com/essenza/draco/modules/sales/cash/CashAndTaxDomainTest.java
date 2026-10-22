package com.essenza.draco.modules.sales.cash;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.essenza.draco.modules.sales.domain.model.LineTax;
import com.essenza.draco.modules.sales.domain.model.cash.CashMovement;
import com.essenza.draco.modules.sales.domain.model.cash.CashMovementType;
import com.essenza.draco.modules.sales.domain.model.cash.CashRegister;
import com.essenza.draco.modules.sales.domain.model.cash.CashRuleViolation;
import com.essenza.draco.modules.sales.domain.model.cash.CashSession;
import com.essenza.draco.modules.sales.domain.model.cash.CashSessionStatus;

class CashAndTaxDomainTest {

    @Test
    void taxIncludedInPrice() {
        LineTax iva = LineTax.included(1L, "IVA_19", "IVA", new BigDecimal("19"), new BigDecimal("35000"));
        assertThat(iva.base()).isEqualByComparingTo("29411.76");
        assertThat(iva.amount()).isEqualByComparingTo("5588.24");
        assertThat(iva.base().add(iva.amount())).isEqualByComparingTo("35000");
        LineTax excluded = LineTax.included(4L, "EXCLUIDO", "EXCLUDED", BigDecimal.ZERO, new BigDecimal("20000"));
        assertThat(excluded.amount()).isEqualByComparingTo("0");
        assertThat(excluded.base()).isEqualByComparingTo("20000");
        assertThat(iva.forTotal(new BigDecimal("119")).base()).isEqualByComparingTo("100");
        LineTax none = LineTax.included(null, null, null, null, new BigDecimal("10"));
        assertThat(none.amount()).isEqualByComparingTo("0");
    }

    @Test
    void sessionOpenAndClose() {
        CashSession session = CashSession.open(1L, "Ana", new BigDecimal("100000"), null, Instant.now());
        assertThat(session.status()).isEqualTo(CashSessionStatus.OPEN);
        session.close("Ana", new BigDecimal("150000"), new BigDecimal("149000"), "faltan mil", Instant.now());
        assertThat(session.cashDifference()).isEqualByComparingTo("-1000");
        assertThatThrownBy(session::requireOpen).isInstanceOf(CashRuleViolation.class);
        assertThatThrownBy(() -> session.close("Ana", BigDecimal.ONE, BigDecimal.ONE, null, Instant.now()))
                .isInstanceOf(CashRuleViolation.class);
        assertThatThrownBy(() -> CashSession.open(1L, " ", BigDecimal.ZERO, null, Instant.now())).isInstanceOf(CashRuleViolation.class);
        assertThatThrownBy(() -> CashSession.open(1L, "Ana", new BigDecimal("-1"), null, Instant.now()))
                .isInstanceOf(CashRuleViolation.class);
    }

    @Test
    void movementsAndRegisters() {
        CashMovement out = new CashMovement(null, 1L, CashMovementType.OUT, new BigDecimal("5000"), "Retiro", null, null, Instant.now());
        assertThat(out.signedAmount()).isEqualByComparingTo("-5000");
        assertThatThrownBy(() -> new CashMovement(null, 1L, CashMovementType.IN, BigDecimal.ZERO, "x", null, null, Instant.now()))
                .isInstanceOf(CashRuleViolation.class);
        assertThat(new CashRegister(null, "caja_2", "Caja 2", null, null, true).code()).isEqualTo("CAJA_2");
        assertThatThrownBy(() -> new CashRegister(null, "1", "x", null, null, true)).isInstanceOf(CashRuleViolation.class);
    }
}
