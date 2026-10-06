package com.essenza.draco.modules.inventory.infrastructure.inbound.rest;

import com.essenza.draco.modules.inventory.application.dto.StockItemDto;
import com.essenza.draco.modules.inventory.application.input.stock.FindStockUseCase;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Existencias por SKU y bodega (Fase 3, solo lectura). */
@RestController
@RequestMapping("/inventory/stock")
public class StockController {

    private final FindStockUseCase findStock;

    public StockController(FindStockUseCase findStock) {
        this.findStock = findStock;
    }

    @Operation(summary = "Stock por SKU y bodega", description = "Filtros opcionales: sku_id, product_id, warehouse_id")
    @GetMapping
    public List<StockItemDto> find(@RequestParam(value = "sku_id", required = false) Long skuId,
                                   @RequestParam(value = "product_id", required = false) Long productId,
                                   @RequestParam(value = "warehouse_id", required = false) Long warehouseId) {
        return findStock.find(skuId, productId, warehouseId);
    }
}
