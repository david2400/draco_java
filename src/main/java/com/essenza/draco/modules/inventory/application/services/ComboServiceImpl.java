// package com.essenza.draco.modules.inventory.application.services;

// import com.essenza.draco.modules.inventory.application.input.combo.CreateComboUseCase;
// import com.essenza.draco.modules.inventory.application.input.combo.DeleteComboUseCase;
// import com.essenza.draco.modules.inventory.application.input.combo.FindComboByIdUseCase;
// import com.essenza.draco.modules.inventory.application.input.combo.FindCombosUseCase;
// import com.essenza.draco.modules.inventory.application.input.combo.UpdateComboUseCase;
// import com.essenza.draco.modules.inventory.domain.dto.combo.ComboDto;
// import com.essenza.draco.modules.inventory.domain.dto.combo.CreateComboDto;
// import com.essenza.draco.modules.inventory.domain.dto.combo.UpdateComboDto;
// import com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.combo.ComboRepositoryAdapter;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import jakarta.validation.Valid;
// import java.util.List;
// import java.util.Optional;

// @Service
// @Transactional
// public class ComboServiceImpl implements CreateComboUseCase, UpdateComboUseCase, DeleteComboUseCase, FindComboByIdUseCase, FindCombosUseCase {

//     private final ComboRepositoryAdapter comboRepository;

//     public ComboServiceImpl(ComboRepositoryAdapter comboRepository) {
//         this.comboRepository = comboRepository;
//     }

//     @Override
//     public ComboDto create(@Valid CreateComboDto input) {
//         return comboRepository.create(input);
//     }

//     @Override
//     public ComboDto update(Long id, @Valid UpdateComboDto input) {
//         return comboRepository.update(id, input);
//     }

//     public boolean deleteById(Long id) {
//         return comboRepository.deleteById(id);
//     }

//     @Transactional(readOnly = true)
//     public Optional<ComboDto> findById(Long id) {
//         return comboRepository.findById(id);
//     }

//     @Transactional(readOnly = true)
//     public List<ComboDto> findAll() {
//         return comboRepository.findAll();
//     }
// }
