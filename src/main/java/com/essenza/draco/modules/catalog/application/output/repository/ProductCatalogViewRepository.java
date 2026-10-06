package com.essenza.draco.modules.catalog.application.output.repository;

import com.essenza.draco.modules.catalog.application.dto.product.ProductImageDto;
import com.essenza.draco.modules.catalog.application.dto.product.ProductSkuDto;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/** Lectura en lote de SKUs e imágenes para completar los DTOs de producto. */
public interface ProductCatalogViewRepository {

    Map<Long, List<ProductSkuDto>> findSkusByProductIds(Collection<Long> productIds);

    Map<Long, List<ProductImageDto>> findImagesByProductIds(Collection<Long> productIds);
}
