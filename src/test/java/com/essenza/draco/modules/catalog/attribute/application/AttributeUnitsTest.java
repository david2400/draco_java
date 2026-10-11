package com.essenza.draco.modules.catalog.attribute.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.essenza.draco.modules.catalog.application.dto.attribute.SaveAttributeDto;
import com.essenza.draco.modules.catalog.application.output.repository.AttributeRepository;
import com.essenza.draco.modules.catalog.application.output.repository.AttributeRepository.UnitInfo;
import com.essenza.draco.modules.catalog.application.services.AttributeServiceImpl;
import com.essenza.draco.modules.catalog.domain.model.attribute.Attribute;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeDataType;
import com.essenza.draco.shared.exceptions.ConflictException;

class AttributeUnitsTest {

    private final AttributeRepository repo = mock(AttributeRepository.class);
    private final AttributeServiceImpl service = new AttributeServiceImpl(repo);

    private static final Attribute VOLUME = new Attribute(7L, "contenido", "Contenido", null, AttributeDataType.NUMBER, 1L, List.of());

    @BeforeEach
    void setUp() {
        when(repo.unitExists(anyLong())).thenReturn(true);
        when(repo.unitNames(anyCollection())).thenReturn(Map.of());
        when(repo.inUse(anyCollection())).thenReturn(Set.of(7L));
        when(repo.optionsInUse(anyCollection())).thenReturn(Set.of());
        when(repo.findById(7L)).thenReturn(Optional.of(VOLUME));
        when(repo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(repo.unit(1L)).thenReturn(Optional.of(new UnitInfo(1L, "ml", "VOLUME", new BigDecimal("0.001"), true)));
        when(repo.unit(2L)).thenReturn(Optional.of(new UnitInfo(2L, "L", "VOLUME", BigDecimal.ONE, true)));
        when(repo.unit(3L)).thenReturn(Optional.of(new UnitInfo(3L, "g", "MASS", new BigDecimal("0.001"), true)));
    }

    private static SaveAttributeDto number(Long unitId) {
        return new SaveAttributeDto("contenido", "Contenido", null, "NUMBER", unitId, null);
    }

    @Test
    void changingUnitWithinDimensionConvertsStoredValues() {
        when(repo.countNumberValues(7L)).thenReturn(4L);
        service.update(7L, number(2L));
        ArgumentCaptor<BigDecimal> factor = ArgumentCaptor.forClass(BigDecimal.class);
        verify(repo).rescaleNumberValues(eq(7L), factor.capture());
        assertThat(factor.getValue()).isEqualByComparingTo("0.001");
    }

    @Test
    void changingUnitToOtherDimensionWithValuesIsConflict() {
        when(repo.countNumberValues(7L)).thenReturn(2L);
        assertThatThrownBy(() -> service.update(7L, number(3L))).isInstanceOf(ConflictException.class)
                .hasMessageContaining("otra magnitud");
        assertThatThrownBy(() -> service.update(7L, number(null))).isInstanceOf(ConflictException.class);
        verify(repo, never()).save(any());
    }

    @Test
    void changingUnitWithoutValuesNeedsNoConversion() {
        when(repo.countNumberValues(7L)).thenReturn(0L);
        service.update(7L, number(3L));
        verify(repo, never()).rescaleNumberValues(anyLong(), any());
    }

    @Test
    void unitIsDroppedForNonNumericTypes() {
        when(repo.existsActiveCode("material", null)).thenReturn(false);
        service.create(new SaveAttributeDto("material", "Material", null, "TEXT", 1L, null));
        ArgumentCaptor<Attribute> saved = ArgumentCaptor.forClass(Attribute.class);
        verify(repo).save(saved.capture());
        assertThat(saved.getValue().getUnitId()).isNull();
    }
}
