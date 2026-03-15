package com.essenza.draco.modules.inventory.domain.model;

public enum ProductType {
    SIMPLE,
    VARIANT,
    COMBO;

    public boolean supportsVariants() {
        return this == VARIANT;
    }

    public boolean supportsBundles() {
        return this == COMBO;
    }
}
