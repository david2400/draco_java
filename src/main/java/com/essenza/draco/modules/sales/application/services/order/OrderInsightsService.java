package com.essenza.draco.modules.sales.application.services.order;

import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.essenza.draco.modules.sales.application.dto.order.CustomerByDocumentDto;
import com.essenza.draco.modules.sales.application.dto.order.CustomerLookupDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderFilter;
import com.essenza.draco.modules.sales.application.dto.order.OrderNumberingDto;
import com.essenza.draco.modules.sales.application.dto.order.OrderSummaryDto;
import com.essenza.draco.modules.sales.application.input.order.OrderInsightsUseCase;
import com.essenza.draco.modules.sales.application.output.gateway.PersonDirectory;
import com.essenza.draco.modules.sales.application.output.repository.OrderInsights;
import com.essenza.draco.modules.sales.application.output.repository.OrderLedger;
import com.essenza.draco.modules.sales.domain.model.order.OrderNumber;
import com.essenza.draco.modules.sales.domain.model.order.OrderRuleViolation;
import com.essenza.draco.shared.common.domain.dto.PageResponse;

/** Listado con filtros y datos calculados, KPIs, clientes de órdenes anteriores y numeración. */
@Service
@Transactional
public class OrderInsightsService implements OrderInsightsUseCase {

    private final OrderInsights insights;
    private final OrderLedger ledger;
    private final PersonDirectory directory;

    public OrderInsightsService(OrderInsights insights, OrderLedger ledger, PersonDirectory directory) {
        this.insights = insights;
        this.ledger = ledger;
        this.directory = directory;
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerByDocumentDto customerByDocument(String docType, String docNumber, Long companyId) {
        String number = docNumber == null ? "" : docNumber.trim().replace(" ", "").replace(".", "");
        if (number.length() < 3 || number.length() > 30) {
            throw new OrderRuleViolation("Escribe un número de documento válido (3 a 30 caracteres).");
        }
        String type = docType == null || docType.isBlank() ? null : docType.trim().toUpperCase(Locale.ROOT);
        PersonDirectory.Result central = directory.findByDocument(companyId, centralDocType(type), number);
        CustomerLookupDto last = insights.latestByDocument(number).orElse(null);
        PersonDirectory.Person person = central.person();
        String centralType = person == null ? null : storeDocType(person.typeId());
        boolean typeMatches = person == null || type == null || centralType == null || centralType.equals(type);
        String username = person == null ? null : person.username();
        String email = username != null && EMAIL.matcher(username).matches() ? username.toLowerCase(Locale.ROOT)
                : last == null ? null : last.email();
        String source = person != null ? "CENTRAL" : last != null ? "ORDERS" : "NONE";
        return new CustomerByDocumentDto(source, central.status().name(), typeMatches,
                person != null ? person.clientId() : last == null ? null : last.customerId(),
                person != null && centralType != null ? centralType : last != null && last.docType() != null ? last.docType() : type,
                person != null && person.cardId() != null ? person.cardId() : number,
                person != null && person.fullName() != null ? person.fullName() : last == null ? null : last.name(),
                person != null ? ("NIT".equals(centralType) ? "COMPANY" : "PERSON") : last == null ? null : last.kind(),
                person == null ? null : person.status(), person == null ? null : person.userId(), username,
                person == null ? null : person.userStatus(), email,
                last == null ? null : last.phone(), last == null ? null : last.billingAddress(),
                last == null ? null : last.billingCity(), last == null ? null : last.recipientName(),
                last == null ? null : last.recipientPhone(), last == null ? null : last.shipAddress(),
                last == null ? null : last.shipAddressDetail(), last == null ? null : last.shipCountryId(),
                last == null ? null : last.shipStateId(), last == null ? null : last.shipCityId(),
                last == null ? null : last.shipCity(), last == null ? null : last.shipPostalCode(),
                last == null ? 0 : last.ordersCount(), last == null ? null : last.lastOrderAt());
    }

    private static final java.util.regex.Pattern EMAIL = java.util.regex.Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$");

    /** Tipo de documento de la tienda al código de access_control (2 letras). */
    static String centralDocType(String storeType) {
        if (storeType == null || storeType.isBlank()) {
            return null;
        }
        String value = storeType.trim().toUpperCase(Locale.ROOT);
        return switch (value) {
            case "NIT" -> "NI";
            case "PEP" -> "PE";
            default -> value;
        };
    }

    /** Tipos de documento de access_control (2 letras) a los de la tienda. */
    static String storeDocType(String centralType) {
        if (centralType == null || centralType.isBlank()) {
            return null;
        }
        String value = centralType.trim().toUpperCase(Locale.ROOT);
        return switch (value) {
            case "NI", "NT", "NIT" -> "NIT";
            case "PE", "PEP" -> "PEP";
            case "CC", "CE", "TI", "PP" -> value;
            default -> null;
        };
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderDto> search(OrderFilter filter, Pageable pageable) {
        Page<OrderDto> page = insights.search(filter == null ? OrderFilter.NONE : filter, pageable);
        List<OrderDto> rows = insights.enrich(page.getContent()).stream().map(OrderSoporte::withNextStates).toList();
        return PageResponse.from(new PageImpl<>(rows, page.getPageable(), page.getTotalElements()));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderSummaryDto summary(OrderFilter filter) {
        return insights.summary(filter == null ? OrderFilter.NONE : filter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerLookupDto> customers(String query) {
        String q = query == null ? "" : query.trim();
        if (q.length() < 2) {
            return List.of();
        }
        return insights.customers(q, 10);
    }

    @Override
    public OrderNumberingDto numbering() {
        OrderNumber numbering = ledger.lockNumbering();
        return toDto(numbering);
    }

    @Override
    public OrderNumberingDto saveNumbering(OrderNumberingDto input) {
        ledger.lockNumbering();
        OrderNumber numbering = new OrderNumber(input.prefix(), input.padding(), input.nextNumber());
        ledger.saveNumbering(numbering);
        return toDto(numbering);
    }

    private static OrderNumberingDto toDto(OrderNumber numbering) {
        return new OrderNumberingDto(numbering.prefix(), numbering.padding(), numbering.next(), numbering.current());
    }
}
