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

import com.essenza.draco.modules.billing.application.dto.tax_rate.SaveTaxRateDto;
import com.essenza.draco.modules.billing.application.dto.tax_rate.TaxRateDto;
import com.essenza.draco.modules.billing.application.input.tax_rate.CreateTaxRateUseCase;
import com.essenza.draco.modules.billing.application.input.tax_rate.DeleteTaxRateUseCase;
import com.essenza.draco.modules.billing.application.input.tax_rate.FindTaxRatesUseCase;
import com.essenza.draco.modules.billing.application.input.tax_rate.UpdateTaxRateUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/billing/tax_rates")
@Tag(name = "Tax rates", description = "Tarifas de impuesto (IVA, INC, excluido) que se asignan a los productos")
@RequiredArgsConstructor
public class TaxRateController {

    private final FindTaxRatesUseCase findTaxRates;
    private final CreateTaxRateUseCase createTaxRate;
    private final UpdateTaxRateUseCase updateTaxRate;
    private final DeleteTaxRateUseCase deleteTaxRate;

    @Operation(summary = "List", description = "Tarifas con su uso; includeInactive=false para solo las activas.")
    @GetMapping
    public List<TaxRateDto> list(@RequestParam(value = "includeInactive", defaultValue = "true") boolean includeInactive) {
        return findTaxRates.findAll(includeInactive);
    }

    @Operation(summary = "Get by id")
    @GetMapping("/{id}")
    public TaxRateDto get(@PathVariable Long id) {
        return findTaxRates.findById(id);
    }

    @Operation(summary = "Create")
    @PostMapping
    public ResponseEntity<TaxRateDto> create(@Valid @RequestBody SaveTaxRateDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createTaxRate.create(input));
    }

    @Operation(summary = "Update", description = "Cambiar el porcentaje solo afecta ventas nuevas.")
    @PutMapping("/{id}")
    public TaxRateDto update(@PathVariable Long id, @Valid @RequestBody SaveTaxRateDto input) {
        return updateTaxRate.update(id, input);
    }

    @Operation(summary = "Delete", description = "409 si es la por defecto o está en uso.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteTaxRate.delete(id);
        return ResponseEntity.noContent().build();
    }
}
