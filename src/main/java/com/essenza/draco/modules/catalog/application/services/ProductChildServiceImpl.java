package com.essenza.draco.modules.catalog.application.services;

import com.essenza.draco.modules.catalog.application.input.product_child.BulkDeleteProductChildrenUseCase;
import com.essenza.draco.modules.catalog.application.input.product_child.CreateProductChildUseCase;
import com.essenza.draco.modules.catalog.application.input.product_child.DeleteProductChildUseCase;
import com.essenza.draco.modules.catalog.application.input.product_child.FindProductChildByIdUseCase;
import com.essenza.draco.modules.catalog.application.input.product_child.FindProductChildrenUseCase;
import com.essenza.draco.modules.catalog.application.input.product_child.UpdateProductChildUseCase;
import com.essenza.draco.modules.catalog.application.dto.product_child.CreateProductChildDto;
import com.essenza.draco.modules.catalog.application.dto.product_child.ProductChildDto;
import com.essenza.draco.modules.catalog.application.dto.product_child.UpdateProductChildDto;
import com.essenza.draco.modules.catalog.application.output.repository.ProductChildRepository;
import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductChildServiceImpl implements CreateProductChildUseCase, UpdateProductChildUseCase, DeleteProductChildUseCase, FindProductChildByIdUseCase, FindProductChildrenUseCase, BulkDeleteProductChildrenUseCase {

    private final ProductChildRepository productChildRepository;

    public ProductChildServiceImpl(ProductChildRepository productChildRepository) {
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
    public BulkOperationResult deleteAll(List<Long> ids) {
        List<Long> unique = ids.stream().distinct().toList();
        BulkOperationResult.Builder result = new BulkOperationResult.Builder(unique.size());
        for (Long id : unique) {
            if (productChildRepository.deleteById(id)) result.success();
            else result.failure(id, "Variante de producto no encontrada: " + id);
        }
        return result.build();
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
