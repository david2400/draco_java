package com.essenza.draco.modules.catalog.application.services.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.simple;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.essenza.draco.modules.catalog.application.dto.product.ProductDto;

class FindProductByIdServiceTest extends ProductServiceTestSupport {

    private FindProductByIdService service;

    @BeforeEach
    void setUp() {
        service = new FindProductByIdService(aggregateRepository, productDtoAssembler, soporte());
    }

    @Test
    void elDtoExponeElEstadoComercialYLaDisponibilidadPorSeparado() {
        when(aggregateRepository.findById(5L)).thenReturn(Optional.of(simple(5L, 0, true)));

        ProductDto dto = service.findById(5L).orElseThrow();

        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getSellable()).isFalse();
    }

    @Test
    void lasRespuestasIncluyenSkusEImagenes() {
        when(aggregateRepository.findById(5L)).thenReturn(Optional.of(simple(5L, 3, true)));
        when(catalogView.findSkusByProductIds(List.of(5L))).thenReturn(java.util.Map.of(5L, List.of(
                com.essenza.draco.modules.catalog.application.dto.product.ProductSkuDto.builder().id(9L).code("SKU-000005").build())));

        ProductDto dto = service.findById(5L).orElseThrow();

        assertThat(dto.getSkus()).extracting("code").containsExactly("SKU-000005");
        assertThat(dto.getImages()).isEmpty();
        assertThat(dto.getStatus()).isEqualTo("ACTIVE");
        assertThat(dto.getProductType()).isEqualTo("SIMPLE");
    }

    @Test
    void elStockMostradoSaleDelInventarioPorSku() {
        when(aggregateRepository.findById(5L)).thenReturn(Optional.of(simple(5L, 99, true)));
        when(catalogView.findSkusByProductIds(List.of(5L))).thenReturn(java.util.Map.of(5L, List.of(
                com.essenza.draco.modules.catalog.application.dto.product.ProductSkuDto.builder().id(9L).code("SKU-000005").build())));
        when(stockQuery.onHandBySku(List.of(9L))).thenReturn(java.util.Map.of());

        ProductDto dto = service.findById(5L).orElseThrow();

        assertThat(dto.getStock()).isZero();          // el valor heredado (99) ya no manda
        assertThat(dto.getSellable()).isFalse();
        assertThat(dto.getSkus().get(0).getStock()).isZero();
    }
}
