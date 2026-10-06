package com.essenza.draco.modules.catalog.application.input.attribute;

/** Impide publicar (pasar a ACTIVE) un producto con la ficha o las variantes incompletas. */
public interface ProductPublicationGuard {

    /** @throws com.essenza.draco.shared.exceptions.ConflictException si falta algo obligatorio */
    void assertPublishable(Long productId);
}
