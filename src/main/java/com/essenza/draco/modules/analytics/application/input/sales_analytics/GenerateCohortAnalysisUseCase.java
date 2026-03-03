package com.essenza.draco.modules.analytics.application.input.sales_analytics;

import com.essenza.draco.modules.analytics.domain.dto.sales_analytics.SalesCohortAnalysisDto;

import java.time.LocalDate;
import java.util.List;

public interface GenerateCohortAnalysisUseCase {
    List<SalesCohortAnalysisDto> generateCohortAnalysis(LocalDate startDate, LocalDate endDate);
}
