package com.essenza.draco.shared.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FieldPathsTest {

    @Test
    void traduceNombresSimplesYAnidados() {
        assertThat(FieldPaths.toSnakeCase("categoryId")).isEqualTo("category_id");
        assertThat(FieldPaths.toSnakeCase("name")).isEqualTo("name");
        assertThat(FieldPaths.toSnakeCase("variants[2].unitPrice")).isEqualTo("variants[2].unit_price");
        assertThat(FieldPaths.toSnakeCase("create.arg0.skuId")).isEqualTo("create.arg0.sku_id");
    }

    @Test
    void toleraVacios() {
        assertThat(FieldPaths.toSnakeCase(null)).isNull();
        assertThat(FieldPaths.toSnakeCase("")).isEmpty();
    }
}
