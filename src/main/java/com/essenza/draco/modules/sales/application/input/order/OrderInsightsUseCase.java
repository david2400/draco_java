package com.essenza.draco.modules.sales.application.input.order;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.essenza.draco.modules.sales.application.dto.order.CustomerLookupDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderFilter;
import com.essenza.draco.modules.sales.application.dto.order.OrderNumberingDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderSummaryDto;
import com.essenza.draco.shared.common.domain.dto.PageResponse;

/** Listado con filtros, KPIs, clientes frecuentes y numeración. */
public interface OrderInsightsUseCase {

    PageResponse<OrderDto> search(OrderFilter filter, Pageable pageable);

    OrderSummaryDto summary(OrderFilter filter);

    List<CustomerLookupDto> customers(String query);

    /** Cliente por documento: usuario de la empresa en el registro central (access_control) más su última orden. */
    com.essenza.draco.modules.sales.application.dto.order.CustomerByDocumentDto customerByDocument(String docType, String docNumber, Long companyId);

    OrderNumberingDto numbering();

    OrderNumberingDto saveNumbering(OrderNumberingDto input);
}
