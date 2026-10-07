package com.essenza.draco.modules.catalog.infrastructure.inbound.rest;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.essenza.draco.modules.catalog.application.dto.lookup.ProductLookupDto;
import com.essenza.draco.modules.catalog.application.dto.lookup.ProductStatsDto;
import com.essenza.draco.modules.catalog.application.dto.lookup.SkuLookupDto;
import com.essenza.draco.modules.catalog.application.input.lookup.CatalogLookupUseCase;
import com.essenza.draco.modules.catalog.application.input.lookup.GetProductStatsUseCase;
import com.essenza.draco.shared.common.lookup.LookupOption;
import com.essenza.draco.shared.common.lookup.LookupRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Búsquedas ligeras para selectores asíncronos (Fase 6). Todas aceptan {@code q} (texto),
 * {@code ids} (ids separados por coma, para pintar el valor elegido; ignoran {@code q})
 * y {@code limit} (1–50, por defecto 20).
 */
@RestController
@Tag(name = "Catalog lookups")
public class CatalogLookupController {

    private final CatalogLookupUseCase lookup;
    private final GetProductStatsUseCase productStats;

    public CatalogLookupController(CatalogLookupUseCase lookup, GetProductStatsUseCase productStats) {
        this.lookup = lookup;
        this.productStats = productStats;
    }

    @Operation(summary = "Indicadores del catálogo",
            description = "Total, publicados, sin stock, stock bajo (≤ low_stock_threshold, 5) y valor del inventario a costo; "
                    + "low_stock_limit (8, máx. 50) productos con menos stock")
    @GetMapping("/catalog/products/stats")
    public ProductStatsDto productStats(@RequestParam(value = "low_stock_threshold", required = false) Integer threshold,
                                        @RequestParam(value = "low_stock_limit", required = false) Integer limit) {
        return productStats.stats(threshold, limit);
    }

    @Operation(summary = "Buscar productos", description = "Por nombre o slug; statuses=ACTIVE,DRAFT filtra por estado")
    @GetMapping("/catalog/products/lookup")
    public List<ProductLookupDto> products(@RequestParam(value = "q", required = false) String q,
                                           @RequestParam(value = "ids", required = false) String ids,
                                           @RequestParam(value = "limit", required = false) Integer limit,
                                           @RequestParam(value = "statuses", required = false) String statuses) {
        List<String> parsed = statuses == null || statuses.isBlank() ? List.of() : Arrays.asList(statuses.split(","));
        return lookup.products(LookupRequest.of(q, ids, limit), parsed);
    }

    @Operation(summary = "Buscar SKUs", description = "Por código, código de barras, producto o variante, con precio y stock")
    @GetMapping("/catalog/skus/lookup")
    public List<SkuLookupDto> skus(@RequestParam(value = "q", required = false) String q,
                                   @RequestParam(value = "ids", required = false) String ids,
                                   @RequestParam(value = "limit", required = false) Integer limit,
                                   @RequestParam(value = "product_id", required = false) Long productId,
                                   @Parameter(description = "Solo SKUs activos de productos publicados")
                                   @RequestParam(value = "sellable_only", required = false, defaultValue = "false") boolean sellableOnly) {
        return lookup.skus(LookupRequest.of(q, ids, limit), productId, sellableOnly);
    }

    @Operation(summary = "Buscar marcas")
    @GetMapping("/catalog/brands/lookup")
    public List<LookupOption> brands(@RequestParam(value = "q", required = false) String q,
                                     @RequestParam(value = "ids", required = false) String ids,
                                     @RequestParam(value = "limit", required = false) Integer limit) {
        return lookup.brands(LookupRequest.of(q, ids, limit));
    }

    @Operation(summary = "Buscar categorías")
    @GetMapping("/catalog/categories/lookup")
    public List<LookupOption> categories(@RequestParam(value = "q", required = false) String q,
                                         @RequestParam(value = "ids", required = false) String ids,
                                         @RequestParam(value = "limit", required = false) Integer limit) {
        return lookup.categories(LookupRequest.of(q, ids, limit));
    }

    @Operation(summary = "Buscar subcategorías", description = "category_id limita a una categoría; hint = categoría")
    @GetMapping("/catalog/subcategories/lookup")
    public List<LookupOption> subcategories(@RequestParam(value = "q", required = false) String q,
                                            @RequestParam(value = "ids", required = false) String ids,
                                            @RequestParam(value = "limit", required = false) Integer limit,
                                            @RequestParam(value = "category_id", required = false) Long categoryId) {
        return lookup.subcategories(LookupRequest.of(q, ids, limit), categoryId);
    }
}
