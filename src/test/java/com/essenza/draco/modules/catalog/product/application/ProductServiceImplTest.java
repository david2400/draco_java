package com.essenza.draco.modules.catalog.product.application;

import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.FACTORY;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.panelUpdate;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.simple;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.variant;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.withVariants;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.essenza.draco.modules.catalog.application.output.repository.ProductAggregateRepository;
import com.essenza.draco.modules.catalog.application.output.repository.ProductCatalogViewRepository;
import com.essenza.draco.shared.common.inventory.StockQuery;
import com.essenza.draco.modules.catalog.application.services.ProductCommandAssembler;
import com.essenza.draco.modules.catalog.application.services.ProductDtoAssembler;
import com.essenza.draco.modules.catalog.application.services.ProductServiceImpl;
import com.essenza.draco.modules.catalog.application.dto.product.ProductDto;
import com.essenza.draco.modules.catalog.domain.model.Product;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.products.ProductRepositoryAdapter;
import com.essenza.draco.shared.exceptions.NotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepositoryAdapter productRepository;

    @Mock
    private ProductAggregateRepository aggregateRepository;

    @Mock
    private ProductCatalogViewRepository catalogView;

    @Mock
    private StockQuery stockQuery;

    @Mock
    private com.essenza.draco.modules.catalog.application.input.attribute.ProductPublicationGuard publicationGuard;

    private ProductServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ProductServiceImpl(productRepository, aggregateRepository,
                new ProductDtoAssembler(), new ProductCommandAssembler(), FACTORY, catalogView, stockQuery, publicationGuard);
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
    void elDtoExponeElEstadoComercialYLaDisponibilidadPorSeparado() {
        when(aggregateRepository.findById(5L)).thenReturn(Optional.of(simple(5L, 0, true)));

        ProductDto dto = service.findById(5L).orElseThrow();

        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getSellable()).isFalse();
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
