package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.products;

import com.essenza.draco.modules.inventory.application.output.repository.ProductRepository;
import com.essenza.draco.modules.inventory.infrastructure.outbound.mappers.ProductMapper;
import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.ProductEntity;
import com.essenza.draco.modules.inventory.domain.dto.product.CreateProductDto;
import com.essenza.draco.modules.inventory.domain.dto.product.ProductDto;
import com.essenza.draco.modules.inventory.domain.dto.product.ProductFilter;
import com.essenza.draco.modules.inventory.domain.dto.product.UpdateProductDto;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepositoryAdapter implements ProductRepository {

    private final JpaProductRepository jpa;
    private final ProductMapper mapper;

    public ProductRepositoryAdapter(JpaProductRepository jpa, ProductMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ProductDto create(CreateProductDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProductDto update(Long id, UpdateProductDto input) {
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
    public Optional<ProductDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public Page<ProductDto> findAll(Pageable pageable) {
        return jpa.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public Page<ProductDto> findAll(ProductFilter filter, Pageable pageable) {
        var spec = byFilter(filter);
        return jpa.findAll(spec, pageable).map(mapper::toDto);
    }

    public static Specification<ProductEntity> byFilter(ProductFilter f) {
        return (root, cq, cb) -> {
            if (f == null) {
                return cb.conjunction();
            }
            List<Predicate> p = new ArrayList<>();

            if (f.getBrandIds() != null && !f.getBrandIds().isEmpty()) {
                p.add(root.get("brandId").in(f.getBrandIds()));
            }
            if (f.getCategoryIds() != null && !f.getCategoryIds().isEmpty()) {
                p.add(root.get("categoryId").in(f.getCategoryIds()));
            }
            if (f.getSubcategoryIds() != null && !f.getSubcategoryIds().isEmpty()) {
                p.add(root.get("subcategoryId").in(f.getSubcategoryIds()));
            }
            if (f.getMinPrice() != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("unitPrice"), f.getMinPrice()));
            }
            if (f.getMaxPrice() != null) {
                p.add(cb.lessThanOrEqualTo(root.get("unitPrice"), f.getMaxPrice()));
            }
//            if (f.getAvailable() != null) {
//                p.add(cb.equal(root.get("available"), f.getAvailable()));
//            }
            if (f.getSearch() != null && !f.getSearch().isBlank()) {
                String like = "%" + f.getSearch().toLowerCase() + "%";
                p.add(cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("description")), like)
                ));
            }

            return cb.and(p.toArray(new Predicate[0]));
        };
    }
}
