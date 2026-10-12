package com.essenza.draco.modules.catalog.application.services.attribute;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.essenza.draco.modules.catalog.application.output.repository.ProductAttributesStore.ProductHeader;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAttributesStore.VariantSku;
import com.essenza.draco.shared.exceptions.ConflictException;

class ProductPublicationGuardServiceTest extends AttributeServicesTestSupport {

    ProductPublicationGuardService productAttributes;

    @BeforeEach
    void setUp() {
        productAttributes = new ProductPublicationGuardService(store, soporte);
    }

    @Test
    void noSePublicaConVariantesIncompletas() {
        when(store.findProduct(5L)).thenReturn(Optional.of(new ProductHeader(5L, "DRAFT", 10L)));
        when(store.variantSkus(5L)).thenReturn(List.of(new VariantSku(100L, "SKU-1", "Rojo", true)));
        when(store.values(5L)).thenReturn(List.of());
        when(store.skuOptions(anyCollection())).thenReturn(Map.of());
        assertThatThrownBy(() -> productAttributes.assertPublishable(5L))
                .isInstanceOf(ConflictException.class).hasMessageContaining("variante");

        when(store.skuOptions(anyCollection())).thenReturn(Map.of(100L, Map.of(3L, 31L)));
        productAttributes.assertPublishable(5L);
    }
}
