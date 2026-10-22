package com.essenza.draco.modules.sales.infrastructure.inbound.rest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.essenza.draco.modules.sales.application.dto.cash.CashMovementDto;
import com.essenza.draco.modules.sales.application.dto.cash.CashSessionDto;
import com.essenza.draco.modules.sales.application.dto.cash.CashSessionFilter;
import com.essenza.draco.modules.sales.application.dto.cash.CloseCashSessionDto;
import com.essenza.draco.modules.sales.application.dto.cash.CreateCashMovementDto;
import com.essenza.draco.modules.sales.application.dto.cash.OpenCashSessionDto;
import com.essenza.draco.modules.sales.application.dto.cash.PosCheckoutDto;
import com.essenza.draco.modules.sales.application.dto.cash.PosCheckoutResultDto;
import com.essenza.draco.modules.sales.application.input.cash.AddCashMovementUseCase;
import com.essenza.draco.modules.sales.application.input.cash.CloseCashSessionUseCase;
import com.essenza.draco.modules.sales.application.input.cash.FindCashSessionUseCase;
import com.essenza.draco.modules.sales.application.input.cash.OpenCashSessionUseCase;
import com.essenza.draco.modules.sales.application.input.cash.PosCheckoutUseCase;
import com.essenza.draco.modules.sales.application.input.cash.SearchCashSessionsUseCase;
import com.essenza.draco.shared.common.domain.dto.PageResponse;
import com.essenza.draco.shared.common.web.PageableFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sales")
@Tag(name = "Cash sessions", description = "Turnos de caja (apertura, movimientos, arqueo y cierre) y ventas de mostrador")
@RequiredArgsConstructor
public class CashSessionController {

    static final ZoneId ZONE = ZoneId.of("America/Bogota");
    private static final Set<String> SORTABLE = Set.of("id", "openedAt", "closedAt", "status");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.desc("id"));

    private final SearchCashSessionsUseCase searchSessions;
    private final FindCashSessionUseCase findSession;
    private final OpenCashSessionUseCase openSession;
    private final CloseCashSessionUseCase closeSession;
    private final AddCashMovementUseCase addMovement;
    private final PosCheckoutUseCase posCheckout;

    @Operation(summary = "Search sessions", description = "Filtros: cashRegisterId, status (OPEN | CLOSED), from/to (fecha de apertura).")
    @GetMapping("/cash_sessions")
    public PageResponse<CashSessionDto> search(
            @RequestParam(value = "cashRegisterId", required = false) Long cashRegisterId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort) {
        CashSessionFilter filter = new CashSessionFilter(cashRegisterId, status,
                from == null ? null : from.atStartOfDay(ZONE).toInstant(),
                to == null ? null : to.plusDays(1).atStartOfDay(ZONE).toInstant());
        return PageResponse.from(searchSessions.search(filter, PageableFactory.of(page, size, sort, SORTABLE, DEFAULT_SORT)));
    }

    @Operation(summary = "Get session", description = "Turno con totales por medio, movimientos y efectivo esperado.")
    @GetMapping("/cash_sessions/{id}")
    public CashSessionDto get(@PathVariable Long id) {
        return findSession.findById(id);
    }

    @Operation(summary = "Open", description = "Abre un turno con base en efectivo (uno por caja).")
    @PostMapping("/cash_sessions")
    public ResponseEntity<CashSessionDto> open(@Valid @RequestBody OpenCashSessionDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(openSession.open(input));
    }

    @Operation(summary = "Close", description = "Cierra con arqueo: efectivo contado y conteo de los demás medios.")
    @PostMapping("/cash_sessions/{id}/close")
    public CashSessionDto close(@PathVariable Long id, @Valid @RequestBody CloseCashSessionDto input) {
        return closeSession.close(id, input);
    }

    @Operation(summary = "Cash movement", description = "Entrada (IN) o salida (OUT) de efectivo con motivo.")
    @PostMapping("/cash_sessions/{id}/movements")
    public ResponseEntity<CashMovementDto> movement(@PathVariable Long id, @Valid @RequestBody CreateCashMovementDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addMovement.add(id, input));
    }

    @Operation(summary = "POS checkout", description = "Venta de mostrador: orden, líneas y pagos del turno en una transacción.")
    @PostMapping("/pos/checkout")
    public ResponseEntity<PosCheckoutResultDto> checkout(@Valid @RequestBody PosCheckoutDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(posCheckout.checkout(input));
    }
}
