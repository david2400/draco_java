package com.essenza.draco.modules.catalog.variant;

import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeOption;
import com.essenza.draco.modules.catalog.domain.model.attribute.ProductAttributesPolicy;
import com.essenza.draco.modules.catalog.domain.model.attribute.Attribute;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeDataType;
import com.essenza.draco.modules.catalog.domain.model.attribute.VariantSelection;
import com.essenza.draco.modules.catalog.domain.model.variant.VariantCodes;
import com.essenza.draco.modules.catalog.domain.model.variant.VariantPlanner;
import com.essenza.draco.modules.catalog.domain.model.variant.VariantPlanner.Axis;
import com.essenza.draco.modules.catalog.domain.model.variant.VariantPlanner.Existing;
import com.essenza.draco.modules.catalog.domain.model.variant.VariantPlanner.Row;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VariantPlannerTest {

    private static final Long COLOR = 13L;
    private static final Long TALLA = 14L;

    private static Axis color() {
        Map<Long, String> o = new LinkedHashMap<>();
        o.put(9L, "Rojo intenso");
        o.put(10L, "Azul");
        return new Axis(COLOR, "Color", o);
    }

    private static Axis talla() {
        Map<Long, String> o = new LinkedHashMap<>();
        o.put(11L, "S");
        o.put(12L, "M");
        return new Axis(TALLA, "Talla", o);
    }

    private static Row row(Long child, long color, long talla) {
        return new Row(child, Map.of(COLOR, color, TALLA, talla), new BigDecimal("50000"), new BigDecimal("20000"), null,
                null, null, null, true, 0);
    }

    @Test
    void creaActualizaYQuitaSegunHistorial() {
        List<Existing> existing = List.of(
                new Existing(1L, 101L, Map.of(COLOR, 9L, TALLA, 11L), false),
                new Existing(2L, 102L, Map.of(COLOR, 10L, TALLA, 11L), true),
                new Existing(3L, 103L, Map.of(COLOR, 10L, TALLA, 12L), false));
        var plan = VariantPlanner.plan(List.of(color(), talla()), List.of(row(null, 9, 11), row(null, 9, 12)), existing);
        assertThat(plan.updates()).hasSize(1);
        assertThat(plan.updates().get(0).current().childId()).isEqualTo(1L);
        assertThat(plan.updates().get(0).name()).isEqualTo("Rojo intenso / S");
        assertThat(plan.creates()).hasSize(1);
        assertThat(plan.creates().get(0).name()).isEqualTo("Rojo intenso / M");
        assertThat(plan.removals()).extracting(r -> r.current().childId() + ":" + r.deactivate())
                .containsExactlyInAnyOrder("2:true", "3:false");
    }

    @Test
    void varianteSinEjesSeAsignaPorId() {
        var plan = VariantPlanner.plan(List.of(color()),
                List.of(new Row(7L, Map.of(COLOR, 10L), BigDecimal.TEN, null, null, null, null, null, true, 0)),
                List.of(new Existing(7L, 107L, Map.of(), true)));
        assertThat(plan.updates()).hasSize(1);
        assertThat(plan.removals()).isEmpty();
    }

    @Test
    void validaciones() {
        assertThatThrownBy(() -> VariantPlanner.plan(List.of(), List.of(), List.of())).hasMessageContaining("al menos un eje");
        assertThatThrownBy(() -> VariantPlanner.plan(List.of(color(), talla()), List.of(row(null, 9, 11), row(null, 9, 11)), List.of()))
                .hasMessageContaining("repetida");
        assertThatThrownBy(() -> VariantPlanner.plan(List.of(color(), talla()),
                List.of(new Row(null, Map.of(COLOR, 9L), BigDecimal.TEN, null, null, null, null, null, true, 0)), List.of()))
                .hasMessageContaining("Talla");
        assertThatThrownBy(() -> VariantPlanner.plan(List.of(color()),
                List.of(new Row(null, Map.of(COLOR, 99L), BigDecimal.TEN, null, null, null, null, null, true, 0)), List.of()))
                .hasMessageContaining("no está entre");
        assertThatThrownBy(() -> VariantPlanner.plan(List.of(color()),
                List.of(new Row(null, Map.of(COLOR, 9L), BigDecimal.ZERO, null, null, null, null, null, true, 0)), List.of()))
                .hasMessageContaining("precio");
        assertThatThrownBy(() -> VariantPlanner.plan(List.of(color()),
                List.of(new Row(null, Map.of(COLOR, 9L), BigDecimal.TEN, null, BigDecimal.ONE, null, null, null, true, 0)), List.of()))
                .hasMessageContaining("precio antes");
        assertThatThrownBy(() -> VariantPlanner.plan(List.of(color()),
                List.of(new Row(null, Map.of(COLOR, 9L), BigDecimal.TEN, null, null, "a b c ñ", null, null, true, 0)), List.of()))
                .hasMessageContaining("SKU");
        // Una combinación conservada (desactivada por historial) no se puede volver a crear como nueva.
        assertThatThrownBy(() -> VariantPlanner.plan(List.of(color()),
                List.of(new Row(8L, Map.of(COLOR, 9L), BigDecimal.TEN, null, null, null, null, null, true, 0)),
                List.of(new Existing(8L, 108L, Map.of(COLOR, 10L), false), new Existing(9L, 109L, Map.of(COLOR, 9L), true))))
                .hasMessageContaining("historial");
    }

    @Test
    void codigosSugeridos() {
        assertThat(VariantCodes.abbreviation("Rojo intenso")).isEqualTo("ROJ");
        assertThat(VariantCodes.abbreviation("XL")).isEqualTo("XL");
        assertThat(VariantCodes.abbreviation("50 ml")).isEqualTo("50ML");
        assertThat(VariantCodes.abbreviation("Azúl")).isEqualTo("AZUL");
        assertThat(VariantPlanner.suggestCode("SKU-000003", List.of(color(), talla()), Map.of(COLOR, 9L, TALLA, 12L)))
                .isEqualTo("SKU-000003-ROJ-M");
    }

    @Test
    void ejesPropiosDelProductoEnLaFicha() {
        Attribute colorDef = new Attribute(COLOR, "color", "Color", null, AttributeDataType.OPTION, null,
                List.of(new AttributeOption(9L, "Rojo", 0), new AttributeOption(10L, "Azul", 1)));
        var result = ProductAttributesPolicy.check(null, Set.of(COLOR), Map.of(COLOR, colorDef), List.of(),
                List.of(new VariantSelection(1L, Map.of(COLOR, 9L))), Set.of(1L, 2L), false);
        assertThat(result.variants()).hasSize(1);
        assertThat(ProductAttributesPolicy.missing(null, Set.of(COLOR), Map.of(COLOR, colorDef), List.of(),
                result.variants(), Set.of(1L, 2L))).containsExactly("1 variante sin todos los ejes");
        assertThatThrownBy(() -> ProductAttributesPolicy.check(null, Set.of(), Map.of(COLOR, colorDef), List.of(),
                List.of(new VariantSelection(1L, Map.of(COLOR, 9L))), Set.of(1L), false)).hasMessageContaining("eje");
    }
}
