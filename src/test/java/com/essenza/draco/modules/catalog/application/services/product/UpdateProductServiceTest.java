package com.essenza.draco.modules.catalog.application.services.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.FACTORY;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.panelUpdate;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.simple;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.variant;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.withVariants;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.essenza.draco.modules.catalog.domain.model.Product;
import com.essenza.draco.shared.exceptions.NotFoundException;

class UpdateProductServiceTest extends ProductServiceTestSupport {

    private UpdateProductService service;

    @BeforeEach
    void setUp() {
        service = new UpdateProductService(productRepository, aggregateRepository, productDtoAssembler,
                productCommandAssembler, FACTORY, publicationGuard, soporte());
    }

    @Test
    void actualizaElProductoDeLaUrlYConservaSusVariantes() {
        Product current = withVariants(42L, variant(10L, "30 ml", 2, true), variant(11L, "50 ml", 5, true));
        when(aggregateRepository.findById(42L)).thenReturn(Optional.of(current));
        when(productRepository.save(any(Product.class))).thenReturn(42L);

        service.update(42L, panelUpdate("Nuevo nombre"));

        ArgumentCaptor<Product> saved = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(saved.capture());
        assertThat(saved.getValue().getId().getValue()).isEqualTo(42L);
        assertThat(saved.getValue().getName()).isEqualTo("Nuevo nombre");
        assertThat(saved.getValue().getVariants()).extracting("name").containsExactly("30 ml", "50 ml");
    }

    @Test
    void actualizarUnProductoInexistenteDevuelveNotFoundYNoGuarda() {
        when(aggregateRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(7L, panelUpdate("X"))).isInstanceOf(NotFoundException.class);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void publicarConsultaLaFichaTecnica() {
        Product inactive = simple(42L, 1, false);
        when(aggregateRepository.findById(42L)).thenReturn(Optional.of(inactive));
        org.mockito.Mockito.doThrow(new com.essenza.draco.shared.exceptions.ConflictException("falta Material"))
                .when(publicationGuard).assertPublishable(42L);

        assertThatThrownBy(() -> service.update(42L, panelUpdate("Activo"))).hasMessageContaining("Material");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void editarUnProductoYaActivoNoVuelveAValidarLaFicha() {
        when(aggregateRepository.findById(42L)).thenReturn(Optional.of(simple(42L, 1, true)));
        when(productRepository.save(any(Product.class))).thenReturn(42L);

        service.update(42L, panelUpdate("Sigue activo"));

        verify(publicationGuard, never()).assertPublishable(anyLong());
    }
}
