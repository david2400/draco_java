package com.essenza.draco.modules.catalog.infrastructure.inbound.rest;

import com.essenza.draco.modules.catalog.application.dto.variant.CreateAxisOptionDto;
import com.essenza.draco.modules.catalog.application.dto.variant.SaveVariantMatrixDto;
import com.essenza.draco.modules.catalog.application.dto.variant.SaveVariantMatrixResultDto;
import com.essenza.draco.modules.catalog.application.dto.variant.VariantMatrixDto;
import com.essenza.draco.modules.catalog.application.input.variant.VariantMatrixUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalog/products/{productId}/variant-matrix")
@Tag(name = "Variant generator", description = "Ejes, valores y matriz de variantes de un producto")
public class VariantMatrixController {

    private final VariantMatrixUseCase matrix;

    public VariantMatrixController(VariantMatrixUseCase matrix) {
        this.matrix = matrix;
    }

    @Operation(summary = "Get matrix", description = "Ejes (guardados o de la plantilla) con sus valores, atributos disponibles y variantes.")
    @GetMapping
    public VariantMatrixDto get(@PathVariable Long productId) {
        return matrix.get(productId);
    }

    @Operation(summary = "Save matrix", description = "Reemplaza el conjunto de variantes: crea, actualiza y quita (desactiva si "
            + "tuvo stock, reservas o ventas; si no, borra). 400 si una regla no se cumple; 409 si un SKU o código de barras ya existe.")
    @PutMapping
    public SaveVariantMatrixResultDto save(@PathVariable Long productId, @Valid @RequestBody SaveVariantMatrixDto input) {
        return matrix.save(productId, input);
    }

    @Operation(summary = "Create axis value", description = "Agrega un valor al atributo (o devuelve el existente con el mismo texto).")
    @PostMapping("/options")
    public ResponseEntity<VariantMatrixDto.Option> createOption(@PathVariable Long productId,
                                                                @Valid @RequestBody CreateAxisOptionDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matrix.createOption(productId, input));
    }
}
