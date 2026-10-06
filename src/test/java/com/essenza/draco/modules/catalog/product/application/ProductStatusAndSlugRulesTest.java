package com.essenza.draco.modules.catalog.product.application;

import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.FACTORY;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.draft;
import static com.essenza.draco.modules.catalog.product.support.ProductFixtures.panelUpdate;
import static org.assertj.core.api.Assertions.assertThat;

import com.essenza.draco.modules.catalog.application.dto.product.CreateProductDto;
import com.essenza.draco.modules.catalog.application.dto.product.UpdateProductDto;
import com.essenza.draco.modules.catalog.application.services.ProductCommandAssembler;
import com.essenza.draco.modules.catalog.domain.command.ProductCommand;
import com.essenza.draco.modules.catalog.domain.model.Product;
import com.essenza.draco.modules.catalog.domain.model.ProductStatus;
import com.essenza.draco.modules.catalog.domain.model.ProductType;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Reglas de estado editorial y slug al crear/actualizar (compatibles con "available"). */
class ProductStatusAndSlugRulesTest {

    private final ProductCommandAssembler assembler = new ProductCommandAssembler();

    private static Product current(ProductStatus status, String slug) {
        return FACTORY.fromDraft(new com.essenza.draco.modules.catalog.domain.services.ProductDraft(
                draft(42L, ProductType.SIMPLE, 5, status.isListed()).id(), "Perfume", "x", ProductType.SIMPLE,
                draft(42L, ProductType.SIMPLE, 5, true).stockInfo(),
                java.math.BigDecimal.ONE, java.math.BigDecimal.TEN, null, null, null, null, null,
                1L, 2L, 3L, 4L, status.isListed(), status, slug), List.of(), List.of());
    }

    private static CreateProductDto create(Boolean available, String status) {
        CreateProductDto dto = panelUpdate("Perfume");
        dto.setAvailable(available);
        dto.setStatus(status);
        return dto;
    }

    @Test
    void altaPublicadaPorDefecto() {
        ProductCommand command = assembler.fromCreateDto(create(true, null));
        assertThat(command.getStatus()).isEqualTo("ACTIVE");
        assertThat(command.getAvailable()).isTrue();
    }

    @Test
    void altaConAvailableFalseQuedaEnBorrador() {
        assertThat(assembler.fromCreateDto(create(false, null)).getStatus()).isEqualTo("DRAFT");
    }

    @Test
    void elEstadoExplicitoMandaSobreAvailable() {
        ProductCommand command = assembler.fromCreateDto(create(true, "ARCHIVED"));
        assertThat(command.getStatus()).isEqualTo("ARCHIVED");
        assertThat(command.getAvailable()).isFalse();
    }

    @Test
    void desactivarUnArchivadoLoMantieneArchivado() {
        UpdateProductDto dto = panelUpdate("Perfume");
        dto.setAvailable(false);
        ProductCommand command = assembler.fromUpdateDto(42L, dto, current(ProductStatus.ARCHIVED, "perfume"));
        assertThat(command.getStatus()).isEqualTo("ARCHIVED");
    }

    @Test
    void desactivarUnActivoLoPausa() {
        UpdateProductDto dto = panelUpdate("Perfume");
        dto.setAvailable(false);
        assertThat(assembler.fromUpdateDto(42L, dto, current(ProductStatus.ACTIVE, "perfume")).getStatus())
                .isEqualTo("INACTIVE");
    }

    @Test
    void reenviarElProductoLeidoCambiandoSoloAvailableLoDesactiva() {
        UpdateProductDto dto = panelUpdate("Perfume");
        dto.setStatus("ACTIVE");   // viene del GET
        dto.setAvailable(false);   // lo único que cambió el cliente
        assertThat(assembler.fromUpdateDto(42L, dto, current(ProductStatus.ACTIVE, "perfume")).getStatus())
                .isEqualTo("INACTIVE");
    }

    @Test
    void cambioExplicitoDeEstadoGanaAunqueAvailableNoCoincida() {
        UpdateProductDto dto = panelUpdate("Perfume");
        dto.setStatus("ARCHIVED");
        dto.setAvailable(true);
        assertThat(assembler.fromUpdateDto(42L, dto, current(ProductStatus.ACTIVE, "perfume")).getStatus())
                .isEqualTo("ARCHIVED");
    }

    @Test
    void sinSlugEnElPayloadSeConservaElActual() {
        UpdateProductDto dto = panelUpdate("Nombre nuevo");
        assertThat(assembler.fromUpdateDto(42L, dto, current(ProductStatus.ACTIVE, "perfume-original")).getSlug())
                .isEqualTo("perfume-original");
    }

    @Test
    void elProductoDerivaAvailableDelEstado() {
        Product product = current(ProductStatus.INACTIVE, "p");
        assertThat(product.isListed()).isFalse();
        assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);
    }
}
