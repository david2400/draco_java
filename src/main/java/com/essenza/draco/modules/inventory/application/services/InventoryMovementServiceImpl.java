package com.essenza.draco.modules.inventory.application.services;

import com.essenza.draco.modules.inventory.application.input.movement.RegisterEntryUseCase;
import com.essenza.draco.modules.inventory.application.input.movement.RegisterExitUseCase;
import com.essenza.draco.modules.inventory.application.input.movement.RegisterTransferUseCase;
import com.essenza.draco.modules.inventory.domain.dto.InventoryMovementDto;
import com.essenza.draco.modules.inventory.domain.dto.StockPerWarehouseDto;
import com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.inventoryMovement.InventoryMovementRepositoryAdapter;
import com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.stock.StockPerWarehouseRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryMovementServiceImpl implements RegisterEntryUseCase, RegisterExitUseCase, RegisterTransferUseCase {

    private final InventoryMovementRepositoryAdapter movementRepository;
    private final StockPerWarehouseRepositoryAdapter stockRepository;

    @Transactional
    public InventoryMovementDto registerEntry(InventoryMovementDto dto) {
        if (dto.getToWarehouseId() == null) throw new IllegalArgumentException("toWarehouseId is required for ENTRY");
        adjustStock(dto.getProductId(), dto.getToWarehouseId(), +dto.getQuantity());
        return saveMovement(dto.getProductId(), null, dto.getToWarehouseId(), "ENTRY", dto.getQuantity(), dto.getReason());
    }

    @Transactional
    public InventoryMovementDto registerExit(InventoryMovementDto dto) {
        if (dto.getFromWarehouseId() == null) throw new IllegalArgumentException("fromWarehouseId is required for EXIT");
        adjustStock(dto.getProductId(), dto.getFromWarehouseId(), -dto.getQuantity());
        return saveMovement(dto.getProductId(), dto.getFromWarehouseId(), null, "EXIT", dto.getQuantity(), dto.getReason());
    }

    @Transactional
    public InventoryMovementDto registerTransfer(InventoryMovementDto dto) {
        if (dto.getFromWarehouseId() == null || dto.getToWarehouseId() == null)
            throw new IllegalArgumentException("fromWarehouseId and toWarehouseId are required for TRANSFER");
        adjustStock(dto.getProductId(), dto.getFromWarehouseId(), -dto.getQuantity());
        adjustStock(dto.getProductId(), dto.getToWarehouseId(), +dto.getQuantity());
        return saveMovement(dto.getProductId(), dto.getFromWarehouseId(), dto.getToWarehouseId(), "TRANSFER", dto.getQuantity(), dto.getReason());
    }

    @Transactional(readOnly = true)
    public Page<InventoryMovementDto> list(Pageable pageable) {
        return movementRepository.findAll(pageable);
    }

    private void adjustStock(Long productId, Long warehouseId, int delta) {
        StockPerWarehouseDto stock = stockRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseGet(() -> {
                    StockPerWarehouseDto s = new StockPerWarehouseDto();
                    s.setProductId(productId);
                    s.setWarehouseId(warehouseId);
                    s.setQuantity(0);
                    s.setMinThreshold(0);
                    return s;
                });
        int newQty = stock.getQuantity() + delta;
        if (newQty < 0) {
            throw new IllegalArgumentException("Insufficient stock in warehouse " + warehouseId);
        }
        stock.setQuantity(newQty);
        stockRepository.save(stock);
    }

    private InventoryMovementDto saveMovement(Long productId, Long fromWarehouseId, Long toWarehouseId, String type, Integer qty, String reason) {
        InventoryMovementDto dto = new InventoryMovementDto();
        dto.setProductId(productId);
        dto.setFromWarehouseId(fromWarehouseId);
        dto.setToWarehouseId(toWarehouseId);
        dto.setType(type);
        dto.setQuantity(qty);
        dto.setReason(reason);
        return movementRepository.save(dto);
    }
}
