package com.essenza.draco.modules.sales.application.output.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.essenza.draco.modules.sales.application.dto.order.CustomerLookupDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderFilter;
import com.essenza.draco.modules.sales.application.dto.order.OrderDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderSummaryDto;

/** Lecturas de órdenes para el panel: búsqueda con filtros, KPIs, clientes y datos calculados. */
public interface OrderInsights {

    Page<OrderDto> search(OrderFilter filter, Pageable pageable);

    OrderSummaryDto summary(OrderFilter filter);

    /** Completa nombres (tienda, bodega, transportadora), cobro y preparación. */
    List<OrderDto> enrich(List<OrderDto> orders);

    /** Clientes de órdenes anteriores (último dato de cada documento o correo). */
    List<CustomerLookupDto> customers(String query, int limit);

    /** Última orden de un documento exacto (contacto y dirección), con el número de órdenes. */
    java.util.Optional<CustomerLookupDto> latestByDocument(String docNumber);
}
