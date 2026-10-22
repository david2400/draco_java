-- =============================================================================
-- V16 · Órdenes empresariales
--
--   orders            número consecutivo (ORD-000123), cliente y datos de facturación
--                     congelados, entrega (envío con dirección y geo, o retiro en
--                     tienda), transportadora, guía, costo de envío, fecha prometida,
--                     origen (tienda, bodega, vendedor, prioridad), descuento global o
--                     cupón, notas internas y para el cliente, y fechas de pago,
--                     despacho, entrega y cancelación.
--   product_orders    order_discount (parte del descuento global de la orden) y
--                     picked_quantity (unidades preparadas).
--   order_events      línea de tiempo auditable de la orden.
--   order_numbering   prefijo, relleno y siguiente número de las órdenes.
--   Las órdenes existentes reciben ORD-<id con 6 dígitos> y un evento de creación.
--
-- No destructivo e idempotente.
-- =============================================================================

SET @has_orders := (SELECT COUNT(*) FROM information_schema.TABLES
                    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders');
SET @has_lines := (SELECT COUNT(*) FROM information_schema.TABLES
                   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product_orders');

-- 1. orders ----------------------------------------------------------------------
SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders' AND COLUMN_NAME = 'order_number');
SET @sql := IF(@has_orders = 1 AND @missing = 1,
  'ALTER TABLE orders ADD COLUMN order_number VARCHAR(30) NULL,
                      ADD COLUMN customer_id BIGINT NULL,
                      ADD COLUMN customer_kind VARCHAR(10) NULL,
                      ADD COLUMN customer_doc_type VARCHAR(10) NULL,
                      ADD COLUMN customer_doc_number VARCHAR(30) NULL,
                      ADD COLUMN customer_name VARCHAR(200) NULL,
                      ADD COLUMN customer_email VARCHAR(160) NULL,
                      ADD COLUMN customer_phone VARCHAR(40) NULL,
                      ADD COLUMN billing_address VARCHAR(200) NULL,
                      ADD COLUMN billing_city VARCHAR(120) NULL,
                      ADD COLUMN delivery_method VARCHAR(12) NOT NULL DEFAULT ''SHIPPING'',
                      ADD COLUMN pickup_store_id BIGINT NULL,
                      ADD COLUMN recipient_name VARCHAR(160) NULL,
                      ADD COLUMN recipient_phone VARCHAR(40) NULL,
                      ADD COLUMN ship_address VARCHAR(200) NULL,
                      ADD COLUMN ship_address_detail VARCHAR(200) NULL,
                      ADD COLUMN ship_country_id BIGINT NULL,
                      ADD COLUMN ship_state_id BIGINT NULL,
                      ADD COLUMN ship_city_id BIGINT NULL,
                      ADD COLUMN ship_country VARCHAR(80) NULL,
                      ADD COLUMN ship_state VARCHAR(80) NULL,
                      ADD COLUMN ship_city VARCHAR(120) NULL,
                      ADD COLUMN ship_postal_code VARCHAR(20) NULL,
                      ADD COLUMN delivery_notes VARCHAR(500) NULL,
                      ADD COLUMN carrier_id BIGINT NULL,
                      ADD COLUMN tracking_number VARCHAR(80) NULL,
                      ADD COLUMN shipping_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
                      ADD COLUMN shipping_discount DECIMAL(15,2) NOT NULL DEFAULT 0,
                      ADD COLUMN promised_date DATE NULL,
                      ADD COLUMN store_id BIGINT NULL,
                      ADD COLUMN warehouse_id BIGINT NULL,
                      ADD COLUMN assigned_to VARCHAR(120) NULL,
                      ADD COLUMN priority VARCHAR(10) NOT NULL DEFAULT ''NORMAL'',
                      ADD COLUMN discount_type VARCHAR(10) NOT NULL DEFAULT ''NONE'',
                      ADD COLUMN discount_value DECIMAL(15,2) NULL,
                      ADD COLUMN coupon_id BIGINT NULL,
                      ADD COLUMN coupon_code VARCHAR(50) NULL,
                      ADD COLUMN discount_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
                      ADD COLUMN internal_notes VARCHAR(2000) NULL,
                      ADD COLUMN customer_notes VARCHAR(2000) NULL,
                      ADD COLUMN paid_at DATETIME(6) NULL,
                      ADD COLUMN shipped_at DATETIME(6) NULL,
                      ADD COLUMN delivered_at DATETIME(6) NULL,
                      ADD COLUMN cancelled_at DATETIME(6) NULL,
                      ADD CONSTRAINT ck_orders_delivery_method CHECK (delivery_method IN (''SHIPPING'', ''PICKUP'')),
                      ADD CONSTRAINT ck_orders_priority CHECK (priority IN (''LOW'', ''NORMAL'', ''HIGH'', ''URGENT'')),
                      ADD CONSTRAINT ck_orders_discount_type CHECK (discount_type IN (''NONE'', ''PERCENT'', ''AMOUNT'', ''COUPON'')),
                      ADD KEY ix_orders_customer_doc (customer_doc_number),
                      ADD KEY ix_orders_state_created (state, created_at),
                      ADD KEY ix_orders_store (store_id),
                      ADD KEY ix_orders_promised (promised_date)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Número para las órdenes existentes y único.
SET @sql := IF(@has_orders = 1,
  'UPDATE orders SET order_number = CONCAT(''ORD-'', LPAD(id_order, 6, ''0'')) WHERE order_number IS NULL', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.STATISTICS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders' AND INDEX_NAME = 'uk_orders_number');
SET @sql := IF(@has_orders = 1 AND @missing = 1,
  'ALTER TABLE orders ADD UNIQUE KEY uk_orders_number (order_number)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Fecha de pago aproximada de las órdenes ya pagadas (para los reportes).
SET @sql := IF(@has_orders = 1,
  'UPDATE orders SET paid_at = COALESCE(updated_at, created_at)
    WHERE paid_at IS NULL AND state IN (''PAID'', ''PROCESSING'', ''SHIPPED'', ''DELIVERED'')', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Bodega y tienda de las ventas de caja.
SET @has_cash := (SELECT COUNT(DISTINCT TABLE_NAME) = 2 FROM information_schema.TABLES
                  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME IN ('cash_sessions', 'cash_registers'));
SET @sql := IF(@has_orders = 1 AND @has_cash = 1,
  'UPDATE orders o
     JOIN cash_sessions s ON s.id_cash_session = o.cash_session_id
     JOIN cash_registers r ON r.id_cash_register = s.cash_register_id
      SET o.store_id = COALESCE(o.store_id, r.store_id),
          o.delivery_method = ''PICKUP''
    WHERE o.store_id IS NULL', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2. product_orders --------------------------------------------------------------
SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product_orders' AND COLUMN_NAME = 'order_discount');
SET @sql := IF(@has_lines = 1 AND @missing = 1,
  'ALTER TABLE product_orders ADD COLUMN order_discount DECIMAL(15,2) NOT NULL DEFAULT 0,
                              ADD COLUMN picked_quantity INT NOT NULL DEFAULT 0', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Las órdenes ya despachadas o entregadas quedan preparadas por completo.
SET @sql := IF(@has_orders = 1 AND @has_lines = 1,
  'UPDATE product_orders l JOIN orders o ON o.id_order = l.order_id
      SET l.picked_quantity = l.quantity
    WHERE o.state IN (''SHIPPED'', ''DELIVERED'') AND l.picked_quantity = 0', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3. order_events ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS order_events (
    id_order_event BIGINT        NOT NULL AUTO_INCREMENT,
    order_id       BIGINT        NOT NULL,
    event_type     VARCHAR(20)   NOT NULL,
    from_state     VARCHAR(20)   NULL,
    to_state       VARCHAR(20)   NULL,
    message        VARCHAR(1000) NULL,
    actor          VARCHAR(120)  NULL,
    created_at     DATETIME(6)   NOT NULL,
    PRIMARY KEY (id_order_event),
    KEY ix_order_events_order (order_id, created_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

SET @sql := IF(@has_orders = 1,
  'INSERT INTO order_events (order_id, event_type, to_state, message, created_at)
   SELECT o.id_order, ''CREATED'', ''PENDING'', NULL, COALESCE(o.created_at, NOW(6))
     FROM orders o
    WHERE NOT EXISTS (SELECT 1 FROM order_events e WHERE e.order_id = o.id_order)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 4. order_numbering -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS order_numbering (
    id          TINYINT     NOT NULL,
    prefix      VARCHAR(10) NOT NULL DEFAULT 'ORD',
    padding     INT         NOT NULL DEFAULT 6,
    next_number BIGINT      NOT NULL DEFAULT 1,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

SET @next := 1;
SET @sql := IF(@has_orders = 1, 'SET @next := (SELECT COALESCE(MAX(id_order), 0) + 1 FROM orders)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
INSERT IGNORE INTO order_numbering (id, prefix, padding, next_number) VALUES (1, 'ORD', 6, @next);
