package com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.sku;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.essenza.draco.modules.catalog.application.dto.product.ProductImageDto;
import com.essenza.draco.modules.catalog.application.dto.product.ProductImageInputDto;
import com.essenza.draco.modules.catalog.application.output.repository.ProductGalleryRepository;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductEntity;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductImageEntity;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.products.JpaProductRepository;

@Repository
public class ProductGalleryRepositoryAdapter implements ProductGalleryRepository {

    private final JpaProductRepository products;
    private final JpaProductImageRepository images;
    private final ProductSkuSynchronizer synchronizer;

    public ProductGalleryRepositoryAdapter(JpaProductRepository products, JpaProductImageRepository images,
                                           ProductSkuSynchronizer synchronizer) {
        this.products = products;
        this.images = images;
        this.synchronizer = synchronizer;
    }

    @Override
    public boolean productExists(Long productId) {
        return productId != null && products.existsById(productId);
    }

    @Override
    public List<ProductImageDto> images(Long productId) {
        return images.findByProductId(productId).stream()
                .sorted(Comparator.comparing(ProductImageEntity::getPosition).thenComparing(ProductImageEntity::getId))
                .map(i -> ProductImageDto.builder().id(i.getId()).url(i.getUrl()).position(i.getPosition())
                        .altText(i.getAltText()).skuId(i.getSkuId()).build())
                .toList();
    }

    @Override
    public void replaceGallery(Long productId, List<ProductImageInputDto> gallery) {
        images.deleteAll(images.findByProductId(productId).stream().filter(i -> i.getSkuId() == null).toList());
        images.flush();
        int position = 0;
        for (ProductImageInputDto input : gallery) {
            images.save(ProductImageEntity.builder()
                    .productId(productId)
                    .url(input.url())
                    .altText(input.altText())
                    .position(position++)
                    .build());
        }
        ProductEntity product = products.findById(productId).orElseThrow();
        product.setImageUrl(gallery.isEmpty() ? null : gallery.get(0).url());
        products.save(product);
        // Alinea el SKU por defecto (image_url) y la posición de las imágenes de variantes.
        synchronizer.sync(productId);
    }
}
