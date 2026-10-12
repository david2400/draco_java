package com.essenza.draco.modules.catalog.application.services.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.simple;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.essenza.draco.modules.catalog.application.dto.product.ProductDto;

class FindProductsServiceTest extends ProductServiceTestSupport {

    private FindProductsService service;

    @BeforeEach
    void setUp() {
        service = new FindProductsService(productRepository, soporte());
    }

    @Test
    void elListadoHidrataLaPaginaEnUnaSolaConsultaEnLote() {
        Pageable pageable = PageRequest.of(0, 2);
        ProductDto a = ProductDto.builder().id(1L).name("A").build();
        ProductDto b = ProductDto.builder().id(2L).name("B").build();
        when(productRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(a, b), pageable, 2));
        when(aggregateRepository.findAllByIds(List.of(1L, 2L)))
                .thenReturn(List.of(simple(2L, 3, true), simple(1L, 0, true)));

        List<ProductDto> content = service.findAll(pageable).getContent();

        assertThat(content).extracting(ProductDto::getId).containsExactly(1L, 2L);
        verify(aggregateRepository, times(1)).findAllByIds(List.of(1L, 2L));
        verify(aggregateRepository, never()).findById(anyLong());
    }
}
