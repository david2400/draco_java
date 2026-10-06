package com.essenza.draco.modules.catalog.application.input.combo;

import com.essenza.draco.modules.catalog.application.dto.combo.ComboDto;

import java.util.Optional;

public interface FindComboByIdUseCase {
    Optional<ComboDto> findById(Long id);
}
