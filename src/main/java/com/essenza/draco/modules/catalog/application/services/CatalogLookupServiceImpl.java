package com.essenza.draco.modules.catalog.application.services;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.catalog.application.dto.lookup.ProductLookupDto;
import com.essenza.draco.modules.catalog.application.dto.lookup.SkuLookupDto;
import com.essenza.draco.modules.catalog.application.input.lookup.CatalogLookupUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.CatalogLookupQuery;
import com.essenza.draco.shared.common.inventory.StockQuery;
import com.essenza.draco.shared.common.lookup.LookupOption;
import com.essenza.draco.shared.common.lookup.LookupRequest;

@Service
@Transactional(readOnly = true)
public class CatalogLookupServiceImpl implements CatalogLookupUseCase {

    private static final Set<String> STATUSES = Set.of("DRAFT", "ACTIVE", "INACTIVE", "ARCHIVED");

    private final CatalogLookupQuery query;
    private final StockQuery stock;

    public CatalogLookupServiceImpl(CatalogLookupQuery query, StockQuery stock) {
        this.query = query;
        this.stock = stock;
    }

    @Override
    public List<ProductLookupDto> products(LookupRequest request, List<String> statuses) {
        List<String> valid = statuses == null ? List.of()
                : statuses.stream().map(String::trim).map(String::toUpperCase).filter(STATUSES::contains).distinct().toList();
        return query.products(request, valid);
    }

    @Override
    public List<SkuLookupDto> skus(LookupRequest request, Long productId, boolean sellableOnly) {
        List<SkuLookupDto> found = query.skus(request, productId, sellableOnly);
        if (found.isEmpty()) {
            return found;
        }
        List<Long> ids = found.stream().map(SkuLookupDto::skuId).toList();
        Map<Long, Integer> onHand = stock.onHandBySku(ids);
        Map<Long, Integer> available = stock.availableBySku(ids);
        return found.stream()
                .map(s -> s.withStock(onHand.getOrDefault(s.skuId(), 0), available.getOrDefault(s.skuId(), 0)))
                .toList();
    }

    @Override
    public List<LookupOption> brands(LookupRequest request) {
        return query.brands(request);
    }

    @Override
    public List<LookupOption> categories(LookupRequest request) {
        return query.categories(request);
    }

    @Override
    public List<LookupOption> subcategories(LookupRequest request, Long categoryId) {
        return query.subcategories(request, categoryId);
    }
}
