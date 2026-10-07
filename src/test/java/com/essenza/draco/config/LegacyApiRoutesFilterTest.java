package com.essenza.draco.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LegacyApiRoutesFilterTest {

    @Test
    void rewritesLegacyPrefixesKeepingSubpaths() {
        assertThat(LegacyApiRoutesFilter.successorOf("/api/shop/inventory/products")).contains("/api/shop/catalog/products");
        assertThat(LegacyApiRoutesFilter.successorOf("/api/shop/inventory/products/12")).contains("/api/shop/catalog/products/12");
        assertThat(LegacyApiRoutesFilter.successorOf("/api/shop/inventory/products/search")).contains("/api/shop/catalog/products/search");
        assertThat(LegacyApiRoutesFilter.successorOf("/api/shop/inventory/product_children/bulk-delete"))
                .contains("/api/shop/catalog/variants/bulk-delete");
        assertThat(LegacyApiRoutesFilter.successorOf("/api/shop/inventory/product_combos/3")).contains("/api/shop/catalog/product_combos/3");
        assertThat(LegacyApiRoutesFilter.successorOf("/api/shop/inventory/inventory_movements/entry"))
                .contains("/api/shop/inventory/stock/movements/entry");
    }

    @Test
    void stockRootIsExactOnly() {
        assertThat(LegacyApiRoutesFilter.successorOf("/api/shop/inventory/stock")).contains("/api/shop/inventory/stock/levels");
        assertThat(LegacyApiRoutesFilter.successorOf("/api/shop/inventory/stock/levels")).isEmpty();
        assertThat(LegacyApiRoutesFilter.successorOf("/api/shop/inventory/stock/reservations")).isEmpty();
    }

    @Test
    void ignoresNewAndSimilarRoutes() {
        assertThat(LegacyApiRoutesFilter.successorOf("/api/shop/catalog/products")).isEmpty();
        assertThat(LegacyApiRoutesFilter.successorOf("/api/shop/inventory/productsX")).isEmpty();
        assertThat(LegacyApiRoutesFilter.successorOf("/api/shop/inventory/warehouses")).isEmpty();
    }
}
