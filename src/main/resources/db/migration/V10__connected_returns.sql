-- =============================================================================
-- V10 · Devoluciones conectadas (stock, pagos y nota crédito)
--
--   order_devolutions         bodega de reingreso, rechazo, inspección y reembolso
--                             (monto devuelto, quién, cuándo y detalle por pago).
--   order_devolution_details  SKU y nombre congelados de la línea de la orden,
--                             decisión de reingreso al stock y unidades reingresadas.
--   orders.returned_amount    valor acreditado al cliente por devoluciones: lo que
--                             se cobra de la orden pasa a ser total − devuelto.
--   invoices                  devolution_id + credit_scope (TOTAL | PARTIAL): nota
--                             crédito parcial por las líneas devueltas.
--   Catálogos base de motivos, formas de devolución y de reembolso (si no existen).
--
-- No destructivo e idempotente.
-- =============================================================================

SET @has_dev := (SELECT COUNT(*) FROM information_schema.TABLES
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'order_devolutions');
SET @has_det := (SELECT COUNT(*) FROM information_schema.TABLES
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'order_devolution_details');
SET @has_orders := (SELECT COUNT(*) FROM information_schema.TABLES
                    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders');
SET @has_lines := (SELECT COUNT(*) FROM information_schema.TABLES
                   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product_orders');
SET @has_invoices := (SELECT COUNT(*) FROM information_schema.TABLES
                      WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'invoices');

-- 1. order_devolutions -------------------------------------------------------------
SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'order_devolutions' AND COLUMN_NAME = 'refunded_amount');
SET @sql := IF(@has_dev = 1 AND @missing = 1,
  'ALTER TABLE order_devolutions ADD COLUMN warehouse_id BIGINT NULL,
                                 ADD COLUMN rejected_reason VARCHAR(255) NULL,
                                 ADD COLUMN rejected_by VARCHAR(100) NULL,
                                 ADD COLUMN rejected_at DATETIME(6) NULL,
                                 ADD COLUMN inspected_by VARCHAR(100) NULL,
                                 ADD COLUMN inspected_at DATETIME(6) NULL,
                                 ADD COLUMN refunded_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
                                 ADD COLUMN refunded_by VARCHAR(100) NULL,
                                 ADD COLUMN refunded_at DATETIME(6) NULL,
                                 ADD COLUMN refund_detail VARCHAR(500) NULL', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.STATISTICS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'order_devolutions' AND INDEX_NAME = 'ix_order_devolutions_order');
SET @sql := IF(@has_dev = 1 AND @missing = 1,
  'CREATE INDEX ix_order_devolutions_order ON order_devolutions (order_id, state)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Estados en mayúscula (el flujo usa P, A, R, I, F, X).
SET @sql := IF(@has_dev = 1, 'UPDATE order_devolutions SET state = UPPER(state) WHERE state <> UPPER(state)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2. order_devolution_details ------------------------------------------------------
SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'order_devolution_details' AND COLUMN_NAME = 'restocked_quantity');
SET @sql := IF(@has_det = 1 AND @missing = 1,
  'ALTER TABLE order_devolution_details ADD COLUMN sku_id BIGINT NULL,
                                        ADD COLUMN product_id BIGINT NULL,
                                        ADD COLUMN sku_code VARCHAR(100) NULL,
                                        ADD COLUMN product_name VARCHAR(255) NULL,
                                        ADD COLUMN restock TINYINT(1) NULL,
                                        ADD COLUMN restocked_quantity INT NOT NULL DEFAULT 0', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Detalles existentes: SKU, producto y nombre desde la línea de la orden.
SET @sql := IF(@has_det = 1 AND @has_lines = 1,
  'UPDATE order_devolution_details d
     JOIN product_orders l ON l.id_product_order = d.product_order_id
      SET d.sku_id = COALESCE(d.sku_id, l.sku_id),
          d.product_id = COALESCE(d.product_id, l.product_id),
          d.sku_code = COALESCE(d.sku_code, l.sku_code),
          d.product_name = COALESCE(d.product_name, l.product_name)
    WHERE d.product_name IS NULL OR d.product_id IS NULL', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3. orders.returned_amount --------------------------------------------------------
SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders' AND COLUMN_NAME = 'returned_amount');
SET @sql := IF(@has_orders = 1 AND @missing = 1,
  'ALTER TABLE orders ADD COLUMN returned_amount DECIMAL(15,2) NOT NULL DEFAULT 0', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 4. invoices: nota crédito por devolución -----------------------------------------
SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'invoices' AND COLUMN_NAME = 'devolution_id');
SET @sql := IF(@has_invoices = 1 AND @missing = 1,
  'ALTER TABLE invoices ADD COLUMN devolution_id BIGINT NULL,
                        ADD COLUMN credit_scope VARCHAR(10) NOT NULL DEFAULT ''TOTAL'',
                        ADD UNIQUE KEY uk_invoices_devolution (devolution_id)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 5. Catálogos base ----------------------------------------------------------------
SET @sql := IF((SELECT COUNT(*) FROM information_schema.TABLES
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'motive_devolutions') = 1,
  'INSERT INTO motive_devolutions (name, description, usr_crea, created_at, deleted)
   SELECT s.name, s.description, 1, NOW(6), 0
     FROM (SELECT ''Producto defectuoso'' name, ''No funciona o tiene fallas de fábrica'' description
           UNION ALL SELECT ''Llegó dañado'', ''Golpeado, roto o con el empaque deteriorado''
           UNION ALL SELECT ''Producto equivocado'', ''Talla, color o referencia diferente a la pedida''
           UNION ALL SELECT ''No cumple lo esperado'', ''No corresponde a la descripción o a las fotos''
           UNION ALL SELECT ''Retracto del cliente'', ''Derecho de retracto (5 días hábiles, Ley 1480)'') s
    WHERE NOT EXISTS (SELECT 1 FROM motive_devolutions m WHERE m.name = s.name)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.TABLES
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'return_methods') = 1,
  'INSERT INTO return_methods (name, description, instructions, active, usr_crea, created_at, deleted)
   SELECT s.name, s.description, s.instructions, 1, 1, NOW(6), 0
     FROM (SELECT ''Entrega en tienda'' name, ''El cliente trae el producto'' description,
                  ''Presentar el número de la orden y el producto con sus accesorios.'' instructions
           UNION ALL SELECT ''Recogida a domicilio'', ''La tienda recoge el producto'', ''Coordinar fecha y dirección con el cliente.''
           UNION ALL SELECT ''Envío por transportadora'', ''El cliente lo envía'', ''Enviar con la guía indicada; conservar el comprobante.'') s
    WHERE NOT EXISTS (SELECT 1 FROM return_methods r WHERE r.name = s.name)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(*) FROM information_schema.TABLES
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'refund_methods') = 1,
  'INSERT INTO refund_methods (name, description, policy, active, usr_crea, created_at, deleted)
   SELECT s.name, s.description, s.policy, 1, 1, NOW(6), 0
     FROM (SELECT ''Mismo medio de pago'' name, ''Se reembolsa sobre los pagos de la orden'' description,
                  ''Tarjeta y pasarelas: el banco puede tardar varios días hábiles.'' policy
           UNION ALL SELECT ''Efectivo en tienda'', ''Se entrega el dinero en caja'', ''Requiere caja abierta y firma del cliente.''
           UNION ALL SELECT ''Transferencia bancaria'', ''Se transfiere a la cuenta del cliente'', ''Pedir certificación bancaria del titular.''
           UNION ALL SELECT ''Saldo a favor'', ''Sin devolución de dinero: queda como crédito del cliente'', ''Registrar el monto de reembolso en 0.'') s
    WHERE NOT EXISTS (SELECT 1 FROM refund_methods r WHERE r.name = s.name)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
