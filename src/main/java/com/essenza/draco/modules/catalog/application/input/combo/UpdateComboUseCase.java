package com.essenza.draco.modules.catalog.application.input.combo;

import com.essenza.draco.modules.catalog.application.dto.combo.UpdateComboDto;
import com.essenza.draco.modules.catalog.application.dto.combo.ComboDto;

public interface UpdateComboUseCase {
    ComboDto update(Long id, UpdateComboDto input);
}
