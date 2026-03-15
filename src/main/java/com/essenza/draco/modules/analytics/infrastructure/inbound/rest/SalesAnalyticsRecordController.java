package com.essenza.draco.modules.analytics.infrastructure.inbound.rest;

import com.essenza.draco.modules.analytics.application.input.sales_analytics_record.CreateSalesAnalyticsRecordUseCase;
import com.essenza.draco.modules.analytics.application.input.sales_analytics_record.DeleteSalesAnalyticsRecordUseCase;
import com.essenza.draco.modules.analytics.application.input.sales_analytics_record.FindSalesAnalyticsRecordByIdUseCase;
import com.essenza.draco.modules.analytics.application.input.sales_analytics_record.FindSalesAnalyticsRecordsUseCase;
import com.essenza.draco.modules.analytics.application.input.sales_analytics_record.UpdateSalesAnalyticsRecordUseCase;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.CreateSalesAnalyticsRecordDto;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.SalesAnalyticsRecordDto;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.UpdateSalesAnalyticsRecordDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/analytics/sales/records")
@Validated
@Tag(name = "Sales Analytics Records", description = "CRUD operations for persisted sales analytics snapshots")
public class SalesAnalyticsRecordController {

    private final CreateSalesAnalyticsRecordUseCase createRecord;
    private final UpdateSalesAnalyticsRecordUseCase updateRecord;
    private final DeleteSalesAnalyticsRecordUseCase deleteRecord;
    private final FindSalesAnalyticsRecordByIdUseCase findRecordById;
    private final FindSalesAnalyticsRecordsUseCase findRecords;

    public SalesAnalyticsRecordController(CreateSalesAnalyticsRecordUseCase createRecord,
                                          UpdateSalesAnalyticsRecordUseCase updateRecord,
                                          DeleteSalesAnalyticsRecordUseCase deleteRecord,
                                          FindSalesAnalyticsRecordByIdUseCase findRecordById,
                                          FindSalesAnalyticsRecordsUseCase findRecords) {
        this.createRecord = createRecord;
        this.updateRecord = updateRecord;
        this.deleteRecord = deleteRecord;
        this.findRecordById = findRecordById;
        this.findRecords = findRecords;
    }

    @Operation(summary = "Create sales analytics record")
    @PostMapping
    public ResponseEntity<SalesAnalyticsRecordDto> create(@Valid @RequestBody CreateSalesAnalyticsRecordDto input) {
        SalesAnalyticsRecordDto created = createRecord.create(input);
        return ResponseEntity.created(URI.create("/analytics/sales/records/" + created.getId())).body(created);
    }

    @Operation(summary = "Update sales analytics record")
    @PutMapping("/{id}")
    public ResponseEntity<SalesAnalyticsRecordDto> update(@PathVariable Long id,
                                                          @Valid @RequestBody UpdateSalesAnalyticsRecordDto input) {
        SalesAnalyticsRecordDto updated = updateRecord.update(id, input);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get record by id")
    @GetMapping("/{id}")
    public ResponseEntity<SalesAnalyticsRecordDto> findById(@PathVariable Long id) {
        return findRecordById.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "List sales analytics records")
    @GetMapping
    public List<SalesAnalyticsRecordDto> findAll() {
        return findRecords.findAll();
    }

    @Operation(summary = "Delete sales analytics record")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = deleteRecord.deleteById(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }
}
