package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.product_child;

import com.essenza.draco.modules.inventory.application.output.repository.ProductChildRepository;
import com.essenza.draco.modules.inventory.domain.dto.product_child.CreateProductChildDto;
import com.essenza.draco.modules.inventory.domain.dto.product_child.ProductChildDto;
import com.essenza.draco.modules.inventory.domain.dto.product_child.UpdateProductChildDto;
import com.essenza.draco.modules.inventory.infrastructure.outbound.mappers.ProductChildMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductChildAdapter implements ProductChildRepository {
    private final JpaProductChildRepository jpa;
    private final ProductChildMapper mapper;

    public ProductChildAdapter(JpaProductChildRepository jpa, ProductChildMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ProductChildDto create(CreateProductChildDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProductChildDto update(Long id, UpdateProductChildDto input) {
        var entity = jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
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
    public Optional<ProductChildDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<ProductChildDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }

}
