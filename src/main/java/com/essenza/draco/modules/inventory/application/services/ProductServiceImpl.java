package com.essenza.draco.modules.inventory.application.services;

import com.essenza.draco.modules.inventory.application.input.product.*;
import com.essenza.draco.modules.inventory.domain.dto.product.CreateProductDto;
import com.essenza.draco.modules.inventory.domain.dto.product.ProductDto;
import com.essenza.draco.modules.inventory.domain.dto.product.ProductFilter;
import com.essenza.draco.modules.inventory.domain.dto.product.UpdateProductDto;
import com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.products.ProductRepositoryAdapter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional
public class ProductServiceImpl implements CreateProductUseCase,
        UpdateProductUseCase,
        DeleteProductUseCase,
        FindProductByIdUseCase,
        FindProductsUseCase,
        FindProductsPageUseCase {

    private final ProductRepositoryAdapter productRepository;

    public ProductServiceImpl(ProductRepositoryAdapter productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductDto create(@Valid CreateProductDto input) {
        return productRepository.create(input);
    }

    @Override
    public ProductDto update(Long id, @Valid UpdateProductDto input) {
        return productRepository.update(id, input);
    }

    @Override
    public boolean deleteById(Long id) {
        return productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDto> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> findAllPage(Pageable pageable, ProductFilter filter) {
        return productRepository.findAll(filter, pageable);
    }
//        return productRepository.findAll();
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<ProductDto> searchByNameOrDescription(String term) {
//        // TODO: Implement custom query (e.g., via @Query) once entities expose getters or use Specifications
//        return productRepository.findAll();
//    }

//    private ProductEntity mapToEntity(CreateProductInput input, Long id) {
//        // Use Lombok builder to avoid direct field accessors
//        ProductEntity.ProductEntityBuilder builder = ProductEntity.builder()
//                .name(input.getName())
//                .description(input.getDescription())
//                .stock(input.getStock())
//                .realPrice(input.getRealPrice())
//                .unitPrice(input.getUnitPrice())
//                .length(input.getLength())
//                .width(input.getWidth())
//                .height(input.getHeight())
//                .weight(input.getWeight())
//                .imageUrl(input.getImageUrl())
//                .available(input.getAvailable())
//                .brandId(input.getBrandId())
//                .categoryId(input.getCategoryId())
//                .subcategoryId(input.getSubcategoryId())
//                .supplierId(input.getSupplierId());
//
//        if (id != null) {
//            // Many Lombok builders allow setting id via builder as well
//            builder.id(id);
//        }
//
//        return builder.build();
//    }
}
