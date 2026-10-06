package com.essenza.draco.modules.catalog.infrastructure.inbound.rest;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.essenza.draco.modules.catalog.application.dto.attribute.ProductTemplateDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.SaveProductTemplateDto;
import com.essenza.draco.modules.catalog.application.input.attribute.ManageProductTemplatesUseCase;
import com.essenza.draco.shared.exceptions.NotFoundException;

@RestController
@RequestMapping("/catalog/product_templates")
@Tag(name = "Product templates", description = "Plantillas por tipo de producto")
public class ProductTemplateController {

    private final ManageProductTemplatesUseCase templates;

    public ProductTemplateController(ManageProductTemplatesUseCase templates) {
        this.templates = templates;
    }

    @Operation(summary = "List product templates")
    @GetMapping
    public List<ProductTemplateDto> findAll() {
        return templates.findAll();
    }

    @Operation(summary = "Get product template")
    @GetMapping("/{id}")
    public ProductTemplateDto findById(@PathVariable Long id) {
        return templates.findById(id).orElseThrow(() -> new NotFoundException("Plantilla no encontrada: " + id));
    }

    @Operation(summary = "Create product template",
            description = "Solo los atributos OPTION pueden ser eje de variante. 409 si el nombre ya existe.")
    @PostMapping
    public ResponseEntity<ProductTemplateDto> create(@Valid @RequestBody SaveProductTemplateDto input) {
        ProductTemplateDto created = templates.create(input);
        return ResponseEntity.created(URI.create("/catalog/product_templates/" + created.id())).body(created);
    }

    @Operation(summary = "Update product template", description = "Reemplaza la lista de atributos.")
    @PutMapping("/{id}")
    public ProductTemplateDto update(@PathVariable Long id, @Valid @RequestBody SaveProductTemplateDto input) {
        return templates.update(id, input);
    }

    @Operation(summary = "Delete product template", description = "409 si algún producto la usa.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        templates.delete(id);
        return ResponseEntity.noContent().build();
    }
}
