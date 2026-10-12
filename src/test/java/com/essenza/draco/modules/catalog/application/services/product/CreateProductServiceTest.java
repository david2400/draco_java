package com.essenza.draco.modules.catalog.application.services.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.FACTORY;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.panelUpdate;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.simple;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.essenza.draco.modules.catalog.domain.model.Product;

class CreateProductServiceTest extends ProductServiceTestSupport {

    private CreateProductService service;

    @BeforeEach
    void setUp() {
        service = new CreateProductService(productRepository, aggregateRepository, productDtoAssembler,
                productCommandAssembler, FACTORY, soporte());
    }

    @Test
    void elSlugSeGeneraDelNombreYSeHaceUnico() {
        when(productRepository.existsBySlug("perfume-arabe", null)).thenReturn(true);
        when(productRepository.existsBySlug("perfume-arabe-2", null)).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(1L);
        when(aggregateRepository.findById(1L)).thenReturn(Optional.of(simple(1L, 1, true)));

        service.create(panelUpdate("Perfume Árabe"));

        ArgumentCaptor<Product> saved = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(saved.capture());
        assertThat(saved.getValue().getSlug()).isEqualTo("perfume-arabe-2");
    }
}
