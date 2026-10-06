-- =============================================================================
-- V4 · Inventario por SKU y bodega (Fase 3)
--
-- Fuente de verdad acordada: el stock por bodega (stock_per_warehouse). Si el
-- stock que muestra la ficha (products.stock / product_childs.stock) es mayor
-- que lo cubierto por las bodegas, la diferencia entra en la bodega principal
-- como "ajuste de apertura" y queda registrada en inventory_movements.
-- Si las bodegas tienen más que la ficha, mandan las bodegas.
--
-- No destructivo e idempotente; en base vacía no hace nada.
-- =============================================================================

SET @has_skus := (SELECT COUNT(*) FROM information_schema.TABLES
                  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product_skus');
SET @has_wh := (SELECT COUNT(*) FROM information_schema.TABLES
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'warehouses');
SET @has_spw := (SELECT COUNT(*) FROM information_schema.TABLES
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'stock_per_warehouse');
SET @has_mov := (SELECT COUNT(*) FROM information_schema.TABLES
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'inventory_movements');
SET @ready := (@has_skus = 1 AND @has_wh = 1);

-- 1. warehouses.is_main ---------------------------------------------------------
SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'warehouses' AND COLUMN_NAME = 'is_main');
SET @sql := IF(@has_wh = 1 AND @missing = 1, 'ALTER TABLE warehouses ADD COLUMN is_main BIT(1) NOT NULL DEFAULT 0', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2. inventory_movements: sku_id, reference_type, reference_id -------------------
SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'inventory_movements' AND COLUMN_NAME = 'sku_id');
SET @sql := IF(@has_mov = 1 AND @missing = 1,
  'ALTER TABLE inventory_movements ADD COLUMN sku_id BIGINT NULL, ADD COLUMN reference_type VARCHAR(30) NULL, ADD COLUMN reference_id BIGINT NULL', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3. stock_items ------------------------------------------------------------------
SET @sql := IF(@ready = 1,
  'CREATE TABLE IF NOT EXISTS stock_items (
       id_stock_item  BIGINT      NOT NULL AUTO_INCREMENT,
       sku_id         BIGINT      NOT NULL,
       product_id     BIGINT      NOT NULL,
       warehouse_id   BIGINT      NOT NULL,
       on_hand        INT         NOT NULL DEFAULT 0,
       reserved       INT         NOT NULL DEFAULT 0,
       min_threshold  INT         NOT NULL DEFAULT 0,
       version        BIGINT      NOT NULL DEFAULT 0,
       usr_crea       INT         NOT NULL,
       usr_mod        INT         NULL,
       created_at     DATETIME(6) NULL,
       updated_at     DATETIME(6) NULL,
       deleted        TINYINT(1)  NOT NULL DEFAULT 0,
       PRIMARY KEY (id_stock_item),
       CONSTRAINT uk_stock_items_sku_warehouse UNIQUE (sku_id, warehouse_id),
       KEY idx_stock_items_product (product_id),
       KEY idx_stock_items_warehouse (warehouse_id),
       CONSTRAINT fk_stock_items_sku FOREIGN KEY (sku_id) REFERENCES product_skus (id_sku),
       CONSTRAINT fk_stock_items_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses (id_warehouse),
       CONSTRAINT ck_stock_items_quantities CHECK (on_hand >= 0 AND reserved >= 0 AND reserved <= on_hand)
   ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- SKU destino del stock "a nivel producto" (bodegas y ficha):
--   producto sin variantes -> su SKU por defecto; con variantes -> la variante predeterminada.
SET @target := '(SELECT s.product_id, MIN(s.id_sku) AS id_sku
                 FROM product_skus s JOIN products p ON p.id_product = s.product_id
                 WHERE s.deleted = 0 AND (
                       (p.product_type <> ''VARIANT'' AND s.legacy_child_id IS NULL)
                    OR (p.product_type = ''VARIANT'' AND s.legacy_child_id IS NOT NULL AND s.is_default = 1))
                 GROUP BY s.product_id)';

-- 4. Stock por bodega (verdad) -> stock_items --------------------------------------
SET @sql := IF(@ready = 1 AND @has_spw = 1, CONCAT(
  'INSERT INTO stock_items (sku_id, product_id, warehouse_id, on_hand, reserved, min_threshold, version, usr_crea, created_at, deleted)
   SELECT t.id_sku, w.product_id, w.warehouse_id, w.quantity, 0, w.min_threshold, 0, 1, NOW(6), 0
   FROM stock_per_warehouse w JOIN ', @target, ' t ON t.product_id = w.product_id
   WHERE w.deleted = 0 AND w.quantity > 0
     AND NOT EXISTS (SELECT 1 FROM stock_items i WHERE i.sku_id = t.id_sku AND i.warehouse_id = w.warehouse_id)'), 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 5. Diferencias (ficha > bodegas) -> ajuste de apertura en la bodega principal -----
--    Stock esperado por SKU: variante -> product_childs.stock; SKU por defecto de
--    un producto sin variantes -> products.stock.
DROP TEMPORARY TABLE IF EXISTS tmp_opening;
SET @sql := IF(@ready = 1,
  'CREATE TEMPORARY TABLE tmp_opening AS
   SELECT x.id_sku, x.product_id, x.expected - COALESCE(cov.covered, 0) AS diff
   FROM (
       SELECT s.id_sku, s.product_id,
              CASE WHEN s.legacy_child_id IS NOT NULL THEN COALESCE(c.stock, 0)
                   WHEN p.product_type = ''VARIANT'' THEN 0
                   ELSE COALESCE(p.stock, 0) END AS expected
       FROM product_skus s
       JOIN products p ON p.id_product = s.product_id AND p.deleted = 0
       LEFT JOIN product_childs c ON c.id_product_child = s.legacy_child_id AND c.deleted = 0
       WHERE s.deleted = 0
   ) x
   LEFT JOIN (SELECT sku_id, SUM(on_hand) AS covered FROM stock_items WHERE deleted = 0 GROUP BY sku_id) cov
          ON cov.sku_id = x.id_sku
   WHERE x.expected - COALESCE(cov.covered, 0) > 0', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @pending := 0;
SET @sql := IF(@ready = 1, 'SELECT COUNT(*) INTO @pending FROM tmp_opening', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 5a. Bodega principal: la marcada; si no hay, la primera activa; si no existe ninguna y
--     hay stock por ubicar, se crea "Bodega principal" (código PRINCIPAL).
SET @wh_count := 0;
SET @sql := IF(@has_wh = 1, 'SELECT COUNT(*) INTO @wh_count FROM warehouses WHERE deleted = 0', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @sql := IF(@ready = 1 AND @pending > 0 AND @wh_count = 0,
  'INSERT INTO warehouses (name, code, address, active, is_main, usr_crea, created_at, deleted)
   VALUES (''Bodega principal'', ''PRINCIPAL'', NULL, 1, 1, 1, NOW(6), 0)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @main_count := 0;
SET @sql := IF(@has_wh = 1, 'SELECT COUNT(*) INTO @main_count FROM warehouses WHERE is_main = 1 AND deleted = 0', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @sql := IF(@has_wh = 1 AND @main_count = 0,
  'UPDATE warehouses SET is_main = 1 WHERE id_warehouse = (SELECT id FROM (
       SELECT id_warehouse AS id FROM warehouses WHERE deleted = 0 ORDER BY active DESC, id_warehouse LIMIT 1) w)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @main := NULL;
SET @sql := IF(@has_wh = 1, 'SELECT id_warehouse INTO @main FROM warehouses WHERE is_main = 1 AND deleted = 0 ORDER BY id_warehouse LIMIT 1', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 5b. Ledger: un movimiento ENTRY por SKU ajustado
SET @sql := IF(@ready = 1 AND @pending > 0 AND @has_mov = 1,
  'INSERT INTO inventory_movements (product_id, sku_id, from_warehouse_id, to_warehouse_id, type, quantity, reason,
                                    reference_type, usr_crea, created_at, deleted)
   SELECT o.product_id, o.id_sku, NULL, @main, ''ENTRY'', o.diff,
          ''Ajuste de apertura: stock de la ficha no ubicado en bodegas (migración Fase 3)'', ''MIGRATION'', 1, NOW(6), 0
   FROM tmp_opening o', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 5c. Existencias en la bodega principal
SET @sql := IF(@ready = 1 AND @pending > 0,
  'INSERT INTO stock_items (sku_id, product_id, warehouse_id, on_hand, reserved, min_threshold, version, usr_crea, created_at, deleted)
   SELECT o.id_sku, o.product_id, @main, o.diff, 0, 0, 0, 1, NOW(6), 0 FROM tmp_opening o
   ON DUPLICATE KEY UPDATE on_hand = on_hand + VALUES(on_hand)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
DROP TEMPORARY TABLE IF EXISTS tmp_opening;

-- 6. Movimientos anteriores: asociarlos al SKU destino de su producto ------------------
SET @sql := IF(@ready = 1 AND @has_mov = 1, CONCAT(
  'UPDATE inventory_movements m JOIN ', @target, ' t ON t.product_id = m.product_id
   SET m.sku_id = t.id_sku WHERE m.sku_id IS NULL'), 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 7. Proyección heredada: columnas de la ficha = suma del inventario --------------------
SET @sql := IF(@ready = 1,
  'UPDATE product_childs c JOIN product_skus s ON s.legacy_child_id = c.id_product_child
   SET c.stock = (SELECT COALESCE(SUM(i.on_hand), 0) FROM stock_items i WHERE i.sku_id = s.id_sku AND i.deleted = 0)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @sql := IF(@ready = 1,
  'UPDATE products p SET p.stock = (SELECT COALESCE(SUM(i.on_hand), 0) FROM stock_items i
       JOIN product_skus s ON s.id_sku = i.sku_id AND s.deleted = 0
       WHERE i.product_id = p.id_product AND i.deleted = 0)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @sql := IF(@ready = 1 AND @has_spw = 1,
  'INSERT INTO stock_per_warehouse (product_id, warehouse_id, quantity, min_threshold, usr_crea, created_at, deleted)
   SELECT i.product_id, i.warehouse_id, SUM(i.on_hand), MAX(i.min_threshold), 1, NOW(6), 0
   FROM stock_items i JOIN product_skus s ON s.id_sku = i.sku_id AND s.deleted = 0
   WHERE i.deleted = 0 GROUP BY i.product_id, i.warehouse_id
   ON DUPLICATE KEY UPDATE quantity = VALUES(quantity)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
