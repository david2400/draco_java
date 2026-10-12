package com.essenza.draco.modules.catalog.application.services.attribute;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.essenza.draco.modules.catalog.application.input.attribute.ProductPublicationGuard;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAttributesStore;
import com.essenza.draco.shared.exceptions.ConflictException;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductPublicationGuardService implements ProductPublicationGuard {

    private final ProductAttributesStore store;
    private final AttributeSoporte soporte;

    @Override
    @Transactional(readOnly = true)
    public void assertPublishable(Long productId) {
        if (store.findProduct(productId).isEmpty()) {
            return;
        }
        List<String> missing = soporte.load(productId).missing();
        if (!missing.isEmpty()) {
            throw new ConflictException("No se puede publicar el producto: falta " + String.join("; ", missing)
                    + ". Complétalo en la ficha técnica.");
        }
    }
}
