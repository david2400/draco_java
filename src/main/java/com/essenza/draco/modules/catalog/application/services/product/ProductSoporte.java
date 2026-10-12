package com.essenza.draco.modules.catalog.application.services.product;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.dto.product.ProductDto;
import com.essenza.draco.modules.catalog.application.dto.product.ProductSkuDto;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAggregateRepository;
import com.essenza.draco.modules.catalog.application.output.repository.ProductCatalogViewRepository;
import com.essenza.draco.modules.catalog.application.output.repository.ProductRepository;
import com.essenza.draco.modules.catalog.application.output.repository.UnitReferences;
import com.essenza.draco.modules.catalog.application.services.ProductDtoAssembler;
import com.essenza.draco.modules.catalog.domain.command.ProductCommand;
import com.essenza.draco.shared.common.inventory.StockQuery;
import com.essenza.draco.shared.common.web.Slugs;

/** Código compartido por los casos de uso de product (antes en ProductServiceImpl). */
@Component
@RequiredArgsConstructor
class ProductSoporte {

    private final ProductRepository productRepository;
    private final ProductAggregateRepository aggregateRepository;
    private final ProductDtoAssembler productDtoAssembler;
    private final ProductCatalogViewRepository catalogView;
    private final StockQuery stockQuery;
    private final UnitReferences unitReferences;

    /**
     * Completa la página con variantes y componentes en lote (3 consultas en
     * total) en lugar de releer cada fila (antes: ~3 consultas por producto).
     */
    Page<ProductDto> hydratePage(Page<ProductDto> basePage) {
        List<Long> ids = basePage.getContent().stream().map(ProductDto::getId).toList();
        Map<Long, ProductDto> hydrated = aggregateRepository.findAllByIds(ids).stream()
                .map(productDtoAssembler::toDto)
                .collect(Collectors.toMap(ProductDto::getId, Function.identity(), (a, b) -> a));
        enrich(hydrated.values());
        return basePage.map(dto -> hydrated.getOrDefault(dto.getId(), dto));
    }

    ProductDto enrich(ProductDto dto) {
        enrich(List.of(dto));
        return dto;
    }

    /** Añade SKUs e imágenes (dos consultas en lote para toda la página). */
    void enrich(java.util.Collection<ProductDto> dtos) {
        List<Long> ids = dtos.stream().map(ProductDto::getId).filter(java.util.Objects::nonNull).toList();
        if (ids.isEmpty()) {
            return;
        }
        var skus = catalogView.findSkusByProductIds(ids);
        var images = catalogView.findImagesByProductIds(ids);
        List<Long> skuIds = skus.values().stream().flatMap(List::stream).map(ProductSkuDto::getId).toList();
        Map<Long, Integer> onHand = stockQuery.onHandBySku(skuIds);
        dtos.forEach(dto -> {
            List<ProductSkuDto> productSkus = skus.getOrDefault(dto.getId(), List.of());
            dto.setSkus(productSkus);
            dto.setImages(images.getOrDefault(dto.getId(), List.of()));
            applyStock(dto, productSkus, onHand);
        });
    }

    /**
     * Fase 3: el stock mostrado sale del inventario (stock_items), no de las
     * columnas heredadas. Recalcula también si el producto es vendible.
     */
    private static void applyStock(ProductDto dto, List<ProductSkuDto> productSkus, Map<Long, Integer> onHand) {
        if (productSkus.isEmpty()) {
            return;
        }
        Map<Long, Integer> byVariant = new java.util.HashMap<>();
        int total = 0;
        for (ProductSkuDto sku : productSkus) {
            int units = onHand.getOrDefault(sku.getId(), 0);
            sku.setStock(units);
            total += units;
            if (sku.getVariantId() != null) {
                byVariant.put(sku.getVariantId(), units);
            }
        }
        dto.setStock(total);
        boolean listed = Boolean.TRUE.equals(dto.getAvailable());
        if (dto.getVariants() != null && !dto.getVariants().isEmpty()) {
            dto.getVariants().forEach(variant -> {
                int units = byVariant.getOrDefault(variant.getId(), 0);
                variant.setStock(units);
                variant.setSellable(Boolean.TRUE.equals(variant.getAvailable()) && units > 0);
            });
            dto.setSellable(listed && dto.getVariants().stream().anyMatch(v -> Boolean.TRUE.equals(v.getSellable())));
        } else {
            dto.setSellable(listed && total > 0);
        }
    }

    /** Slug normalizado y único (sufijo -2, -3… si ya existe). */
    ProductCommand withUniqueSlug(ProductCommand command, Long excludeId) {
        String base = Slugs.resolve(command.getSlug(), command.getName());
        if (base.isBlank()) {
            base = "producto";
        }
        String candidate = base;
        int suffix = 2;
        while (productRepository.existsBySlug(candidate, excludeId)) {
            candidate = base + "-" + suffix++;
        }
        return command.toBuilder().slug(candidate).build();
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

    void checkNetContentUnit(Long unitId) {
        if (unitId != null && !unitReferences.exists(unitId)) {
            throw new IllegalArgumentException("La unidad del contenido neto no existe: " + unitId);
        }
    }
}
