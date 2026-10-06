package com.essenza.draco.modules.catalog.application.input.combo;

import com.essenza.draco.modules.catalog.application.dto.combo.ComboDto;

import java.util.List;

public interface FindCombosUseCase {
    List<ComboDto> findAll();
}
