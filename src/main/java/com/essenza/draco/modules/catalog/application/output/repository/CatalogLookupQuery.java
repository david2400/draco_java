package com.essenza.draco.modules.catalog.application.output.repository;

import java.util.List;

import com.essenza.draco.modules.catalog.application.dto.lookup.ProductLookupDto;
import com.essenza.draco.modules.catalog.application.dto.lookup.SkuLookupDto;
import com.essenza.draco.shared.common.lookup.LookupOption;
import com.essenza.draco.shared.common.lookup.LookupRequest;

/** Consultas ligeras del catálogo para selectores con búsqueda (solo lectura, sin borrados). */
public interface CatalogLookupQuery {

    List<ProductLookupDto> products(LookupRequest request, List<String> statuses);

    /** SKUs sin datos de stock (onHand/available a 0); el servicio los completa. */
    List<SkuLookupDto> skus(LookupRequest request, Long productId, boolean sellableOnly);

    List<LookupOption> brands(LookupRequest request);

    List<LookupOption> categories(LookupRequest request);

    List<LookupOption> subcategories(LookupRequest request, Long categoryId);
}
