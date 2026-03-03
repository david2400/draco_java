package com.essenza.draco.modules.inventory.application.input.combo;

import com.essenza.draco.modules.inventory.domain.dto.combo.ComboDto;

import java.util.Optional;

public interface FindComboByIdUseCase {
    Optional<ComboDto> findById(Long id);
}
