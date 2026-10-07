package com.essenza.draco.modules.catalog.application.input.lookup;

import java.util.List;

import com.essenza.draco.modules.catalog.application.dto.lookup.ProductLookupDto;
import com.essenza.draco.modules.catalog.application.dto.lookup.SkuLookupDto;
import com.essenza.draco.shared.common.lookup.LookupOption;
import com.essenza.draco.shared.common.lookup.LookupRequest;

/** Búsquedas ligeras para los selectores asíncronos del panel. */
public interface CatalogLookupUseCase {

    List<ProductLookupDto> products(LookupRequest request, List<String> statuses);

    List<SkuLookupDto> skus(LookupRequest request, Long productId, boolean sellableOnly);

    List<LookupOption> brands(LookupRequest request);

    List<LookupOption> categories(LookupRequest request);

    List<LookupOption> subcategories(LookupRequest request, Long categoryId);
}
