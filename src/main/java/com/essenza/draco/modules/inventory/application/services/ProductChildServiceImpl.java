package com.essenza.draco.modules.inventory.application.services;

import com.essenza.draco.modules.inventory.application.input.product_child.CreateProductChildUseCase;
import com.essenza.draco.modules.inventory.application.input.product_child.DeleteProductChildUseCase;
import com.essenza.draco.modules.inventory.application.input.product_child.FindProductChildByIdUseCase;
import com.essenza.draco.modules.inventory.application.input.product_child.FindProductChildrenUseCase;
import com.essenza.draco.modules.inventory.application.input.product_child.UpdateProductChildUseCase;
import com.essenza.draco.modules.inventory.domain.dto.product_child.CreateProductChildDto;
import com.essenza.draco.modules.inventory.domain.dto.product_child.ProductChildDto;
import com.essenza.draco.modules.inventory.domain.dto.product_child.UpdateProductChildDto;
import com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.product_child.ProductChildAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductChildServiceImpl implements CreateProductChildUseCase, UpdateProductChildUseCase, DeleteProductChildUseCase, FindProductChildByIdUseCase, FindProductChildrenUseCase {

    private final ProductChildAdapter productChildRepository;

    public ProductChildServiceImpl(ProductChildAdapter productChildRepository) {
        this.productChildRepository = productChildRepository;
    }

    @Override
    public ProductChildDto create(CreateProductChildDto input) {
        return productChildRepository.create(input);
    }

    @Override
    public ProductChildDto update(Long id, UpdateProductChildDto input) {
        return productChildRepository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        return productChildRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductChildDto> findById(Long id) {
        return productChildRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductChildDto> findAll() {
        return productChildRepository.findAll();
    }
}
