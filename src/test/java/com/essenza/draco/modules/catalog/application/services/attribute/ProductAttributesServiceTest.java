package com.essenza.draco.modules.catalog.application.services.attribute;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.essenza.draco.modules.catalog.application.dto.attribute.SaveProductAttributesDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.VariantAttributesDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.VariantOptionDto;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAttributesStore.ProductHeader;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAttributesStore.VariantSku;
import com.essenza.draco.modules.catalog.domain.model.attribute.Attribute;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeDataType;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeRuleViolation;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeValue;

class ProductAttributesServiceTest extends AttributeServicesTestSupport {

    ProductAttributesService productAttributes;

    @BeforeEach
    void setUp() {
        productAttributes = new ProductAttributesService(store, templates, attributeDtos, templateDtos, soporte);
    }

    @Test
    void guardaEjesDeVarianteYRechazaCombinacionRepetida() {
        when(store.findProduct(5L)).thenReturn(Optional.of(new ProductHeader(5L, "DRAFT", 10L)));
        when(store.variantSkus(5L)).thenReturn(List.of(new VariantSku(100L, "SKU-1", "Rojo", true),
                new VariantSku(101L, "SKU-2", "Azul", true)));
        when(store.values(5L)).thenReturn(List.of());
        when(store.skuOptions(anyCollection())).thenReturn(Map.of(100L, Map.of(3L, 31L)));

        var dto = productAttributes.save(5L, new SaveProductAttributesDto(10L, List.of(), List.of(
                new VariantAttributesDto(100L, null, null, null, List.of(new VariantOptionDto(3L, 31L))))));
        verify(store).setTemplate(5L, 10L);
        verify(store).replaceSkuOptions(eq(Set.of(100L, 101L)), any());
        assertThat(dto.missing()).containsExactly("1 variante sin todos los ejes");

        assertThatThrownBy(() -> productAttributes.save(5L, new SaveProductAttributesDto(10L, List.of(), List.of(
                new VariantAttributesDto(100L, null, null, null, List.of(new VariantOptionDto(3L, 31L))),
                new VariantAttributesDto(101L, null, null, null, List.of(new VariantOptionDto(3L, 31L)))))))
                .isInstanceOf(AttributeRuleViolation.class).hasMessageContaining("misma combinación");
    }

    @Test
    void conPlantillaLaFichaMuestraAtributosAdicionalesPeroNoEjes() {
        Attribute material = new Attribute(1L, "material", "Material", null, AttributeDataType.TEXT, null, List.of());
        when(attributes.findByIds(anyCollection())).thenReturn(List.of(COLOR, material));
        when(store.findProduct(5L)).thenReturn(Optional.of(new ProductHeader(5L, "DRAFT", 10L)));
        when(store.variantSkus(5L)).thenReturn(List.of());
        when(store.skuOptions(anyCollection())).thenReturn(Map.of());
        // Material no está en la plantilla (adicional); Color es eje y quedó de una plantilla anterior.
        when(store.values(5L)).thenReturn(List.of(new AttributeValue(1L, "Lino", null, null, null),
                new AttributeValue(3L, null, null, null, 31L)));

        var dto = productAttributes.get(5L);
        assertThat(dto.values()).extracting(value -> value.attributeId()).containsExactly(1L);
        assertThat(dto.attributes()).extracting(attribute -> attribute.id()).containsExactly(3L, 1L);
    }
}
