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
import com.essenza.draco.modules.catalog.application.output.repository.UnitReferences;
import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductChildServiceImpl implements CreateProductChildUseCase, UpdateProductChildUseCase, DeleteProductChildUseCase, FindProductChildByIdUseCase, FindProductChildrenUseCase, BulkDeleteProductChildrenUseCase {

    private final ProductChildRepository productChildRepository;
    private final UnitReferences unitReferences;

    public ProductChildServiceImpl(ProductChildRepository productChildRepository) {
        this(productChildRepository, unitId -> true);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public ProductChildServiceImpl(ProductChildRepository productChildRepository, UnitReferences unitReferences) {
        this.unitReferences = unitReferences;
        this.productChildRepository = productChildRepository;
    }

    @Override
    public ProductChildDto create(CreateProductChildDto input) {
        checkNetContent(input);
        return productChildRepository.create(input);
    }

    @Override
    public ProductChildDto update(Long id, UpdateProductChildDto input) {
        checkNetContent(input);
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

    /** Contenido neto: valor y unidad juntos, valor > 0 y unidad existente. */
    private void checkNetContent(CreateProductChildDto input) {
        if (input.getNetContent() == null && input.getNetContentUnitId() == null) {
            return;
        }
        if (input.getNetContent() == null || input.getNetContentUnitId() == null) {
            throw new IllegalArgumentException("El contenido neto necesita valor y unidad.");
        }
        if (input.getNetContent().signum() <= 0) {
            throw new IllegalArgumentException("El contenido neto debe ser mayor que 0.");
        }
        if (!unitReferences.exists(input.getNetContentUnitId())) {
            throw new IllegalArgumentException("La unidad del contenido neto no existe: " + input.getNetContentUnitId());
        }
    }
}
