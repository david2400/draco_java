package com.essenza.draco.modules.catalog.product.application;

import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.combo;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.panelUpdate;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.simple;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.variant;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.variantDto;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.withVariants;
import static org.assertj.core.api.Assertions.assertThat;

import com.essenza.draco.modules.catalog.application.services.ProductCommandAssembler;
import com.essenza.draco.modules.catalog.domain.command.ProductCommand;
import com.essenza.draco.modules.catalog.application.dto.product.UpdateProductDto;
import com.essenza.draco.modules.catalog.domain.model.BundleItem;
import com.essenza.draco.modules.catalog.domain.model.Product;
import com.essenza.draco.modules.catalog.domain.model.ProductId;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Reglas de actualización parcial: lo que el payload no trae se conserva. */
class ProductCommandAssemblerTest {

    private final ProductCommandAssembler assembler = new ProductCommandAssembler();

    @Test
    void usaElIdDeLaUrlAunqueElBodyNoLoTraiga() {
        ProductCommand command = assembler.fromUpdateDto(42L, panelUpdate("Nuevo nombre"), simple(42L, 5, true));

        assertThat(command.getId()).isEqualTo(42L);
        assertThat(command.getName()).isEqualTo("Nuevo nombre");
    }

    @Test
    void elIdDelBodySeIgnoraSiNoCoincideConLaUrl() {
        UpdateProductDto dto = panelUpdate("X");
        dto.setId(999L);

        assertThat(assembler.fromUpdateDto(42L, dto, simple(42L, 5, true)).getId()).isEqualTo(42L);
    }

    @Test
    void sinCampoVariantsConservaLasVariantesActuales() {
        Product current = withVariants(42L, variant(10L, "30 ml", 2, true), variant(11L, "50 ml", 0, false));

        ProductCommand command = assembler.fromUpdateDto(42L, panelUpdate("Ficha"), current);

        assertThat(command.getVariants())
                .extracting("id", "name", "available")
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(10L, "30 ml", true),
                        org.assertj.core.groups.Tuple.tuple(11L, "50 ml", false));
    }

    @Test
    void listaVaciaDeVariantesSignificaEliminarlas() {
        Product current = withVariants(42L, variant(10L, "30 ml", 2, true));
        UpdateProductDto dto = panelUpdate("Ficha");
        dto.setVariants(List.of());

        assertThat(assembler.fromUpdateDto(42L, dto, current).getVariants()).isEmpty();
    }

    @Test
    void variantesEnviadasReemplazanLasActuales() {
        Product current = withVariants(42L, variant(10L, "30 ml", 2, true));
        UpdateProductDto dto = panelUpdate("Ficha");
        dto.setVariants(List.of(variantDto(10L, "30 ml (renombrada)"), variantDto(null, "100 ml")));

        assertThat(assembler.fromUpdateDto(42L, dto, current).getVariants())
                .extracting("name")
                .containsExactly("30 ml (renombrada)", "100 ml");
    }

    @Test
    void comboSinCampoBundleItemsConservaSusComponentes() {
        Product current = combo(42L, BundleItem.of(ProductId.of(7L), 2), BundleItem.of(ProductId.of(8L), 1));
        UpdateProductDto dto = panelUpdate("Kit");
        dto.setIsCombo(true);

        ProductCommand command = assembler.fromUpdateDto(42L, dto, current);

        assertThat(command.getIsCombo()).isTrue();
        assertThat(command.getBundleItems()).extracting("productId", "quantity")
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(7L, 2),
                        org.assertj.core.groups.Tuple.tuple(8L, 1));
        assertThat(command.getVariants()).isEmpty();
    }
}
