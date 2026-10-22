package com.essenza.draco.modules.catalog.application.services.product;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.essenza.draco.modules.catalog.application.output.repository.ProductAggregateRepository;
import com.essenza.draco.modules.catalog.application.output.repository.ProductCatalogViewRepository;
import com.essenza.draco.modules.catalog.application.services.ProductCommandAssembler;
import com.essenza.draco.modules.catalog.application.services.ProductDtoAssembler;
import com.essenza.draco.modules.catalog.infrastructure.outbound.repositories.products.ProductRepositoryAdapter;
import com.essenza.draco.shared.common.inventory.StockQuery;

/** Mocks comunes a los tests de los casos de uso de producto (antes ProductServiceImplTest). */
@ExtendWith(MockitoExtension.class)
abstract class ProductServiceTestSupport {

    @Mock
    ProductRepositoryAdapter productRepository;

    @Mock
    ProductAggregateRepository aggregateRepository;

    @Mock
    ProductCatalogViewRepository catalogView;

    @Mock
    StockQuery stockQuery;

    @Mock
    com.essenza.draco.modules.catalog.application.input.attribute.ProductPublicationGuard publicationGuard;

    final ProductDtoAssembler productDtoAssembler = new ProductDtoAssembler();
    final ProductCommandAssembler productCommandAssembler = new ProductCommandAssembler();

    /** Soporte con la unidad siempre válida (como el antiguo constructor sin UnitReferences). */
    ProductSoporte soporte() {
        return new ProductSoporte(productRepository, aggregateRepository, productDtoAssembler, catalogView, stockQuery,
                unitId -> true, new com.essenza.draco.shared.common.billing.TaxRates() {
                    @Override
                    public TaxRateView effective(Long taxRateId) {
                        return new TaxRateView(1L, "IVA_19", "IVA 19 %", "IVA", java.math.BigDecimal.valueOf(19));
                    }

                    @Override
                    public boolean isAssignable(Long taxRateId) {
                        return true;
                    }
                });
    }
}
