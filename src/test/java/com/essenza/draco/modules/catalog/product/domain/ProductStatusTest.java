package com.essenza.draco.modules.catalog.product.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.essenza.draco.modules.catalog.domain.model.ProductStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ProductStatusTest {

    @Test
    void soloActivoEstaPublicado() {
        assertThat(ProductStatus.ACTIVE.isListed()).isTrue();
        assertThat(ProductStatus.DRAFT.isListed()).isFalse();
        assertThat(ProductStatus.INACTIVE.isListed()).isFalse();
        assertThat(ProductStatus.ARCHIVED.isListed()).isFalse();
    }

    @ParameterizedTest(name = "available={0}, actual={1} -> {2}")
    @CsvSource({
            "true,  DRAFT,    ACTIVE",
            "true,  ARCHIVED, ACTIVE",
            "false, ACTIVE,   INACTIVE",
            "false, DRAFT,    DRAFT",
            "false, ARCHIVED, ARCHIVED",
            "false, INACTIVE, INACTIVE"
    })
    void compatibilidadConElBooleanoAvailable(boolean available, ProductStatus current, ProductStatus expected) {
        assertThat(ProductStatus.fromAvailability(available, current)).isEqualTo(expected);
    }

    @Test
    void parseToleraMinusculasYVacio() {
        assertThat(ProductStatus.parse(" archived ")).isEqualTo(ProductStatus.ARCHIVED);
        assertThat(ProductStatus.parse("")).isNull();
        assertThat(ProductStatus.parse(null)).isNull();
        assertThatThrownBy(() -> ProductStatus.parse("PUBLICADO")).isInstanceOf(IllegalArgumentException.class);
    }
}
