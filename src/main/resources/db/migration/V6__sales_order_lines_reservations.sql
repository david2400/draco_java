-- =============================================================================
-- V6 · Ventas por SKU y reservas de stock (Fase 5)
--
--   orders          total DECIMAL(15,2); stock_managed (las órdenes nuevas reservan y
--                   descuentan stock; las existentes no se tocan); cancel_reason;
--                   estado normalizado a mayúsculas.
--   product_orders  dinero DECIMAL(15,2); sku_id + foto del precio, código y nombre
--                   (sku_code, product_name, unit_price) congelada al crear la línea.
--   stock_reservations  reservas por línea y bodega (ACTIVE → COMMITTED / RELEASED /
--                   EXPIRED; COMMITTED → RETURNED si se cancela una orden pagada).
--
-- No destructivo e idempotente.
-- =============================================================================

SET @has_orders := (SELECT COUNT(*) FROM information_schema.TABLES
                    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders');
SET @has_lines := (SELECT COUNT(*) FROM information_schema.TABLES
                   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product_orders');
SET @has_skus := (SELECT COUNT(*) FROM information_schema.TABLES
                  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product_skus');
SET @has_wh := (SELECT COUNT(*) FROM information_schema.TABLES
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'warehouses');
SET @ready := (@has_orders = 1 AND @has_lines = 1 AND @has_skus = 1);

-- 1. orders ------------------------------------------------------------------------
SET @is_double := (SELECT COUNT(*) FROM information_schema.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders' AND COLUMN_NAME = 'total'
                     AND DATA_TYPE IN ('double', 'float'));
SET @sql := IF(@ready = 1 AND @is_double = 1,
  'ALTER TABLE orders MODIFY COLUMN total DECIMAL(15,2) NOT NULL DEFAULT 0', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders' AND COLUMN_NAME = 'stock_managed');
SET @sql := IF(@ready = 1 AND @missing = 1,
  'ALTER TABLE orders ADD COLUMN stock_managed TINYINT(1) NOT NULL DEFAULT 0,
                      ADD COLUMN cancel_reason VARCHAR(200) NULL', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(@ready = 1, 'UPDATE orders SET state = UPPER(TRIM(state)) WHERE state <> UPPER(TRIM(state))', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2. product_orders: dinero DECIMAL ------------------------------------------------
SET @is_double := (SELECT COUNT(*) FROM information_schema.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product_orders' AND COLUMN_NAME = 'subtotal'
                     AND DATA_TYPE IN ('double', 'float'));
SET @sql := IF(@ready = 1 AND @is_double = 1,
  'ALTER TABLE product_orders MODIFY COLUMN discount DECIMAL(15,2) NOT NULL DEFAULT 0,
                              MODIFY COLUMN subtotal DECIMAL(15,2) NOT NULL DEFAULT 0,
                              MODIFY COLUMN total DECIMAL(15,2) NOT NULL DEFAULT 0', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3. product_orders: SKU y foto de precio ------------------------------------------
SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product_orders' AND COLUMN_NAME = 'sku_id');
SET @sql := IF(@ready = 1 AND @missing = 1,
  'ALTER TABLE product_orders ADD COLUMN sku_id BIGINT NULL,
                              ADD COLUMN sku_code VARCHAR(64) NULL,
                              ADD COLUMN product_name VARCHAR(255) NULL,
                              ADD COLUMN unit_price DECIMAL(15,2) NULL,
                              ADD KEY idx_product_orders_sku (sku_id),
                              ADD CONSTRAINT fk_product_orders_sku FOREIGN KEY (sku_id) REFERENCES product_skus (id_sku)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Líneas existentes: SKU por defecto del producto (si lo tiene), nombre y precio unitario
-- deducido de la línea (subtotal / cantidad).
SET @sql := IF(@ready = 1,
  'UPDATE product_orders po
      JOIN products p ON p.id_product = po.product_id
      LEFT JOIN product_skus s ON s.id_sku = (
           SELECT s2.id_sku FROM product_skus s2
            WHERE s2.product_id = po.product_id AND s2.legacy_child_id IS NULL AND s2.deleted = 0
            ORDER BY s2.is_default DESC, s2.id_sku LIMIT 1)
      SET po.sku_id = COALESCE(po.sku_id, s.id_sku),
          po.sku_code = COALESCE(po.sku_code, s.code),
          po.product_name = COALESCE(po.product_name, LEFT(p.name, 255)),
          po.unit_price = COALESCE(po.unit_price, IF(po.quantity > 0, ROUND(po.subtotal / po.quantity, 2), po.subtotal))
    WHERE po.sku_id IS NULL OR po.product_name IS NULL OR po.unit_price IS NULL', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 4. stock_reservations ------------------------------------------------------------
SET @sql := IF(@ready = 1 AND @has_wh = 1,
  'CREATE TABLE IF NOT EXISTS stock_reservations (
       id_reservation  BIGINT      NOT NULL AUTO_INCREMENT,
       order_id        BIGINT      NOT NULL,
       order_line_id   BIGINT      NOT NULL,
       sku_id          BIGINT      NOT NULL,
       product_id      BIGINT      NOT NULL,
       warehouse_id    BIGINT      NOT NULL,
       quantity        INT         NOT NULL,
       status          VARCHAR(20) NOT NULL,
       expires_at      DATETIME(6) NULL,
       version         BIGINT      NOT NULL DEFAULT 0,
       created_at      DATETIME(6) NULL,
       updated_at      DATETIME(6) NULL,
       PRIMARY KEY (id_reservation),
       KEY idx_reservations_order (order_id),
       KEY idx_reservations_line (order_line_id),
       KEY idx_reservations_status (status, expires_at),
       KEY idx_reservations_stock (sku_id, warehouse_id),
       CONSTRAINT fk_reservations_order FOREIGN KEY (order_id) REFERENCES orders (id_order),
       CONSTRAINT fk_reservations_line FOREIGN KEY (order_line_id) REFERENCES product_orders (id_product_order),
       CONSTRAINT fk_reservations_sku FOREIGN KEY (sku_id) REFERENCES product_skus (id_sku),
       CONSTRAINT fk_reservations_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses (id_warehouse),
       CONSTRAINT ck_reservations_quantity CHECK (quantity >= 0),
       CONSTRAINT ck_reservations_status CHECK (status IN (''ACTIVE'', ''COMMITTED'', ''RELEASED'', ''EXPIRED'', ''RETURNED''))
   ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
