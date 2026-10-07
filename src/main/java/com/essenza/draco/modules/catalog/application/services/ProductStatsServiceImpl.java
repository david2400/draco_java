package com.essenza.draco.modules.catalog.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.catalog.application.dto.lookup.ProductStatsDto;
import com.essenza.draco.modules.catalog.application.input.lookup.GetProductStatsUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.ProductStatsQuery;

@Service
@Transactional(readOnly = true)
public class ProductStatsServiceImpl implements GetProductStatsUseCase {

    static final int DEFAULT_THRESHOLD = 5;
    static final int DEFAULT_LIMIT = 8;

    private final ProductStatsQuery query;

    public ProductStatsServiceImpl(ProductStatsQuery query) {
        this.query = query;
    }

    @Override
    public ProductStatsDto stats(Integer lowStockThreshold, Integer lowStockLimit) {
        int threshold = lowStockThreshold == null ? DEFAULT_THRESHOLD : Math.min(Math.max(lowStockThreshold, 0), 1000);
        int limit = lowStockLimit == null ? DEFAULT_LIMIT : Math.min(Math.max(lowStockLimit, 0), 50);
        return query.stats(threshold, limit);
    }
}
