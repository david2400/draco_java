package com.essenza.draco.modules.billing.infrastructure.inbound.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.essenza.draco.modules.billing.application.dto.resolution.ResolutionDto;
import com.essenza.draco.modules.billing.application.dto.resolution.SaveResolutionDto;
import com.essenza.draco.modules.billing.application.input.resolution.CreateResolutionUseCase;
import com.essenza.draco.modules.billing.application.input.resolution.DeleteResolutionUseCase;
import com.essenza.draco.modules.billing.application.input.resolution.FindResolutionsUseCase;
import com.essenza.draco.modules.billing.application.input.resolution.UpdateResolutionUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/billing/resolutions")
@Tag(name = "Billing resolutions", description = "Numeración autorizada por la DIAN para facturas y notas crédito")
@RequiredArgsConstructor
public class ResolutionController {

    private final FindResolutionsUseCase findResolutions;
    private final CreateResolutionUseCase createResolution;
    private final UpdateResolutionUseCase updateResolution;
    private final DeleteResolutionUseCase deleteResolution;

    @Operation(summary = "List", description = "kind = INVOICE | CREDIT_NOTE (vacío = todas). Incluye avisos de vencimiento y agotamiento.")
    @GetMapping
    public List<ResolutionDto> list(@RequestParam(value = "kind", required = false) String kind) {
        return findResolutions.findAll(kind);
    }

    @Operation(summary = "Get by id")
    @GetMapping("/{id}")
    public ResolutionDto get(@PathVariable Long id) {
        return findResolutions.findById(id);
    }

    @Operation(summary = "Create")
    @PostMapping
    public ResponseEntity<ResolutionDto> create(@Valid @RequestBody SaveResolutionDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createResolution.create(input));
    }

    @Operation(summary = "Update", description = "Con documentos emitidos no cambian tipo, prefijo ni número inicial.")
    @PutMapping("/{id}")
    public ResolutionDto update(@PathVariable Long id, @Valid @RequestBody SaveResolutionDto input) {
        return updateResolution.update(id, input);
    }

    @Operation(summary = "Delete", description = "409 si ya tiene documentos.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteResolution.delete(id);
        return ResponseEntity.noContent().build();
    }
}
