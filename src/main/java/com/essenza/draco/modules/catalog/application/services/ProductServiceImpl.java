package com.essenza.draco.modules.catalog.application.services;

import com.essenza.draco.modules.catalog.application.input.product.*;
import com.essenza.draco.modules.catalog.application.input.attribute.ProductPublicationGuard;
import com.essenza.draco.modules.catalog.domain.model.ProductStatus;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAggregateRepository;
import com.essenza.draco.modules.catalog.domain.services.ProductFactory;
import com.essenza.draco.modules.catalog.application.dto.product.CreateProductDto;
import com.essenza.draco.modules.catalog.application.dto.product.ProductDto;
import com.essenza.draco.modules.catalog.application.dto.product.ProductFilter;
import com.essenza.draco.modules.catalog.application.dto.product.UpdateProductDto;
import com.essenza.draco.modules.catalog.application.output.repository.ProductRepository;
import com.essenza.draco.modules.catalog.application.output.repository.ProductCatalogViewRepository;
import com.essenza.draco.modules.catalog.application.output.repository.UnitReferences;
import com.essenza.draco.modules.catalog.domain.command.ProductCommand;
import com.essenza.draco.shared.common.web.Slugs;
import com.essenza.draco.shared.common.inventory.StockQuery;
import com.essenza.draco.modules.catalog.application.dto.product.ProductSkuDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.shared.exceptions.NotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@Transactional
public class ProductServiceImpl implements CreateProductUseCase,
        UpdateProductUseCase,
        DeleteProductUseCase,
        FindProductByIdUseCase,
        FindProductsUseCase,
        FindProductsPageUseCase {

    private final ProductRepository productRepository;
    private final ProductAggregateRepository aggregateRepository;
    private final ProductDtoAssembler productDtoAssembler;
    private final ProductCommandAssembler productCommandAssembler;
    private final ProductFactory productFactory;
    private final ProductCatalogViewRepository catalogView;
    private final StockQuery stockQuery;
    private final ProductPublicationGuard publicationGuard;
    private final UnitReferences unitReferences;

    public ProductServiceImpl(ProductRepository productRepository,
                              ProductAggregateRepository aggregateRepository,
                              ProductDtoAssembler productDtoAssembler,
                              ProductCommandAssembler productCommandAssembler,
                              ProductFactory productFactory,
                              ProductCatalogViewRepository catalogView,
                              StockQuery stockQuery,
                              ProductPublicationGuard publicationGuard) {
        this(productRepository, aggregateRepository, productDtoAssembler, productCommandAssembler, productFactory,
                catalogView, stockQuery, publicationGuard, unitId -> true);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              ProductAggregateRepository aggregateRepository,
                              ProductDtoAssembler productDtoAssembler,
                              ProductCommandAssembler productCommandAssembler,
                              ProductFactory productFactory,
                              ProductCatalogViewRepository catalogView,
                              StockQuery stockQuery,
                              ProductPublicationGuard publicationGuard,
                              UnitReferences unitReferences) {
        this.unitReferences = unitReferences;
        this.productRepository = productRepository;
        this.aggregateRepository = aggregateRepository;
        this.productDtoAssembler = productDtoAssembler;
        this.productCommandAssembler = productCommandAssembler;
        this.productFactory = productFactory;
        this.catalogView = catalogView;
        this.stockQuery = stockQuery;
        this.publicationGuard = publicationGuard;
    }

    @Override
    public ProductDto create(@Valid CreateProductDto input) {
        checkNetContentUnit(input.getNetContentUnitId());
        var command = withUniqueSlug(productCommandAssembler.fromCreateDto(input), null);
        var product = productFactory.fromCommand(command);
        Long id = productRepository.save(product);
        return aggregateRepository.findById(id)
                .map(productDtoAssembler::toDto)
                .map(this::enrich)
                .orElseThrow(() -> new IllegalStateException("Product not found after creation: " + id));
    }

    @Override
    public ProductDto update(Long id, @Valid UpdateProductDto input) {
        var current = aggregateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado: " + id));
        checkNetContentUnit(input.getNetContentUnitId());
        var command = withUniqueSlug(productCommandAssembler.fromUpdateDto(id, input, current), id);
        var product = productFactory.fromCommand(command);
        if (product.getStatus() == ProductStatus.ACTIVE && current.getStatus() != ProductStatus.ACTIVE) {
            // Publicar exige la ficha técnica y los ejes de variante completos (Fase 4).
            publicationGuard.assertPublishable(id);
        }
        Long persistedId = productRepository.save(product);
        return aggregateRepository.findById(persistedId)
                .map(productDtoAssembler::toDto)
                .map(this::enrich)
                .orElseThrow(() -> new IllegalStateException("Product not found after update: " + persistedId));
    }

    @Override
    public boolean deleteById(Long id) {
        return productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDto> findById(Long id) {
        return aggregateRepository.findById(id).map(productDtoAssembler::toDto).map(this::enrich);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> findAll(Pageable pageable) {
        return hydratePage(productRepository.findAll(pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> findAllPage(Pageable pageable, ProductFilter filter) {
        return hydratePage(productRepository.findAll(filter, pageable));
    }

    /**
     * Completa la página con variantes y componentes en lote (3 consultas en
     * total) en lugar de releer cada fila (antes: ~3 consultas por producto).
     */
    private Page<ProductDto> hydratePage(Page<ProductDto> basePage) {
        List<Long> ids = basePage.getContent().stream().map(ProductDto::getId).toList();
        Map<Long, ProductDto> hydrated = aggregateRepository.findAllByIds(ids).stream()
                .map(productDtoAssembler::toDto)
                .collect(Collectors.toMap(ProductDto::getId, Function.identity(), (a, b) -> a));
        enrich(hydrated.values());
        return basePage.map(dto -> hydrated.getOrDefault(dto.getId(), dto));
    }

    private ProductDto enrich(ProductDto dto) {
        enrich(List.of(dto));
        return dto;
    }

    /** Añade SKUs e imágenes (dos consultas en lote para toda la página). */
    private void enrich(java.util.Collection<ProductDto> dtos) {
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
    private ProductCommand withUniqueSlug(ProductCommand command, Long excludeId) {
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

    private void checkNetContentUnit(Long unitId) {
        if (unitId != null && !unitReferences.exists(unitId)) {
            throw new IllegalArgumentException("La unidad del contenido neto no existe: " + unitId);
        }
    }
}
