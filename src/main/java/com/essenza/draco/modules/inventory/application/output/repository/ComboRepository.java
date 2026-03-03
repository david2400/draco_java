package com.essenza.draco.modules.inventory.application.output.repository;

import com.essenza.draco.modules.inventory.domain.dto.combo.ComboDto;
import com.essenza.draco.modules.inventory.domain.dto.combo.CreateComboDto;
import com.essenza.draco.modules.inventory.domain.dto.combo.UpdateComboDto;

import java.util.List;
import java.util.Optional;

public interface ComboRepository {

    ComboDto create(CreateComboDto input);

    ComboDto update(Long id, UpdateComboDto input);

    boolean deleteById(Long id);

    Optional<ComboDto> findById(Long id);

    List<ComboDto> findAll();
}
