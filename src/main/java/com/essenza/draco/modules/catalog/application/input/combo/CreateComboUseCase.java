package com.essenza.draco.modules.catalog.application.input.combo;

import com.essenza.draco.modules.catalog.application.dto.combo.ComboDto;
import com.essenza.draco.modules.catalog.application.dto.combo.CreateComboDto;

public interface CreateComboUseCase {
    ComboDto create(CreateComboDto input);
}
