package com.essenza.draco.modules.inventory.application.services;

import com.essenza.draco.modules.inventory.application.input.product_combo.CreateProductComboUseCase;
import com.essenza.draco.modules.inventory.application.input.product_combo.DeleteProductComboUseCase;
import com.essenza.draco.modules.inventory.application.input.product_combo.FindProductComboByIdUseCase;
import com.essenza.draco.modules.inventory.application.input.product_combo.FindProductCombosUseCase;
import com.essenza.draco.modules.inventory.application.input.product_combo.UpdateProductComboUseCase;
import com.essenza.draco.modules.inventory.domain.dto.product_combo.CreateProductComboDto;
import com.essenza.draco.modules.inventory.domain.dto.product_combo.ProductComboDto;
import com.essenza.draco.modules.inventory.domain.dto.product_combo.UpdateProductComboDto;
import com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.product_combo.ProductComboRepositoryAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductComboServiceImpl implements CreateProductComboUseCase, UpdateProductComboUseCase, DeleteProductComboUseCase, FindProductComboByIdUseCase, FindProductCombosUseCase {

    private final ProductComboRepositoryAdapter productComboRepository;

    public ProductComboServiceImpl(ProductComboRepositoryAdapter productComboRepository) {
        this.productComboRepository = productComboRepository;
    }

    @Override
    public ProductComboDto create(@Valid CreateProductComboDto input) {
        return productComboRepository.create(input);
    }

    @Override
    public ProductComboDto update(Long id, @Valid UpdateProductComboDto input) {
        return productComboRepository.update(id, input);
    }

    public boolean deleteById(Long id) {
        return productComboRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<ProductComboDto> findById(Long id) {
        return productComboRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<ProductComboDto> findAll() {
        return productComboRepository.findAll();
    }
}
