package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.product_combo;

import com.essenza.draco.modules.inventory.application.output.repository.ProductComboRepository;
import com.essenza.draco.modules.inventory.domain.dto.product_combo.CreateProductComboDto;
import com.essenza.draco.modules.inventory.domain.dto.product_combo.ProductComboDto;
import com.essenza.draco.modules.inventory.domain.dto.product_combo.UpdateProductComboDto;
import com.essenza.draco.modules.inventory.infrastructure.outbound.mappers.ProductComboMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductComboRepositoryAdapter implements ProductComboRepository {

    private final JpaProductComboRepository jpa;
    private final ProductComboMapper mapper;

    public ProductComboRepositoryAdapter(JpaProductComboRepository jpa, ProductComboMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ProductComboDto create(CreateProductComboDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProductComboDto update(Long id, UpdateProductComboDto input) {
        var entity = jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ProductCombo not found: " + id));
        mapper.updateEntityFromDto(input, entity);
        var updated = jpa.save(entity);
        return mapper.toDto(updated);
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        if (!jpa.existsById(id)) return false;
        jpa.deleteById(id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductComboDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductComboDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }
}
