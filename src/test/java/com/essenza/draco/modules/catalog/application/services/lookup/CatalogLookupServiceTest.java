package com.essenza.draco.modules.catalog.application.services.lookup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.essenza.draco.modules.catalog.application.dto.lookup.SkuLookupDto;
import com.essenza.draco.modules.catalog.application.output.repository.CatalogLookupQuery;
import com.essenza.draco.shared.common.inventory.StockQuery;
import com.essenza.draco.shared.common.lookup.LookupRequest;

class CatalogLookupServiceTest {

    private final CatalogLookupQuery query = mock(CatalogLookupQuery.class);
    private final StockQuery stock = mock(StockQuery.class);
    private final CatalogLookupService lookup = new CatalogLookupService(query, stock);

    private static SkuLookupDto sku(long id) {
        return new SkuLookupDto(id, 1L, "SKU-" + id, "P", "P", null, "SIMPLE", BigDecimal.TEN, true, true, 0, 0, null);
    }

    @Test
    void skusAreEnrichedWithStock() {
        LookupRequest req = LookupRequest.of("p", null, 10);
        when(query.skus(req, null, true)).thenReturn(List.of(sku(1), sku(2)));
        when(stock.onHandBySku(List.of(1L, 2L), null)).thenReturn(Map.of(1L, 9));
        when(stock.availableBySku(List.of(1L, 2L), null)).thenReturn(Map.of(1L, 4));

        List<SkuLookupDto> result = lookup.skus(req, null, true);

        assertThat(result).extracting(SkuLookupDto::onHand).containsExactly(9, 0);
        assertThat(result).extracting(SkuLookupDto::available).containsExactly(4, 0);
    }

    @Test
    void emptySkuResultSkipsStockQuery() {
        LookupRequest req = LookupRequest.of("zz", null, 10);
        when(query.skus(req, null, false)).thenReturn(List.of());
        assertThat(lookup.skus(req, null, false)).isEmpty();
        verify(stock, never()).onHandBySku(any(), any());
    }

    @Test
    void unknownStatusesAreDropped() {
        LookupRequest req = LookupRequest.of(null, null, 10);
        lookup.products(req, List.of(" active", "bogus", "DRAFT", "ACTIVE"));
        verify(query).products(req, List.of("ACTIVE", "DRAFT"));
    }
}
