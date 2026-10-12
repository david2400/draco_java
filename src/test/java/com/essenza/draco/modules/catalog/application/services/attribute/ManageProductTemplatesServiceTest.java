package com.essenza.draco.modules.catalog.application.services.attribute;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.essenza.draco.shared.exceptions.ConflictException;

class ManageProductTemplatesServiceTest extends AttributeServicesTestSupport {

    ManageProductTemplatesService templateService;

    @BeforeEach
    void setUp() {
        templateService = new ManageProductTemplatesService(templates, attributes, templateDtos);
    }

    @Test
    void plantillaEnUsoNoSeBorra() {
        when(templates.productCounts()).thenReturn(Map.of(10L, 2L));
        assertThatThrownBy(() -> templateService.delete(10L)).isInstanceOf(ConflictException.class)
                .hasMessageContaining("2 productos");
    }
}
