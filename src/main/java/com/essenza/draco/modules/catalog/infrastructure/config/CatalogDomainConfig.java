package com.essenza.draco.modules.catalog.infrastructure.config;

import com.essenza.draco.modules.catalog.domain.model.pricing.BundlePriceResolver;
import com.essenza.draco.modules.catalog.domain.services.ProductFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra como beans las piezas del dominio del catálogo, que son Java puro
 * (sin anotaciones de Spring) para poder probarlas sin contenedor.
 */
@Configuration
public class CatalogDomainConfig {

    @Bean
    public ProductFactory productFactory(BundlePriceResolver bundlePriceResolver) {
        return new ProductFactory(bundlePriceResolver);
    }
}
