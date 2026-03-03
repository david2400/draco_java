package com.essenza.draco.modules.analytics.infrastructure.inbound.rest;

import com.essenza.draco.modules.analytics.application.input.sales_analytics.CalculatePerformanceKpisUseCase;
import com.essenza.draco.modules.analytics.application.input.sales_analytics.GenerateCohortAnalysisUseCase;
import com.essenza.draco.modules.analytics.application.input.sales_analytics.GenerateConversionFunnelUseCase;
import com.essenza.draco.modules.analytics.application.input.sales_analytics.GenerateSalesReportUseCase;
import com.essenza.draco.modules.analytics.application.input.sales_analytics.GenerateSalesTrendsUseCase;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.SalesAnalyticsDto;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.SalesCohortAnalysisDto;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.SalesConversionFunnelDto;
import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.SalesPerformanceKpiDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/analytics/sales")
@Tag(name = "Sales Analytics", description = "Sales analytics operations")
public class SalesAnalyticsController {

    private final GenerateSalesReportUseCase generateSalesReportUseCase;
    private final GenerateSalesTrendsUseCase generateSalesTrendsUseCase;
    private final CalculatePerformanceKpisUseCase calculatePerformanceKpisUseCase;
    private final GenerateCohortAnalysisUseCase generateCohortAnalysisUseCase;
    private final GenerateConversionFunnelUseCase generateConversionFunnelUseCase;

    public SalesAnalyticsController(GenerateSalesReportUseCase generateSalesReportUseCase,
                                    GenerateSalesTrendsUseCase generateSalesTrendsUseCase,
                                    CalculatePerformanceKpisUseCase calculatePerformanceKpisUseCase,
                                    GenerateCohortAnalysisUseCase generateCohortAnalysisUseCase,
                                    GenerateConversionFunnelUseCase generateConversionFunnelUseCase) {
        this.generateSalesReportUseCase = generateSalesReportUseCase;
        this.generateSalesTrendsUseCase = generateSalesTrendsUseCase;
        this.calculatePerformanceKpisUseCase = calculatePerformanceKpisUseCase;
        this.generateCohortAnalysisUseCase = generateCohortAnalysisUseCase;
        this.generateConversionFunnelUseCase = generateConversionFunnelUseCase;
    }

    @Operation(summary = "Generate sales report", description = "Generates aggregated sales analytics for the selected period")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sales analytics report",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SalesAnalyticsDto.class)))
    })
    @GetMapping("/report")
    public ResponseEntity<SalesAnalyticsDto> generateReport(
            @Parameter(description = "Start date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Period granularity", example = "DAILY")
            @RequestParam(defaultValue = "DAILY") String period) {
        return ResponseEntity.ok(generateSalesReportUseCase.generateSalesReport(startDate, endDate, period));
    }

    @Operation(summary = "Generate sales trends", description = "Returns sales analytics grouped by period within the range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sales trends",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SalesAnalyticsDto.class))))
    })
    @GetMapping("/trends")
    public ResponseEntity<List<SalesAnalyticsDto>> generateTrends(
            @Parameter(description = "Start date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Period granularity", example = "WEEKLY")
            @RequestParam(defaultValue = "WEEKLY") String period) {
        return ResponseEntity.ok(generateSalesTrendsUseCase.generateSalesTrends(startDate, endDate, period));
    }

    @Operation(summary = "Calculate performance KPIs", description = "Returns growth KPIs comparing current vs previous period")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Performance KPIs",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SalesPerformanceKpiDto.class)))
    })
    @GetMapping("/kpis")
    public ResponseEntity<SalesPerformanceKpiDto> calculateKpis(
            @Parameter(description = "Start date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(calculatePerformanceKpisUseCase.calculatePerformanceKpis(startDate, endDate));
    }

    @Operation(summary = "Generate cohort analysis", description = "Returns retention metrics per customer cohort")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cohort analysis",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SalesCohortAnalysisDto.class))))
    })
    @GetMapping("/cohorts")
    public ResponseEntity<List<SalesCohortAnalysisDto>> generateCohorts(
            @Parameter(description = "Start date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(generateCohortAnalysisUseCase.generateCohortAnalysis(startDate, endDate));
    }

    @Operation(summary = "Generate conversion funnel", description = "Returns conversion funnel metrics for the period")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversion funnel data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SalesConversionFunnelDto.class)))
    })
    @GetMapping("/funnel")
    public ResponseEntity<SalesConversionFunnelDto> generateFunnel(
            @Parameter(description = "Start date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(generateConversionFunnelUseCase.generateConversionFunnel(startDate, endDate));
    }
}
