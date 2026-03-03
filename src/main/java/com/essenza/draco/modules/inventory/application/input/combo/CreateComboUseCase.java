package com.essenza.draco.modules.inventory.application.input.combo;

import com.essenza.draco.modules.inventory.domain.dto.combo.ComboDto;
import com.essenza.draco.modules.inventory.domain.dto.combo.CreateComboDto;

public interface CreateComboUseCase {
    ComboDto create(CreateComboDto input);
}
