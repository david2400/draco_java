package com.essenza.draco.modules.catalog.attribute.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.essenza.draco.modules.catalog.domain.model.attribute.Attribute;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeDataType;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeOption;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeRuleViolation;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeValue;
import com.essenza.draco.modules.catalog.domain.model.attribute.ProductAttributesPolicy;
import com.essenza.draco.modules.catalog.domain.model.attribute.ProductTemplate;
import com.essenza.draco.modules.catalog.domain.model.attribute.TemplateAttribute;
import com.essenza.draco.modules.catalog.domain.model.attribute.VariantSelection;

class ProductAttributesPolicyTest {

    static final Attribute MATERIAL = new Attribute(1L, "material", "Material", null, AttributeDataType.TEXT, null, List.of());
    static final Attribute PESO = new Attribute(2L, "peso", "Peso", null, AttributeDataType.NUMBER, 9L, List.of());
    static final Attribute COLOR = new Attribute(3L, "color", "Color", null, AttributeDataType.OPTION, null,
            List.of(new AttributeOption(31L, "Rojo", 0), new AttributeOption(32L, "Azul", 1)));
    static final Attribute TALLA = new Attribute(4L, "talla", "Talla", null, AttributeDataType.OPTION, null,
            List.of(new AttributeOption(41L, "S", 0), new AttributeOption(42L, "M", 1)));
    static final Map<Long, Attribute> DEFS = Map.of(1L, MATERIAL, 2L, PESO, 3L, COLOR, 4L, TALLA);

    static final ProductTemplate ROPA = new ProductTemplate(10L, "Ropa", null, List.of(
            new TemplateAttribute(1L, true, false, true, 0),
            new TemplateAttribute(2L, false, false, false, 1),
            new TemplateAttribute(3L, false, true, true, 2),
            new TemplateAttribute(4L, false, true, true, 3)));

    static AttributeValue text(long attributeId, String value) {
        return new AttributeValue(attributeId, value, null, null, null);
    }

    @Test
    void normalizaYDescartaValoresVacios() {
        var result = ProductAttributesPolicy.check(ROPA, DEFS,
                List.of(text(1L, "  Algodón "), new AttributeValue(2L, null, null, null, null)),
                List.of(), Set.of(), false);
        assertThat(result.values()).containsExactly(text(1L, "Algodón"));
    }

    @Test
    void elTipoDelValorDebeCoincidir() {
        assertThatThrownBy(() -> ProductAttributesPolicy.check(ROPA, DEFS, List.of(text(2L, "pesado")), List.of(), Set.of(), false))
                .isInstanceOf(AttributeRuleViolation.class).hasMessageContaining("espera un número");
    }

    @Test
    void conPlantillaSoloSeAceptanSusAtributosYLosEjesNoVanEnLaFicha() {
        ProductTemplate solo = new ProductTemplate(11L, "Solo material", null, List.of(new TemplateAttribute(1L, false, false, false, 0)));
        assertThatThrownBy(() -> ProductAttributesPolicy.check(solo, DEFS,
                List.of(new AttributeValue(2L, null, BigDecimal.ONE, null, null)), List.of(), Set.of(), false))
                .hasMessageContaining("no pertenece a la plantilla");
        assertThatThrownBy(() -> ProductAttributesPolicy.check(ROPA, DEFS,
                List.of(new AttributeValue(3L, null, null, null, 31L)), List.of(), Set.of(), false))
                .hasMessageContaining("eje de variante");
    }

    @Test
    void dosVariantesNoPuedenRepetirCombinacion() {
        var a = new VariantSelection(100L, Map.of(3L, 31L, 4L, 41L));
        var b = new VariantSelection(101L, Map.of(3L, 31L, 4L, 41L));
        assertThatThrownBy(() -> ProductAttributesPolicy.check(ROPA, DEFS, List.of(), List.of(a, b), Set.of(100L, 101L), false))
                .hasMessageContaining("misma combinación").hasMessageContaining("Rojo").hasMessageContaining("S");
    }

    @Test
    void laOpcionDebeSerDelEjeYElSkuDelProducto() {
        assertThatThrownBy(() -> ProductAttributesPolicy.check(ROPA, DEFS, List.of(),
                List.of(new VariantSelection(100L, Map.of(3L, 41L))), Set.of(100L), false))
                .hasMessageContaining("no pertenece");
        assertThatThrownBy(() -> ProductAttributesPolicy.check(ROPA, DEFS, List.of(),
                List.of(new VariantSelection(999L, Map.of(3L, 31L))), Set.of(100L), false))
                .hasMessageContaining("no es una variante");
    }

    @Test
    void enBorradorSePuedeGuardarIncompletoPeroPublicadoNo() {
        var parcial = List.of(new VariantSelection(100L, Map.of(3L, 31L)));
        var draft = ProductAttributesPolicy.check(ROPA, DEFS, List.of(), parcial, Set.of(100L), false);
        assertThat(draft.variants()).hasSize(1);
        assertThatThrownBy(() -> ProductAttributesPolicy.check(ROPA, DEFS, List.of(), parcial, Set.of(100L), true))
                .hasMessageContaining("Material").hasMessageContaining("1 variante sin todos los ejes");
    }

    @Test
    void missingListaObligatoriosYVariantesIncompletas() {
        var missing = ProductAttributesPolicy.missing(ROPA, DEFS, List.of(text(1L, "Lino")),
                List.of(new VariantSelection(100L, Map.of(3L, 31L, 4L, 42L))), Set.of(100L, 101L));
        assertThat(missing).containsExactly("1 variante sin todos los ejes");
        assertThat(ProductAttributesPolicy.missing(null, DEFS, List.of(), List.of(), Set.of(100L))).isEmpty();
    }

    @Test
    void sinPlantillaNoHayEjesDeVariante() {
        assertThatThrownBy(() -> ProductAttributesPolicy.check(null, DEFS, List.of(),
                List.of(new VariantSelection(100L, Map.of(3L, 31L))), Set.of(100L), false))
                .hasMessageContaining("no es un eje de variante");
    }

    @Test
    void reglasDeDefinicion() {
        assertThatThrownBy(() -> new Attribute(null, "Color", "Color", null, AttributeDataType.TEXT, null, List.of()))
                .hasMessageContaining("código");
        assertThatThrownBy(() -> new Attribute(null, "color", "Color", null, AttributeDataType.OPTION, null, List.of()))
                .hasMessageContaining("al menos una opción");
        assertThatThrownBy(() -> new Attribute(null, "color", "Color", null, AttributeDataType.OPTION, null,
                List.of(new AttributeOption(null, "Rojo", 0), new AttributeOption(null, "rojo", 1))))
                .hasMessageContaining("repetida");
        ProductTemplate malEje = new ProductTemplate(null, "X", null, List.of(new TemplateAttribute(1L, false, true, false, 0)));
        assertThatThrownBy(() -> malEje.validateAgainst(DEFS)).hasMessageContaining("solo los atributos de opciones");
        assertThatThrownBy(() -> AttributeDataType.parse("fecha")).hasMessageContaining("no válido");
    }
}
