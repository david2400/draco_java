package com.essenza.draco.modules.inventory.application.services.warehouse;

import com.essenza.draco.modules.inventory.application.input.warehouse.DeleteWarehouseUseCase;
import com.essenza.draco.modules.inventory.application.output.repository.StockItemRepository;
import com.essenza.draco.modules.inventory.application.output.repository.WarehouseInsights;
import com.essenza.draco.modules.inventory.application.output.repository.WarehouseRepository;
import com.essenza.draco.modules.inventory.domain.model.warehouse.Warehouse;
import com.essenza.draco.shared.common.domain.dto.BulkOperationResult;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

/** Borrado lógico protegido: ni la principal, ni con stock, ni si la usan tiendas o cajas. */
@Service
public class DeleteWarehouseService implements DeleteWarehouseUseCase {

    private final WarehouseRepository warehouses;
    private final StockItemRepository stock;
    private final WarehouseInsights insights;
    private final WarehouseSoporte soporte;
    private final TransactionTemplate tx;

    public DeleteWarehouseService(WarehouseRepository warehouses, StockItemRepository stock, WarehouseInsights insights,
                                  WarehouseSoporte soporte, PlatformTransactionManager transactionManager) {
        this.warehouses = warehouses;
        this.stock = stock;
        this.insights = insights;
        this.soporte = soporte;
        this.tx = new TransactionTemplate(transactionManager);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Warehouse w = soporte.warehouse(id);
        if (w.main()) {
            throw new ConflictException("La bodega principal no se puede eliminar: marca otra como principal primero.");
        }
        long units = stock.totalOnHandInWarehouse(id);
        if (units > 0) {
            throw new ConflictException("No se puede eliminar la bodega: tiene " + units
                    + " unidad(es) en stock. Transfiérelas a otra bodega primero.");
        }
        int stores = insights.stores(id).size();
        int registers = (int) insights.registers(id).stream().filter(WarehouseInsights.RegisterLink::own).count();
        if (stores > 0 || registers > 0) {
            throw new ConflictException("La bodega " + w.name() + " está asignada a " + stores + " tienda(s) y "
                    + registers + " caja(s): cámbialas o desactiva la bodega en lugar de eliminarla.");
        }
        warehouses.delete(id);
    }

    /** Cada bodega en su propia transacción: un fallo no revierte las demás. */
    @Override
    public BulkOperationResult deleteAll(List<Long> ids) {
        List<Long> unique = ids.stream().distinct().toList();
        BulkOperationResult.Builder result = new BulkOperationResult.Builder(unique.size());
        for (Long id : unique) {
            try {
                tx.executeWithoutResult(status -> delete(id));
                result.success();
            } catch (NotFoundException | ConflictException ex) {
                result.failure(id, ex.getMessage());
            }
        }
        return result.build();
    }
}
