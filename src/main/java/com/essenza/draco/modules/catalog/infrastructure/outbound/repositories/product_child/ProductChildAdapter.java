package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.product_child;

import com.essenza.draco.modules.catalog.application.output.repository.ProductChildRepository;
import com.essenza.draco.modules.catalog.application.dto.product_child.CreateProductChildDto;
import com.essenza.draco.modules.catalog.application.dto.product_child.ProductChildDto;
import com.essenza.draco.modules.catalog.application.dto.product_child.UpdateProductChildDto;
import com.essenza.draco.modules.catalog.infrastructure.outbound.mappers.ProductChildMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.sku.ProductSkuSynchronizer;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.sku.SkuSyncResult;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductChildEntity;
import com.essenza.draco.shared.common.inventory.StockLevelRequested;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductChildAdapter implements ProductChildRepository {
    private final JpaProductChildRepository jpa;
    private final ProductChildMapper mapper;
    private final ProductSkuSynchronizer skuSynchronizer;
    private final ApplicationEventPublisher events;

    public ProductChildAdapter(JpaProductChildRepository jpa, ProductChildMapper mapper,
                               ProductSkuSynchronizer skuSynchronizer, ApplicationEventPublisher events) {
        this.jpa = jpa;
        this.mapper = mapper;
        this.skuSynchronizer = skuSynchronizer;
        this.events = events;
    }

    /** El stock de la variante lo gobierna el inventario: se pide el ajuste de su SKU. */
    private void requestStock(ProductChildEntity child, SkuSyncResult skus) {
        Long skuId = skus.skuByChildId().get(child.getId());
        if (skuId != null && child.getStock() != null) {
            events.publishEvent(new StockLevelRequested(child.getProductId(), skuId, child.getStock(), "VARIANT_EDIT"));
        }
    }

    @Override
    @Transactional
    public ProductChildDto create(CreateProductChildDto input) {
        var entity = mapper.toEntity(input);
        var saved = jpa.save(entity);
        requestStock(saved, skuSynchronizer.sync(saved.getProductId()));
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProductChildDto update(Long id, UpdateProductChildDto input) {
        var entity = jpa.findById(id)
                .orElseThrow(() -> new com.essenza.draco.shared.exceptions.NotFoundException("Variante de producto no encontrada: " + id));
        mapper.updateEntityFromDto(input, entity);
        // El mapper ignora nulos: el contenido neto se reemplaza siempre (vacío = quitarlo).
        entity.setNetContent(input.getNetContent());
        entity.setNetContentUnitId(input.getNetContentUnitId());
        var updated = jpa.save(entity);
        requestStock(updated, skuSynchronizer.sync(updated.getProductId()));
        return mapper.toDto(updated);
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        var entity = jpa.findById(id);
        if (entity.isEmpty()) return false;
        jpa.deleteById(id);
        skuSynchronizer.sync(entity.get().getProductId());
        return true;
    }

    @Override
    public Optional<ProductChildDto> findById(Long id) {
        return jpa.findById(id).map(mapper::toDto);
    }

    @Override
    public List<ProductChildDto> findAll() {
        return jpa.findAll().stream().map(mapper::toDto).toList();
    }

}
