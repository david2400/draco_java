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

import com.essenza.draco.modules.catalog.application.dto.attribute.AttributeDto;
import com.essenza.draco.modules.catalog.application.dto.attribute.SaveAttributeDto;
import com.essenza.draco.modules.catalog.application.input.attribute.ManageAttributesUseCase;
import com.essenza.draco.shared.exceptions.NotFoundException;

@RestController
@RequestMapping("/catalog/attributes")
@Tag(name = "Attributes", description = "Atributos del catálogo (ficha técnica y ejes de variante)")
public class AttributeController {

    private final ManageAttributesUseCase attributes;

    public AttributeController(ManageAttributesUseCase attributes) {
        this.attributes = attributes;
    }

    @Operation(summary = "List attributes", description = "Todos los atributos con sus opciones (lista corta, sin paginar).")
    @GetMapping
    public List<AttributeDto> findAll() {
        return attributes.findAll();
    }

    @Operation(summary = "Get attribute")
    @GetMapping("/{id}")
    public AttributeDto findById(@PathVariable Long id) {
        return attributes.findById(id).orElseThrow(() -> new NotFoundException("Atributo no encontrado: " + id));
    }

    @Operation(summary = "Create attribute", description = "409 si el código ya existe.")
    @PostMapping
    public ResponseEntity<AttributeDto> create(@Valid @RequestBody SaveAttributeDto input) {
        AttributeDto created = attributes.create(input);
        return ResponseEntity.created(URI.create("/catalog/attributes/" + created.id())).body(created);
    }

    @Operation(summary = "Update attribute",
            description = "Reemplaza el atributo. 409 si se cambia el tipo de un atributo en uso o se quita una opción en uso.")
    @PutMapping("/{id}")
    public AttributeDto update(@PathVariable Long id, @Valid @RequestBody SaveAttributeDto input) {
        return attributes.update(id, input);
    }

    @Operation(summary = "Delete attribute", description = "409 si se usa en plantillas o productos.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        attributes.delete(id);
        return ResponseEntity.noContent().build();
    }
}
