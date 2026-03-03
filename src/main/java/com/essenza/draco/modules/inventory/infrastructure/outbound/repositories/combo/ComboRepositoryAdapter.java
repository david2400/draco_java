// package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.combo;

// import com.essenza.draco.modules.inventory.application.output.repository.ComboRepository;
// import com.essenza.draco.modules.inventory.domain.dto.combo.ComboDto;
// import com.essenza.draco.modules.inventory.domain.dto.combo.CreateComboDto;
// import com.essenza.draco.modules.inventory.domain.dto.combo.UpdateComboDto;
// import com.essenza.draco.modules.inventory.infrastructure.outbound.mappers.ComboMapper;
// import org.springframework.stereotype.Repository;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;
// import java.util.Optional;

// @Repository
// public class ComboRepositoryAdapter implements ComboRepository {

//     private final JpaComboRepository jpa;
//     private final ComboMapper mapper;

//     public ComboRepositoryAdapter(JpaComboRepository jpa, ComboMapper mapper) {
//         this.jpa = jpa;
//         this.mapper = mapper;
//     }

//     @Override
//     @Transactional
//     public ComboDto create(CreateComboDto input) {
//         var entity = mapper.toEntity(input);
//         var saved = jpa.save(entity);
//         return mapper.toDto(saved);
//     }

//     @Override
//     @Transactional
//     public ComboDto update(Long id, UpdateComboDto input) {
//         var entity = jpa.findById(id)
//                 .orElseThrow(() -> new IllegalArgumentException("Combo not found: " + id));
//         mapper.updateEntityFromDto(input, entity);
//         var updated = jpa.save(entity);
//         return mapper.toDto(updated);
//     }

//     @Override
//     @Transactional
//     public boolean deleteById(Long id) {
//         if (!jpa.existsById(id)) return false;
//         jpa.deleteById(id);
//         return true;
//     }

//     @Override
//     @Transactional(readOnly = true)
//     public Optional<ComboDto> findById(Long id) {
//         return jpa.findById(id).map(mapper::toDto);
//     }

//     @Override
//     @Transactional(readOnly = true)
//     public List<ComboDto> findAll() {
//         return jpa.findAll().stream().map(mapper::toDto).toList();
//     }
// }
