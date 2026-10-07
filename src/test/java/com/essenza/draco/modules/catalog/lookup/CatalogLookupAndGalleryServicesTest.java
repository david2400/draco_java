package com.essenza.draco.modules.catalog.lookup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.essenza.draco.modules.catalog.application.dto.lookup.SkuLookupDto;
import com.essenza.draco.modules.catalog.application.dto.product.ProductImageInputDto;
import com.essenza.draco.modules.catalog.application.dto.product.ReplaceProductImagesDto;
import com.essenza.draco.modules.catalog.application.output.repository.CatalogLookupQuery;
import com.essenza.draco.modules.catalog.application.output.repository.ProductGalleryRepository;
import com.essenza.draco.modules.catalog.application.services.CatalogLookupServiceImpl;
import com.essenza.draco.modules.catalog.application.services.ProductGalleryServiceImpl;
import com.essenza.draco.shared.common.inventory.StockQuery;
import com.essenza.draco.shared.common.lookup.LookupRequest;
import com.essenza.draco.shared.exceptions.NotFoundException;

class CatalogLookupAndGalleryServicesTest {

    private final CatalogLookupQuery query = mock(CatalogLookupQuery.class);
    private final StockQuery stock = mock(StockQuery.class);
    private final CatalogLookupServiceImpl lookup = new CatalogLookupServiceImpl(query, stock);

    private static SkuLookupDto sku(long id) {
        return new SkuLookupDto(id, 1L, "SKU-" + id, "P", "P", null, "SIMPLE", BigDecimal.TEN, true, true, 0, 0, null);
    }

    @Test
    void skusAreEnrichedWithStock() {
        LookupRequest req = LookupRequest.of("p", null, 10);
        when(query.skus(req, null, true)).thenReturn(List.of(sku(1), sku(2)));
        when(stock.onHandBySku(List.of(1L, 2L))).thenReturn(Map.of(1L, 9));
        when(stock.availableBySku(List.of(1L, 2L))).thenReturn(Map.of(1L, 4));

        List<SkuLookupDto> result = lookup.skus(req, null, true);

        assertThat(result).extracting(SkuLookupDto::onHand).containsExactly(9, 0);
        assertThat(result).extracting(SkuLookupDto::available).containsExactly(4, 0);
    }

    @Test
    void emptySkuResultSkipsStockQuery() {
        LookupRequest req = LookupRequest.of("zz", null, 10);
        when(query.skus(req, null, false)).thenReturn(List.of());
        assertThat(lookup.skus(req, null, false)).isEmpty();
        verify(stock, never()).onHandBySku(any());
    }

    @Test
    void unknownStatusesAreDropped() {
        LookupRequest req = LookupRequest.of(null, null, 10);
        lookup.products(req, List.of(" active", "bogus", "DRAFT", "ACTIVE"));
        verify(query).products(req, List.of("ACTIVE", "DRAFT"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void galleryDeduplicatesAndTrims() {
        ProductGalleryRepository repo = mock(ProductGalleryRepository.class);
        when(repo.productExists(5L)).thenReturn(true);
        ProductGalleryServiceImpl service = new ProductGalleryServiceImpl(repo);

        service.replace(5L, new ReplaceProductImagesDto(List.of(
                new ProductImageInputDto(" https://a/1.jpg ", " Frente "),
                new ProductImageInputDto("https://a/2.jpg", ""),
                new ProductImageInputDto("https://a/1.jpg", "dup"))));

        ArgumentCaptor<List<ProductImageInputDto>> saved = ArgumentCaptor.forClass(List.class);
        verify(repo).replaceGallery(eq(5L), saved.capture());
        assertThat(saved.getValue()).containsExactly(
                new ProductImageInputDto("https://a/1.jpg", "Frente"),
                new ProductImageInputDto("https://a/2.jpg", null));
    }

    @Test
    void galleryOfMissingProductIs404() {
        ProductGalleryRepository repo = mock(ProductGalleryRepository.class);
        ProductGalleryServiceImpl service = new ProductGalleryServiceImpl(repo);
        assertThatThrownBy(() -> service.list(99L)).isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> service.replace(99L, new ReplaceProductImagesDto(List.of())))
                .isInstanceOf(NotFoundException.class);
        verify(repo, never()).replaceGallery(any(), anyList());
    }
}
