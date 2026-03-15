package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.products;

import com.essenza.draco.modules.inventory.application.output.repository.ProductAggregateRepository;
import com.essenza.draco.modules.inventory.domain.model.BundleItem;
import com.essenza.draco.modules.inventory.domain.model.Product;
import com.essenza.draco.modules.inventory.domain.model.ProductId;
import com.essenza.draco.modules.inventory.domain.model.ProductType;
import com.essenza.draco.modules.inventory.domain.model.StockInfo;
import com.essenza.draco.modules.inventory.domain.model.Variant;
import com.essenza.draco.modules.inventory.domain.model.VariantId;
import com.essenza.draco.modules.inventory.domain.services.ProductDraft;
import com.essenza.draco.modules.inventory.domain.services.ProductFactory;
import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.ProductChildEntity;
import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.ProductComboEntity;
import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.ProductEntity;
import com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.product_child.JpaProductChildRepository;
import com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.product_combo.JpaProductComboRepository;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class ProductAggregateRepositoryAdapter implements ProductAggregateRepository {

    private final JpaProductRepository productRepository;
    private final JpaProductChildRepository childRepository;
    private final JpaProductComboRepository comboRepository;
    private final ProductFactory productFactory;

    public ProductAggregateRepositoryAdapter(JpaProductRepository productRepository,
                                             JpaProductChildRepository childRepository,
                                             JpaProductComboRepository comboRepository,
                                             ProductFactory productFactory) {
        this.productRepository = productRepository;
        this.childRepository = childRepository;
        this.comboRepository = comboRepository;
        this.productFactory = productFactory;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id).map(this::toAggregate);
    }

    @Override
    public List<Product> findAllByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<ProductEntity> products = productRepository.findAllById(ids);
        Map<Long, List<ProductChildEntity>> childrenByProductId = childRepository.findByProductIdIn(ids)
                .stream()
                .collect(Collectors.groupingBy(ProductChildEntity::getProductId));
        Map<Long, List<ProductComboEntity>> combosByProductId = comboRepository.findByComboIdIn(ids)
                .stream()
                .collect(Collectors.groupingBy(ProductComboEntity::getComboId));

        return products.stream()
                .map(entity -> toAggregate(entity,
                        childrenByProductId.getOrDefault(entity.getId(), List.of()),
                        combosByProductId.getOrDefault(entity.getId(), List.of())))
                .toList();
    }

    private Product toAggregate(ProductEntity entity) {
        List<ProductChildEntity> childEntities = childRepository.findByProductId(entity.getId());
        List<ProductComboEntity> comboEntities = Boolean.TRUE.equals(entity.getIsCombo())
                ? comboRepository.findByComboId(entity.getId())
                : List.of();
        return toAggregate(entity, childEntities, comboEntities);
    }

    private Product toAggregate(ProductEntity entity,
                                List<ProductChildEntity> childEntities,
                                List<ProductComboEntity> comboEntities) {

        ProductType type = resolveType(entity, !childEntities.isEmpty());
        ProductDraft draft = new ProductDraft(
                ProductId.of(entity.getId()),
                entity.getName(),
                entity.getDescription(),
                type,
                StockInfo.of(nullSafeInt(entity.getStock()), 0),
                BigDecimal.valueOf(entity.getRealPrice()),
                BigDecimal.valueOf(entity.getUnitPrice()),
                entity.getLength(),
                entity.getWidth(),
                entity.getHeight(),
                entity.getWeight(),
                entity.getImageUrl(),
                entity.getBrandId(),
                entity.getCategoryId(),
                entity.getSubcategoryId(),
                entity.getSupplierId(),
                Boolean.TRUE.equals(entity.getAvailable())
        );

        List<Variant> variants = type.supportsVariants()
                ? childEntities.stream().map(this::toVariant).toList()
                : List.of();

        List<BundleItem> bundleItems = type.supportsBundles()
                ? comboEntities.stream().map(this::toBundleItem).toList()
                : List.of();

        return productFactory.fromDraft(draft, variants, bundleItems);
    }

    private Variant toVariant(ProductChildEntity child) {
        return Variant.builder(VariantId.of(child.getId()))
                .name(child.getName())
                .description(child.getDescription())
                .stockInfo(StockInfo.of(nullSafeInt(child.getStock()), 0))
                .unitPrice(BigDecimal.valueOf(child.getUnitPrice()))
                .imageUrl(child.getImageUrl())
                .available(Boolean.TRUE.equals(child.getAvailable()))
                .build();
    }

    private BundleItem toBundleItem(ProductComboEntity combo) {
        return BundleItem.of(ProductId.of(combo.getProductId()), combo.getQuantity());
    }

    private ProductType resolveType(ProductEntity entity, boolean hasVariants) {
        if (Boolean.TRUE.equals(entity.getIsCombo())) {
            return ProductType.COMBO;
        }
        if (hasVariants) {
            return ProductType.VARIANT;
        }
        return ProductType.SIMPLE;
    }

    private int nullSafeInt(Integer value) {
        return value == null ? 0 : value;
    }
}
