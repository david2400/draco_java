-- =============================================================================
-- V11 · Puntos de venta (tiendas) y configuración de cajas
--
--   stores                  sede/tienda: código, dirección y contacto, bodega de la que
--                           salen las ventas de mostrador, horario y textos del ticket.
--   cash_registers          store_id (cada caja pertenece a una tienda) y reglas:
--                           base de apertura (libre, sugerida o fija), tope de efectivo,
--                           arqueo ciego, tolerancia de diferencia, descuento máximo,
--                           factura automática, ancho del ticket y equipo.
--   cash_register_methods   medios de pago permitidos por caja (sin filas = todos).
--   Se crea "Tienda principal" con la bodega principal y se le asignan las cajas existentes.
--
-- No destructivo e idempotente.
-- =============================================================================

SET @has_registers := (SELECT COUNT(*) FROM information_schema.TABLES
                       WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'cash_registers');
SET @has_warehouses := (SELECT COUNT(*) FROM information_schema.TABLES
                        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'warehouses');
SET @has_methods := (SELECT COUNT(*) FROM information_schema.TABLES
                     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'payment_types');

-- 1. stores ------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS stores (
    id_store        BIGINT        NOT NULL AUTO_INCREMENT,
    code            VARCHAR(30)   NOT NULL,
    name            VARCHAR(120)  NOT NULL,
    address         VARCHAR(200)  NULL,
    city            VARCHAR(80)   NULL,
    phone           VARCHAR(40)   NULL,
    email           VARCHAR(120)  NULL,
    warehouse_id    BIGINT        NULL,
    opening_hours   VARCHAR(255)  NULL,
    ticket_header   VARCHAR(500)  NULL,
    ticket_footer   VARCHAR(500)  NULL,
    active          TINYINT(1)    NOT NULL DEFAULT 1,
    usr_crea        INT           NOT NULL,
    usr_mod         INT           NULL,
    created_at      DATETIME(6)   NULL,
    updated_at      DATETIME(6)   NULL,
    deleted         TINYINT(1)    NOT NULL DEFAULT 0,
    code_active     VARCHAR(30)   GENERATED ALWAYS AS (IF(deleted = 0, code, NULL)) STORED,
    PRIMARY KEY (id_store),
    UNIQUE KEY uk_stores_code_active (code_active)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

SET @sql := IF(@has_warehouses = 1 AND (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'stores' AND CONSTRAINT_NAME = 'fk_stores_warehouse') = 0,
  'ALTER TABLE stores ADD CONSTRAINT fk_stores_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses (id_warehouse)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Tienda principal con la bodega principal (o la primera).
SET @sql := IF(@has_warehouses = 1,
  'INSERT INTO stores (code, name, warehouse_id, ticket_footer, active, usr_crea, created_at, deleted)
   SELECT ''TIENDA_1'', ''Tienda principal'',
          (SELECT w.id_warehouse FROM warehouses w WHERE w.deleted = 0 ORDER BY w.is_main DESC, w.id_warehouse LIMIT 1),
          ''¡Gracias por tu compra!'', 1, 1, NOW(6), 0 FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM stores WHERE deleted = 0)',
  'INSERT INTO stores (code, name, ticket_footer, active, usr_crea, created_at, deleted)
   SELECT ''TIENDA_1'', ''Tienda principal'', ''¡Gracias por tu compra!'', 1, 1, NOW(6), 0 FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM stores WHERE deleted = 0)');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2. cash_registers: tienda y reglas -----------------------------------------------
SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'cash_registers' AND COLUMN_NAME = 'store_id');
SET @sql := IF(@has_registers = 1 AND @missing = 1,
  'ALTER TABLE cash_registers ADD COLUMN store_id BIGINT NULL,
                              ADD COLUMN device_name VARCHAR(80) NULL,
                              ADD COLUMN opening_mode VARCHAR(10) NOT NULL DEFAULT ''FREE'',
                              ADD COLUMN default_opening_amount DECIMAL(15,2) NULL,
                              ADD COLUMN cash_limit DECIMAL(15,2) NULL,
                              ADD COLUMN blind_close TINYINT(1) NOT NULL DEFAULT 0,
                              ADD COLUMN difference_tolerance DECIMAL(15,2) NOT NULL DEFAULT 0,
                              ADD COLUMN max_discount_percent DECIMAL(5,2) NULL,
                              ADD COLUMN auto_invoice TINYINT(1) NOT NULL DEFAULT 0,
                              ADD COLUMN receipt_width VARCHAR(5) NOT NULL DEFAULT ''80MM'',
                              ADD CONSTRAINT fk_cash_registers_store FOREIGN KEY (store_id) REFERENCES stores (id_store),
                              ADD CONSTRAINT ck_cash_registers_opening CHECK (opening_mode IN (''FREE'', ''SUGGESTED'', ''FIXED'')),
                              ADD CONSTRAINT ck_cash_registers_receipt CHECK (receipt_width IN (''58MM'', ''80MM''))', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(@has_registers = 1,
  'UPDATE cash_registers SET store_id = (SELECT s.id_store FROM stores s WHERE s.deleted = 0 ORDER BY s.id_store LIMIT 1)
    WHERE store_id IS NULL', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3. Medios de pago permitidos por caja --------------------------------------------
CREATE TABLE IF NOT EXISTS cash_register_methods (
    cash_register_id   BIGINT NOT NULL,
    payment_method_id  BIGINT NOT NULL,
    PRIMARY KEY (cash_register_id, payment_method_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

SET @sql := IF(@has_registers = 1 AND (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'cash_register_methods'
                   AND CONSTRAINT_NAME = 'fk_register_methods_register') = 0,
  'ALTER TABLE cash_register_methods ADD CONSTRAINT fk_register_methods_register
     FOREIGN KEY (cash_register_id) REFERENCES cash_registers (id_cash_register)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(@has_methods = 1 AND (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'cash_register_methods'
                   AND CONSTRAINT_NAME = 'fk_register_methods_method') = 0,
  'ALTER TABLE cash_register_methods ADD CONSTRAINT fk_register_methods_method
     FOREIGN KEY (payment_method_id) REFERENCES payment_types (id_payment_type)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
