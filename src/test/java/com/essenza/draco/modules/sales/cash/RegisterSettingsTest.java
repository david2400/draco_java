package com.essenza.draco.modules.sales.cash;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.essenza.draco.modules.sales.domain.model.cash.CashRegister;
import com.essenza.draco.modules.sales.domain.model.cash.CashRuleViolation;
import com.essenza.draco.modules.sales.domain.model.cash.OpeningMode;
import com.essenza.draco.modules.sales.domain.model.cash.RegisterSettings;
import com.essenza.draco.modules.sales.domain.model.cash.Store;

class RegisterSettingsTest {

    private static RegisterSettings settings(OpeningMode mode, String base, String limit, String tolerance, String maxDiscount) {
        return new RegisterSettings(List.of(3L, 2L, 2L), mode, base == null ? null : new BigDecimal(base),
                limit == null ? null : new BigDecimal(limit), true, tolerance == null ? null : new BigDecimal(tolerance),
                maxDiscount == null ? null : new BigDecimal(maxDiscount), false, "58mm", "  Terminal 1 ");
    }

    @Test
    void normalizesAndAllowsMethods() {
        RegisterSettings s = settings(OpeningMode.FREE, null, null, null, null);
        assertThat(s.allowedMethodIds()).containsExactly(2L, 3L);
        assertThat(s.allows(2L)).isTrue();
        assertThat(s.allows(9L)).isFalse();
        assertThat(RegisterSettings.defaults().allows(9L)).isTrue();
        assertThat(s.receiptWidth()).isEqualTo("58MM");
        assertThat(s.deviceName()).isEqualTo("Terminal 1");
        assertThat(s.differenceTolerance()).isEqualByComparingTo("0");
    }

    @Test
    void openingAmountByMode() {
        assertThat(settings(OpeningMode.FREE, null, null, null, null).openingAmount(null)).isEqualByComparingTo("0");
        assertThat(settings(OpeningMode.FREE, null, null, null, null).openingAmount(new BigDecimal("5000"))).isEqualByComparingTo("5000");
        RegisterSettings suggested = settings(OpeningMode.SUGGESTED, "100000", null, null, null);
        assertThat(suggested.openingAmount(null)).isEqualByComparingTo("100000");
        assertThat(suggested.openingAmount(new BigDecimal("80000"))).isEqualByComparingTo("80000");
        RegisterSettings fixed = settings(OpeningMode.FIXED, "150000", null, null, null);
        assertThat(fixed.openingAmount(null)).isEqualByComparingTo("150000");
        assertThat(fixed.openingAmount(new BigDecimal("150000.00"))).isEqualByComparingTo("150000");
        assertThatThrownBy(() -> fixed.openingAmount(new BigDecimal("1"))).hasMessageContaining("base fija");
        assertThatThrownBy(() -> settings(OpeningMode.FIXED, null, null, null, null)).isInstanceOf(CashRuleViolation.class);
    }

    @Test
    void limitsToleranceAndDiscount() {
        RegisterSettings s = settings(OpeningMode.SUGGESTED, "100000", "500000", "2000", "10");
        assertThat(s.overCashLimit(new BigDecimal("500000"))).isFalse();
        assertThat(s.overCashLimit(new BigDecimal("500000.01"))).isTrue();
        assertThat(s.exceedsTolerance(new BigDecimal("-2000"))).isFalse();
        assertThat(s.exceedsTolerance(new BigDecimal("-2000.01"))).isTrue();
        s.checkDiscount("Crema", new BigDecimal("70000"), new BigDecimal("7000"));
        assertThatThrownBy(() -> s.checkDiscount("Crema", new BigDecimal("70000"), new BigDecimal("7000.01")))
                .hasMessageContaining("máximo de la caja (10 %");
        assertThatThrownBy(() -> settings(OpeningMode.SUGGESTED, "100000", "50000", null, null)).hasMessageContaining("tope");
        assertThatThrownBy(() -> settings(OpeningMode.FREE, null, null, null, "120")).hasMessageContaining("0 a 100");
        assertThatThrownBy(() -> new RegisterSettings(List.of(), OpeningMode.FREE, null, null, false, null, null, false, "90MM", null))
                .hasMessageContaining("Ancho");
        assertThat(OpeningMode.parse(null)).isEqualTo(OpeningMode.FREE);
        assertThatThrownBy(() -> OpeningMode.parse("x")).isInstanceOf(CashRuleViolation.class);
    }

    @Test
    void storeAndRegisterValidation() {
        Store store = new Store(null, "tienda_norte", " Tienda Norte ", " Cra 1 ", null, null, "norte@essenza.co", 2L, null,
                null, "Gracias", true);
        assertThat(store.code()).isEqualTo("TIENDA_NORTE");
        assertThat(store.name()).isEqualTo("Tienda Norte");
        assertThat(store.address()).isEqualTo("Cra 1");
        assertThatThrownBy(() -> new Store(null, "T", "x", null, null, null, null, 1L, null, null, null, true))
                .isInstanceOf(CashRuleViolation.class);
        assertThatThrownBy(() -> new Store(null, "TT", "x", null, null, null, "mal", 1L, null, null, null, true))
                .hasMessageContaining("correo");
        assertThatThrownBy(() -> new Store(null, "TT", "x", null, null, null, null, 1L, null, null, null, true, null, null, 5001L))
                .hasMessageContaining("departamento");
        assertThatThrownBy(() -> new Store(null, "TT", "x", null, null, null, null, 1L, null, null, null, true, null, 5L, 5001L))
                .hasMessageContaining("país");
        Store located = new Store(null, "TT", "x", null, "Medellín", null, null, 1L, null, null, null, true, 1L, 5L, 5001L);
        assertThat(located.cityId()).isEqualTo(5001L);
        CashRegister register = new CashRegister(null, "caja_9", "Caja 9", null, null, true);
        assertThat(register.settings()).isEqualTo(RegisterSettings.defaults());
        assertThat(register.storeId()).isNull();
    }
}
