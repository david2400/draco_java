package com.essenza.draco.modules.inventory.application.input.combo;

import com.essenza.draco.modules.inventory.domain.dto.combo.UpdateComboDto;
import com.essenza.draco.modules.inventory.domain.dto.combo.ComboDto;

public interface UpdateComboUseCase {
    ComboDto update(Long id, UpdateComboDto input);
}
