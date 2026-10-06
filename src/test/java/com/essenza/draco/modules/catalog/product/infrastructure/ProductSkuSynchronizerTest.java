package com.essenza.draco.modules.catalog.product.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductChildEntity;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductEntity;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductImageEntity;
import com.essenza.draco.modules.catalog.infrastructure.outbound.persistence.mysql.shop.ProductSkuEntity;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.product_child.JpaProductChildRepository;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.products.JpaProductRepository;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.sku.JpaProductImageRepository;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.sku.JpaProductSkuRepository;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.sku.ProductSkuSynchronizer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Capa de compatibilidad: product_skus / product_images siguen al modelo heredado. */
@ExtendWith(MockitoExtension.class)
class ProductSkuSynchronizerTest {

    @Mock JpaProductRepository products;
    @Mock JpaProductChildRepository children;
    @Mock JpaProductSkuRepository skus;
    @Mock JpaProductImageRepository images;

    private ProductSkuSynchronizer synchronizer;
    private final List<ProductSkuEntity> skuTable = new ArrayList<>();
    private final List<ProductImageEntity> imageTable = new ArrayList<>();
    private final AtomicLong ids = new AtomicLong(100);

    @BeforeEach
    void setUp() {
        synchronizer = new ProductSkuSynchronizer(products, children, skus, images);
        lenient().when(skus.findByProductId(any())).thenAnswer(inv -> skuTable.stream()
                .filter(s -> s.getProductId().equals(inv.getArgument(0))).toList());
        lenient().when(skus.save(any())).thenAnswer(inv -> {
            ProductSkuEntity s = inv.getArgument(0);
            if (s.getId() == null) { s.setId(ids.incrementAndGet()); skuTable.add(s); }
            return s;
        });
        lenient().doAnswer(inv -> skuTable.remove((ProductSkuEntity) inv.getArgument(0))).when(skus).delete(any());
        lenient().when(images.findByProductId(any())).thenAnswer(inv -> imageTable.stream()
                .filter(i -> i.getProductId().equals(inv.getArgument(0))).toList());
        lenient().when(images.save(any())).thenAnswer(inv -> {
            ProductImageEntity i = inv.getArgument(0);
            if (i.getId() == null) { i.setId(ids.incrementAndGet()); imageTable.add(i); }
            return i;
        });
        lenient().doAnswer(inv -> imageTable.remove((ProductImageEntity) inv.getArgument(0))).when(images).delete(any());
    }

    private ProductEntity product(Long id, String image) {
        ProductEntity p = ProductEntity.builder().id(id).name("Perfume Floral").description("EDP")
                .unitPrice(new BigDecimal("120000")).realPrice(new BigDecimal("80000"))
                .weight(0.4).available(true).isCombo(false).imageUrl(image).productType("SIMPLE").status("ACTIVE").build();
        when(products.findById(id)).thenReturn(Optional.of(p));
        return p;
    }

    private ProductChildEntity child(Long id, Long productId, String name, String price, String image) {
        return ProductChildEntity.builder().id(id).productId(productId).name(name)
                .unitPrice(new BigDecimal(price)).available(true).stock(1).imageUrl(image).build();
    }

    @Test
    void productoSimpleTieneUnSkuPorDefectoConPrecioYCosto() {
        product(7L, "https://img/p.jpg");
        when(children.findByProductId(7L)).thenReturn(List.of());

        synchronizer.sync(7L);
        synchronizer.sync(7L); // idempotente

        assertThat(skuTable).singleElement().satisfies(s -> {
            assertThat(s.getCode()).isEqualTo("SKU-000007");
            assertThat(s.getIsDefault()).isTrue();
            assertThat(s.getPrice()).isEqualByComparingTo("120000");
            assertThat(s.getCostPrice()).isEqualByComparingTo("80000");
            assertThat(s.getLegacyChildId()).isNull();
        });
        assertThat(imageTable).singleElement().satisfies(i -> {
            assertThat(i.getPosition()).isZero();
            assertThat(i.getUrl()).isEqualTo("https://img/p.jpg");
        });
    }

    @Test
    void alAgregarVariantesSeCreaUnSkuPorVarianteYSeRetiraElPorDefecto() {
        ProductEntity p = product(7L, null);
        when(children.findByProductId(7L)).thenReturn(List.of());
        synchronizer.sync(7L);

        when(children.findByProductId(7L)).thenReturn(List.of(
                child(12L, 7L, "100 ml", "150000", "https://img/100.jpg"),
                child(11L, 7L, "50 ml", "99000", null)));
        synchronizer.sync(7L);

        assertThat(skuTable).extracting(ProductSkuEntity::getCode, ProductSkuEntity::getIsDefault, ProductSkuEntity::getActive)
                .containsExactlyInAnyOrder(
                        org.assertj.core.groups.Tuple.tuple("SKU-000007", false, false),
                        org.assertj.core.groups.Tuple.tuple("SKU-000007-11", true, true),
                        org.assertj.core.groups.Tuple.tuple("SKU-000007-12", false, true));
        assertThat(p.getProductType()).isEqualTo("VARIANT");
        assertThat(imageTable).singleElement().satisfies(i -> assertThat(i.getSkuId()).isNotNull());
    }

    @Test
    void alQuitarTodasLasVariantesSeReutilizaElSkuPorDefecto() {
        product(7L, null);
        when(children.findByProductId(7L)).thenReturn(List.of());
        synchronizer.sync(7L);
        Long defaultId = skuTable.get(0).getId();
        when(children.findByProductId(7L)).thenReturn(List.of(child(11L, 7L, "50 ml", "99000", null)));
        synchronizer.sync(7L);
        when(children.findByProductId(7L)).thenReturn(List.of());

        synchronizer.sync(7L);

        assertThat(skuTable).singleElement().satisfies(s -> {
            assertThat(s.getId()).isEqualTo(defaultId);
            assertThat(s.getIsDefault()).isTrue();
            assertThat(s.getActive()).isTrue();
        });
    }

    @Test
    void codigoOcupadoPorUnaFilaBorradaRecibeSufijo() {
        product(7L, null);
        when(children.findByProductId(7L)).thenReturn(List.of());
        when(skus.countByCodeIncludingDeleted("SKU-000007")).thenReturn(1L);

        synchronizer.sync(7L);

        assertThat(skuTable).singleElement().satisfies(s -> assertThat(s.getCode()).isEqualTo("SKU-000007-R2"));
    }

    @Test
    void varianteEliminadaRetiraSuSku() {
        product(7L, null);
        when(children.findByProductId(7L)).thenReturn(List.of(child(11L, 7L, "50 ml", "99000", null), child(12L, 7L, "100 ml", "150000", null)));
        synchronizer.sync(7L);
        when(children.findByProductId(7L)).thenReturn(List.of(child(12L, 7L, "100 ml", "150000", null)));

        synchronizer.sync(7L);

        assertThat(skuTable).filteredOn(s -> s.getLegacyChildId() != null).singleElement().satisfies(s -> {
            assertThat(s.getLegacyChildId()).isEqualTo(12L);
            assertThat(s.getIsDefault()).isTrue();
        });
    }

    @Test
    void productoInexistenteBorraSusSkus() {
        when(products.findById(9L)).thenReturn(Optional.empty());
        synchronizer.sync(9L);
        verify(products, never()).save(any());
    }
}
