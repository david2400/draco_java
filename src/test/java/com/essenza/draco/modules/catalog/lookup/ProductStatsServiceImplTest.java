package com.essenza.draco.modules.catalog.lookup;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import com.essenza.draco.modules.catalog.application.output.repository.ProductStatsQuery;
import com.essenza.draco.modules.catalog.application.services.ProductStatsServiceImpl;

class ProductStatsServiceImplTest {

    private final ProductStatsQuery query = mock(ProductStatsQuery.class);
    private final ProductStatsServiceImpl service = new ProductStatsServiceImpl(query);

    @Test
    void defaults() {
        service.stats(null, null);
        verify(query).stats(5, 8);
    }

    @Test
    void clampsRanges() {
        service.stats(-3, 999);
        verify(query).stats(0, 50);
        service.stats(5000, -1);
        verify(query).stats(1000, 0);
    }
}
