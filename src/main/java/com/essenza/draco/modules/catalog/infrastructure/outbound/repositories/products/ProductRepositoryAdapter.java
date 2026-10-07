package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.products;

import com.essenza.draco.modules.catalog.domain.model.NetContent;

import com.essenza.draco.modules.catalog.application.output.repository.ProductRepository;
import com.essenza.draco.modules.catalog.application.dto.product.CreateProductDto;
import com.essenza.draco.modules.catalog.application.dto.product.ProductDto;
import com.essenza.draco.modules.catalog.application.dto.product.ProductFilter;
import com.essenza.draco.modules.catalog.application.dto.product.UpdateProductDto;
import com.essenza.draco.modules.catalog.domain.model.BundleItem;
import com.essenza.draco.modules.catalog.domain.model.Product;
import com.essenza.draco.modules.catalog.domain.model.ProductType;
import com.essenza.draco.modules.catalog.domain.model.Variant;
import com.essenza.draco.modules.catalog.infrastructure.outbound.mappers.ProductMapper;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductChildEntity;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductComboEntity;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductEntity;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.product_child.JpaProductChildRepository;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.product_combo.JpaProductComboRepository;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.sku.ProductSkuSynchronizer;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.sku.SkuSyncResult;
import com.essenza.draco.shared.common.inventory.StockLevelRequested;
import org.springframework.context.ApplicationEventPublisher;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ProductRepositoryAdapter implements ProductRepository {

    private final JpaProductRepository jpa;
    private final ProductMapper mapper;
    private final JpaProductChildRepository childRepository;
    private final JpaProductComboRepository comboRepository;
    private final ProductSkuSynchronizer skuSynchronizer;
    private final ApplicationEventPublisher events;

    public ProductRepositoryAdapter(JpaProductRepository jpa,
                                    ProductMapper mapper,
                                    JpaProductChildRepository childRepository,
                                    JpaProductComboRepository comboRepository,
                                    ProductSkuSynchronizer skuSynchronizer,
                                    ApplicationEventPublisher events) {
        this.jpa = jpa;
        this.mapper = mapper;
        this.childRepository = childRepository;
        this.comboRepository = comboRepository;
        this.skuSynchronizer = skuSynchronizer;
        this.events = events;
    }

    @Transactional
    public Long save(Product product) {
        ProductEntity entity = toEntity(product);
        ProductEntity saved = jpa.save(entity);
        Map<Long, Integer> desiredByChild = syncVariants(saved.getId(), product);
        syncBundleItems(saved.getId(), product);
        SkuSyncResult skus = skuSynchronizer.sync(saved.getId());
        publishStock(saved.getId(), product, desiredByChild, skus);
        return saved.getId();
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
        skuSynchronizer.removeAll(id);
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

    /** Dinero con 2 decimales (DECIMAL(15,2)), redondeo comercial. */
    private static BigDecimal money(BigDecimal value) {
        return Optional.ofNullable(value).orElse(BigDecimal.ZERO).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    @Override
    public boolean existsBySlug(String slug, Long excludeId) {
        return excludeId == null ? jpa.existsBySlug(slug) : jpa.existsBySlugAndIdNot(slug, excludeId);
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
            if (f.getStatuses() != null && !f.getStatuses().isEmpty()) {
                p.add(root.get("status").in(f.getStatuses().stream()
                        .map(s -> s == null ? "" : s.trim().toUpperCase(java.util.Locale.ROOT)).toList()));
            }
            if (f.getAvailable() != null) {
                p.add(cb.equal(root.get("available"), f.getAvailable()));
            }
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

    private ProductEntity toEntity(Product product) {
        int stock = product.getStockInfo() == null ? 0 : product.getStockInfo().getOnHand();
        BigDecimal realPrice = money(product.getRealPrice());
        BigDecimal unitPrice = money(product.getUnitPrice());

        return ProductEntity.builder()
                .id(product.getId() == null ? null : product.getId().getValue())
                .name(product.getName())
                .description(product.getDescription())
                .stock(stock)
                .realPrice(realPrice)
                .unitPrice(unitPrice)
                .status(product.getStatus().name())
                .slug(product.getSlug())
                .productType(product.getType().name())
                .length(product.getLength())
                .width(product.getWidth())
                .height(product.getHeight())
                .weight(product.getWeight())
                .netContent(NetContent.valueOf(product.getNetContent()))
                .netContentUnitId(NetContent.unitOf(product.getNetContent()))
                .imageUrl(product.getImageUrl())
                .available(product.isListed())
                .brandId(product.getBrandId())
                .categoryId(product.getCategoryId())
                .subcategoryId(product.getSubcategoryId())
                .supplierId(product.getSupplierId())
                .isCombo(product.getType() == ProductType.COMBO)
                .build();
    }

    /**
     * Fase 3: el stock lo gobierna el inventario. El "stock" del formulario se
     * convierte en una petición de ajuste por SKU (si ya coincide, no hay movimiento).
     */
    private void publishStock(Long productId, Product product, Map<Long, Integer> desiredByChild, SkuSyncResult skus) {
        if (product.getType().supportsVariants()) {
            desiredByChild.forEach((childId, desired) -> {
                Long skuId = skus.skuByChildId().get(childId);
                if (skuId != null) {
                    events.publishEvent(new StockLevelRequested(productId, skuId, desired, "PRODUCT_EDIT"));
                }
            });
        } else if (skus.defaultSkuId() != null) {
            events.publishEvent(new StockLevelRequested(productId, skus.defaultSkuId(),
                    product.getStockInfo().getOnHand(), "PRODUCT_EDIT"));
        }
    }

    private Map<Long, Integer> syncVariants(Long productId, Product product) {
        List<ProductChildEntity> existingEntities = childRepository.findByProductId(productId);
        Map<Long, Integer> desiredByChild = new java.util.LinkedHashMap<>();

        if (!product.getType().supportsVariants()) {
            if (!existingEntities.isEmpty()) {
                childRepository.deleteAll(existingEntities);
            }
            return desiredByChild;
        }

        Map<Long, ProductChildEntity> existingById = existingEntities.stream()
                .collect(Collectors.toMap(ProductChildEntity::getId, Function.identity()));

        for (Variant variant : product.getVariants()) {
            Long variantId = variant.getId() == null ? null : variant.getId().getValue();
            ProductChildEntity entity = variantId == null ? null : existingById.remove(variantId);

            if (entity == null) {
                entity = ProductChildEntity.builder()
                        .id(variantId)
                        .productId(productId)
                        .build();
            }

            entity.setProductId(productId);
            entity.setName(variant.getName());
            entity.setDescription(variant.getDescription());
            entity.setStock(variant.getStockInfo().getOnHand());
            entity.setUnitPrice(money(variant.getUnitPrice()));
            entity.setImageUrl(variant.getImageUrl());
            entity.setAvailable(variant.isListed());

            ProductChildEntity savedChild = childRepository.save(entity);
            desiredByChild.put(savedChild.getId(), variant.getStockInfo().getOnHand());
        }

        if (!existingById.isEmpty()) {
            childRepository.deleteAll(existingById.values());
        }
        return desiredByChild;
    }

    private void syncBundleItems(Long comboId, Product product) {
        List<ProductComboEntity> existing = comboRepository.findByComboId(comboId);
        if (!existing.isEmpty()) {
            comboRepository.deleteAll(existing);
        }

        if (!product.getType().supportsBundles()) {
            return;
        }

        for (BundleItem item : product.getBundleItems()) {
            ProductComboEntity entity = ProductComboEntity.builder()
                    .comboId(comboId)
                    .productId(item.getProductId().getValue())
                    .quantity(item.getQuantity())
                    .build();
            comboRepository.save(entity);
        }
    }
}
