package com.essenza.draco.modules.catalog.attribute.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.essenza.draco.modules.catalog.application.dto.attribute.AttributeOptionDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.SaveAttributeDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.SaveProductAttributesDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.VariantAttributesDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.VariantOptionDto;
import com.essenza.draco.modules.catalog.application.output.repository.AttributeRepository;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAttributesStore;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAttributesStore.ProductHeader;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAttributesStore.VariantSku;
import com.essenza.draco.modules.catalog.application.output.repository.ProductTemplateRepository;
import com.essenza.draco.modules.catalog.application.services.AttributeServiceImpl;
import com.essenza.draco.modules.catalog.application.services.ProductAttributesServiceImpl;
import com.essenza.draco.modules.catalog.application.services.ProductTemplateServiceImpl;
import com.essenza.draco.modules.catalog.domain.model.attribute.Attribute;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeDataType;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeOption;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeRuleViolation;
import com.essenza.draco.modules.catalog.domain.model.attribute.ProductTemplate;
import com.essenza.draco.modules.catalog.domain.model.attribute.TemplateAttribute;
import com.essenza.draco.shared.exceptions.ConflictException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AttributeServicesTest {

    @Mock
    AttributeRepository attributes;
    @Mock
    ProductTemplateRepository templates;
    @Mock
    ProductAttributesStore store;

    AttributeServiceImpl attributeService;
    ProductTemplateServiceImpl templateService;
    ProductAttributesServiceImpl productAttributes;

    static final Attribute COLOR = new Attribute(3L, "color", "Color", null, AttributeDataType.OPTION, null,
            List.of(new AttributeOption(31L, "Rojo", 0), new AttributeOption(32L, "Azul", 1)));
    static final ProductTemplate ROPA = new ProductTemplate(10L, "Ropa", null,
            List.of(new TemplateAttribute(3L, false, true, true, 0)));

    @BeforeEach
    void setUp() {
        attributeService = new AttributeServiceImpl(attributes);
        templateService = new ProductTemplateServiceImpl(templates, attributes, attributeService);
        productAttributes = new ProductAttributesServiceImpl(store, templates, attributes, attributeService, templateService);
        when(attributes.inUse(anyCollection())).thenReturn(Set.of());
        when(attributes.optionsInUse(anyCollection())).thenReturn(Set.of());
        when(attributes.findByIds(anyCollection())).thenReturn(List.of(COLOR));
        when(attributes.findAll()).thenReturn(List.of(COLOR));
        when(templates.findById(10L)).thenReturn(Optional.of(ROPA));
        when(templates.productCounts()).thenReturn(Map.of());
    }

    @Test
    void codigoRepetidoEs409() {
        when(attributes.existsActiveCode("color", null)).thenReturn(true);
        assertThatThrownBy(() -> attributeService.create(new SaveAttributeDto("color", "Color", null, "OPTION", null,
                List.of(new AttributeOptionDto(null, "Rojo", null)))))
                .isInstanceOf(ConflictException.class);
        verify(attributes, never()).save(any());
    }

    @Test
    void noSeQuitaUnaOpcionEnUso() {
        when(attributes.findById(3L)).thenReturn(Optional.of(COLOR));
        when(attributes.optionsInUse(Set.of(32L))).thenReturn(Set.of(32L));
        assertThatThrownBy(() -> attributeService.update(3L, new SaveAttributeDto("color", "Color", null, "OPTION", null,
                List.of(new AttributeOptionDto(31L, "Rojo", 0)))))
                .isInstanceOf(ConflictException.class).hasMessageContaining("Azul");
    }

    @Test
    void noSeCambiaElTipoDeUnAtributoEnUso() {
        when(attributes.findById(3L)).thenReturn(Optional.of(COLOR));
        when(attributes.inUse(Set.of(3L))).thenReturn(Set.of(3L));
        assertThatThrownBy(() -> attributeService.update(3L, new SaveAttributeDto("color", "Color", null, "TEXT", null, null)))
                .isInstanceOf(ConflictException.class).hasMessageContaining("tipo");
    }

    @Test
    void plantillaEnUsoNoSeBorra() {
        when(templates.productCounts()).thenReturn(Map.of(10L, 2L));
        assertThatThrownBy(() -> templateService.delete(10L)).isInstanceOf(ConflictException.class)
                .hasMessageContaining("2 productos");
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
    void noSePublicaConVariantesIncompletas() {
        when(store.findProduct(5L)).thenReturn(Optional.of(new ProductHeader(5L, "DRAFT", 10L)));
        when(store.variantSkus(5L)).thenReturn(List.of(new VariantSku(100L, "SKU-1", "Rojo", true)));
        when(store.values(5L)).thenReturn(List.of());
        when(store.skuOptions(anyCollection())).thenReturn(Map.of());
        assertThatThrownBy(() -> productAttributes.assertPublishable(5L))
                .isInstanceOf(ConflictException.class).hasMessageContaining("variante");

        when(store.skuOptions(anyCollection())).thenReturn(Map.of(100L, Map.of(3L, 31L)));
        productAttributes.assertPublishable(5L);
    }
}
