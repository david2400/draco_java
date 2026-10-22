package com.essenza.draco.modules.devolution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.essenza.draco.modules.devolution.domain.model.DevolutionRuleViolation;
import com.essenza.draco.modules.devolution.domain.model.DevolutionState;
import com.essenza.draco.modules.devolution.domain.model.ItemCondition;
import com.essenza.draco.modules.devolution.domain.model.ReturnCase;
import com.essenza.draco.modules.devolution.domain.model.ReturnItem;

class ReturnCaseDomainTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 12, 10, 0);

    /** Devolución guardada (ids 1 y 2) de 2 de 3 unidades de la línea 10 y 1 unidad de la línea 20. */
    private static ReturnCase saved(DevolutionState state) {
        return new ReturnCase(5L, 99L, state, 1L, "Cliente devuelve", null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, List.of(
                new ReturnItem(1L, 10L, 100L, 1000L, "SKU-A", "Camisa · M", 2, new BigDecimal("33333.33"), null, null,
                        null, null, null, null, 0),
                new ReturnItem(2L, 20L, 200L, 2000L, "SKU-B", "Gorra", 1, new BigDecimal("50000.00"), null, null,
                        null, null, null, null, 0)));
    }

    private static Map<Long, ReturnCase.LineValue> orderLines() {
        return Map.of(10L, new ReturnCase.LineValue(new BigDecimal("100000.00"), 3),
                20L, new ReturnCase.LineValue(new BigDecimal("50000.00"), 1));
    }

    @Test
    void requestNeedsMotiveObservationAndUniqueLines() {
        ReturnItem a = ReturnItem.requested(10L, 100L, 1000L, "SKU-A", "Camisa", 1, BigDecimal.TEN, null);
        assertThatThrownBy(() -> ReturnCase.request(99L, null, "x", null, null, null, List.of(a)))
                .isInstanceOf(DevolutionRuleViolation.class);
        assertThatThrownBy(() -> ReturnCase.request(99L, 1L, " ", null, null, null, List.of(a)))
                .isInstanceOf(DevolutionRuleViolation.class);
        assertThatThrownBy(() -> ReturnCase.request(99L, 1L, "x", null, null, null, List.of()))
                .isInstanceOf(DevolutionRuleViolation.class);
        assertThatThrownBy(() -> ReturnCase.request(99L, 1L, "x", null, null, null, List.of(a, a)))
                .hasMessageContaining("una sola vez");
        assertThat(ReturnCase.request(99L, 1L, "x", null, null, null, List.of(a)).state()).isEqualTo(DevolutionState.PENDING);
    }

    @Test
    void stateMachineRejectsOutOfOrderActions() {
        ReturnCase c = saved(DevolutionState.PENDING);
        assertThatThrownBy(() -> c.receive("ana", Map.of(), null, NOW)).hasMessageContaining("debe estar aprobada");
        c.approve("ana", 3L, NOW);
        assertThat(c.state()).isEqualTo(DevolutionState.APPROVED);
        assertThat(c.returnMethodId()).isEqualTo(3L);
        assertThat(c.actions()).containsExactly("receive", "reject");
        assertThatThrownBy(() -> c.approve("ana", null, NOW)).isInstanceOf(DevolutionRuleViolation.class);
        assertThatThrownBy(() -> c.addItem(ReturnItem.requested(30L, 1L, 1L, "X", "X", 1, BigDecimal.ONE, null)))
                .hasMessageContaining("pendiente");
    }

    @Test
    void receiveDefaultsToFullQuantityAndRequiresSomething() {
        ReturnCase c = saved(DevolutionState.APPROVED);
        assertThatThrownBy(() -> c.receive("ana", Map.of(1L, 3), null, NOW)).hasMessageContaining("entre 0 y 2");
        assertThatThrownBy(() -> c.receive("ana", Map.of(1L, 0, 2L, 0), null, NOW)).hasMessageContaining("rechaza");
        assertThatThrownBy(() -> c.receive("ana", Map.of(9L, 1), null, NOW)).hasMessageContaining("no pertenece");
        c.receive("ana", Map.of(1L, 1), "Caja abierta", NOW);
        assertThat(c.items().get(0).received()).isEqualTo(1);
        assertThat(c.items().get(1).received()).isEqualTo(1);
        assertThat(c.inspectionNotes()).isEqualTo("Caja abierta");
        assertThat(c.state()).isEqualTo(DevolutionState.RECEIVED);
    }

    @Test
    void inspectComputesProportionalCreditAndRestockByCondition() {
        ReturnCase c = saved(DevolutionState.APPROVED);
        c.receive("ana", Map.of(), null, NOW); // 2 camisas + 1 gorra
        List<ReturnItem> restock = c.inspect("luis", 7L, Map.of(
                        1L, new ReturnCase.Inspection(ItemCondition.OPENED, null, new BigDecimal("5000")),
                        2L, new ReturnCase.Inspection(ItemCondition.DEFECTIVE, null, null)),
                orderLines(), "Revisado", NOW);
        // 2 de 3 camisas de 100.000 = 66.666,67 − 5.000 de reposición; gorra completa 50.000.
        assertThat(c.items().get(0).refundAmount()).isEqualByComparingTo("61666.67");
        assertThat(c.items().get(1).refundAmount()).isEqualByComparingTo("50000.00");
        assertThat(c.creditValue()).isEqualByComparingTo("111666.67");
        assertThat(restock).extracting(ReturnItem::id).containsExactly(1L);
        assertThat(c.items().get(0).restockedQuantity()).isEqualTo(2);
        assertThat(c.items().get(1).restock()).isFalse();
        assertThat(c.warehouseId()).isEqualTo(7L);
        assertThat(c.state()).isEqualTo(DevolutionState.INSPECTED);
    }

    @Test
    void inspectAllowsOverridingRestockAndValidatesFee() {
        ReturnCase c = saved(DevolutionState.APPROVED);
        c.receive("ana", Map.of(), null, NOW);
        assertThatThrownBy(() -> c.inspect("luis", null, Map.of(
                        1L, new ReturnCase.Inspection(ItemCondition.NEW, null, new BigDecimal("70000")),
                        2L, new ReturnCase.Inspection(ItemCondition.NEW, null, null)),
                orderLines(), null, NOW)).hasMessageContaining("supera el valor devuelto");
        assertThatThrownBy(() -> c.inspect("luis", null, Map.of(1L, new ReturnCase.Inspection(ItemCondition.NEW, null, null)),
                orderLines(), null, NOW)).hasMessageContaining("Falta la inspección");
        List<ReturnItem> restock = c.inspect("luis", null, Map.of(
                        1L, new ReturnCase.Inspection(ItemCondition.NEW, false, null),
                        2L, new ReturnCase.Inspection(ItemCondition.DAMAGED, true, null)),
                orderLines(), null, NOW);
        assertThat(restock).extracting(ReturnItem::id).containsExactly(2L);
    }

    @Test
    void refundAmountDefaultsToCreditAndCannotExceedIt() {
        ReturnCase c = saved(DevolutionState.APPROVED);
        c.receive("ana", Map.of(1L, 0), null, NOW);
        c.inspect("luis", null, Map.of(2L, new ReturnCase.Inspection(ItemCondition.NEW, null, null)), orderLines(), null, NOW);
        assertThat(c.refundAmountFor(null)).isEqualByComparingTo("50000.00");
        assertThat(c.refundAmountFor(BigDecimal.ZERO)).isEqualByComparingTo("0");
        assertThatThrownBy(() -> c.refundAmountFor(new BigDecimal("50000.01"))).hasMessageContaining("supera");
        c.markRefunded("caja", 4L, new BigDecimal("50000"), "PAY-99-X: 50000", NOW);
        assertThat(c.state()).isEqualTo(DevolutionState.REFUNDED);
        assertThat(c.actions()).isEmpty();
        assertThat(c.refundMethodId()).isEqualTo(4L);
    }

    @Test
    void rejectOnlyBeforeInspectionAndFreesUnits() {
        ReturnCase c = saved(DevolutionState.RECEIVED);
        assertThatThrownBy(() -> c.reject("ana", " ", NOW)).hasMessageContaining("motivo");
        c.reject("ana", "Producto usado", NOW);
        assertThat(c.state()).isEqualTo(DevolutionState.REJECTED);
        assertThat(c.unitsFor(c.items().get(0))).isZero();
        assertThatThrownBy(() -> saved(DevolutionState.INSPECTED).reject("ana", "x", NOW))
                .isInstanceOf(DevolutionRuleViolation.class);
    }

    @Test
    void conditionParsingAndDefaults() {
        assertThat(ItemCondition.parse("opened").restockByDefault()).isTrue();
        assertThat(ItemCondition.parse("DAMAGED").restockByDefault()).isFalse();
        assertThatThrownBy(() -> ItemCondition.parse("USED")).isInstanceOf(DevolutionRuleViolation.class);
        assertThat(DevolutionState.fromCode("f")).contains(DevolutionState.REFUNDED);
    }
}
