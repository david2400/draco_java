package com.essenza.draco.modules.catalog.application.services.attribute;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.essenza.draco.modules.catalog.application.dto.attribute.AttributeOptionDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.SaveAttributeDto;
import com.essenza.draco.shared.exceptions.ConflictException;

class ManageAttributesServiceTest extends AttributeServicesTestSupport {

    ManageAttributesService attributeService;

    @BeforeEach
    void setUp() {
        attributeService = new ManageAttributesService(attributes, attributeDtos);
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
}
