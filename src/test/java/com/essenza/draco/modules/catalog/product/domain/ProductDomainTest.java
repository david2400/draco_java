package com.essenza.draco.modules.catalog.product.domain;

import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.FACTORY;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.combo;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.draft;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.simple;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.variant;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.withVariants;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.essenza.draco.modules.catalog.domain.command.ProductBundleItemCommand;
import com.essenza.draco.modules.catalog.domain.command.ProductCommand;
import com.essenza.draco.modules.catalog.domain.command.ProductVariantCommand;
import com.essenza.draco.modules.catalog.domain.model.BundleItem;
import com.essenza.draco.modules.catalog.domain.model.Product;
import com.essenza.draco.modules.catalog.domain.model.ProductId;
import com.essenza.draco.modules.catalog.domain.model.ProductType;
import com.essenza.draco.modules.catalog.domain.model.StockInfo;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** Caracteriza el agregado Product antes de evolucionar el catálogo. */
class ProductDomainTest {

    @Nested
    @DisplayName("Estado comercial (listed) vs vendible (sellable)")
    class ListedVsSellable {

        @Test
        void productoPublicadoSinStockSigueListadoPeroNoVendible() {
            Product product = simple(1L, 0, true);

            assertThat(product.isListed()).isTrue();
            assertThat(product.isSellable()).isFalse();
        }

        @Test
        void productoDespublicadoNoEsVendibleAunqueTengaStock() {
            Product product = simple(1L, 25, false);

            assertThat(product.isListed()).isFalse();
            assertThat(product.isSellable()).isFalse();
        }

        @Test
        void productoConVariantesEsVendibleSiAlgunaLoEs() {
            Product product = withVariants(1L,
                    variant(10L, "30 ml", 0, true),
                    variant(11L, "50 ml", 4, true));

            assertThat(product.isSellable()).isTrue();
            assertThat(product.getVariants().get(0).isListed()).isTrue();
            assertThat(product.getVariants().get(0).isSellable()).isFalse();
        }

        @Test
        void isAvailableSeMantieneComoAliasDeSellable() {
            Product product = simple(1L, 0, true);

            assertThat(product.isAvailable()).isEqualTo(product.isSellable());
        }
    }

    @Nested
    @DisplayName("Resolución del tipo en la fábrica")
    class FactoryType {

        private ProductCommand.ProductCommandBuilder base() {
            return ProductCommand.builder()
                    .name("Kit")
                    .stock(1)
                    .realPrice(BigDecimal.ONE)
                    .unitPrice(BigDecimal.TEN)
                    .brandId(1L).categoryId(2L).subcategoryId(3L).supplierId(4L)
                    .available(true);
        }

        @Test
        void sinVariantesNiComboEsSimple() {
            assertThat(FACTORY.fromCommand(base().build()).getType()).isEqualTo(ProductType.SIMPLE);
        }

        @Test
        void conVariantesEsVariant() {
            Product product = FACTORY.fromCommand(base()
                    .variants(List.of(ProductVariantCommand.builder()
                            .name("50 ml").stock(2).unitPrice(BigDecimal.TEN).available(true).build()))
                    .build());

            assertThat(product.getType()).isEqualTo(ProductType.VARIANT);
            assertThat(product.getVariants()).hasSize(1);
        }

        @Test
        void comboExigeComponentes() {
            assertThatThrownBy(() -> FACTORY.fromCommand(base().isCombo(true).build()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void comboHeredadoSinComponentesSeRehidrataSinFallar() {
            Product product = FACTORY.fromDraft(draft(1L, ProductType.COMBO, 0, true), List.of(), List.of());
            assertThat(product.getType()).isEqualTo(ProductType.COMBO);
            assertThat(product.getBundleItems()).isEmpty();
        }

        @Test
        void comboConComponentesEsCombo() {
            Product product = FACTORY.fromCommand(base().isCombo(true)
                    .bundleItems(List.of(ProductBundleItemCommand.builder().productId(9L).quantity(2).build()))
                    .build());

            assertThat(product.getType()).isEqualTo(ProductType.COMBO);
            assertThat(product.getBundleItems()).hasSize(1);
        }

        @Test
        void variantesNoPermitidasEnProductoSimpleConstruidoDesdeBorrador() {
            assertThatThrownBy(() -> FACTORY.fromDraft(draft(1L, ProductType.SIMPLE, 1, true),
                    List.of(variant(1L, "x", 1, true)), List.of()))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("StockInfo")
    class Stock {

        @Test
        void disponibleEsOnHandMenosReservado() {
            assertThat(StockInfo.of(10, 3).getAvailableToSell()).isEqualTo(7);
        }

        @Test
        void noPermiteReservarMasDeLoDisponible() {
            assertThatThrownBy(() -> StockInfo.of(5, 0).reserve(6)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void noPermiteValoresNegativos() {
            assertThatThrownBy(() -> StockInfo.of(-1, 0)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void precioDeComboSumaComponentesSegunLaPolitica() {
        Product product = combo(1L, BundleItem.of(ProductId.of(9L), 2));

        // La política compuesta usa el resolver de prueba: 10 por unidad.
        BigDecimal price = product.calculatePrice(
                com.essenza.draco.modules.catalog.domain.model.pricing.ProductPricingRequest.simple(1));

        assertThat(price).isEqualByComparingTo("20");
    }
}
