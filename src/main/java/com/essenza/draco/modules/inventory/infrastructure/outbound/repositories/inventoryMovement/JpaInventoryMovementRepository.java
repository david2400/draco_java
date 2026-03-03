package com.essenza.draco.modules.inventory.infrastructure.outbound.repositories.inventoryMovement;

import com.essenza.draco.modules.inventory.infrastructure.outbound.persistence.mysql.shop.InventoryMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaInventoryMovementRepository extends JpaRepository<InventoryMovementEntity, Long> {
}
