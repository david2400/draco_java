package com.essenza.draco.modules.catalog.application.services.attribute;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.essenza.draco.modules.catalog.application.output.repository.AttributeRepository;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAttributesStore;
import com.essenza.draco.modules.catalog.application.output.repository.ProductTemplateRepository;
import com.essenza.draco.modules.catalog.domain.model.attribute.Attribute;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeDataType;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeOption;
import com.essenza.draco.modules.catalog.domain.model.attribute.ProductTemplate;
import com.essenza.draco.modules.catalog.domain.model.attribute.TemplateAttribute;

/** Mocks y datos comunes a los tests de los casos de uso de atributos (antes AttributeServicesTest). */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
abstract class AttributeServicesTestSupport {

    @Mock
    AttributeRepository attributes;
    @Mock
    ProductTemplateRepository templates;
    @Mock
    ProductAttributesStore store;

    AttributeDtosService attributeDtos;
    ProductTemplateDtosService templateDtos;
    AttributeSoporte soporte;

    static final Attribute COLOR = new Attribute(3L, "color", "Color", null, AttributeDataType.OPTION, null,
            List.of(new AttributeOption(31L, "Rojo", 0), new AttributeOption(32L, "Azul", 1)));
    static final ProductTemplate ROPA = new ProductTemplate(10L, "Ropa", null,
            List.of(new TemplateAttribute(3L, false, true, true, 0)));

    @BeforeEach
    void setUpSupport() {
        attributeDtos = new AttributeDtosService(attributes);
        templateDtos = new ProductTemplateDtosService(templates, attributes, attributeDtos);
        soporte = new AttributeSoporte(store, templates, attributes);
        when(attributes.inUse(anyCollection())).thenReturn(Set.of());
        when(attributes.optionsInUse(anyCollection())).thenReturn(Set.of());
        when(attributes.findByIds(anyCollection())).thenReturn(List.of(COLOR));
        when(attributes.findAll()).thenReturn(List.of(COLOR));
        when(templates.findById(10L)).thenReturn(Optional.of(ROPA));
        when(templates.productCounts()).thenReturn(Map.of());
    }
}
