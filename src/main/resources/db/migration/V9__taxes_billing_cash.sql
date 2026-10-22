-- =============================================================================
-- V9 · Impuestos, facturación electrónica y caja (punto de venta)
--
--   tax_rates          tarifas (IVA 19 %, IVA 5 %, IVA exento 0 %, excluido, INC 8 %);
--                      una por defecto. products.tax_rate_id (NULL = la por defecto).
--   product_orders     impuesto congelado por línea: tarifa, base y valor. Los precios
--                      incluyen IVA: base = total / (1 + tarifa), impuesto = total − base.
--   orders             tax_amount, net_amount (suma de bases), channel (BACKOFFICE | POS |
--                      ONLINE) y cash_session_id.
--   billing_settings   datos del emisor (NIT, razón social, régimen…), ambiente,
--                      proveedor, software y facturación automática al pagar.
--   billing_resolutions numeración autorizada por la DIAN (prefijo, rango, vigencia,
--                      clave técnica) para facturas y notas crédito.
--   invoices / invoice_lines  facturas y notas crédito con CUFE/CUDE, comprador,
--                      totales e impuestos por línea. Una factura vigente por orden.
--   cash_registers / cash_sessions / cash_session_counts / cash_movements  cajas,
--                      turnos (apertura con base, cierre con arqueo por medio de pago)
--                      y entradas/salidas de efectivo. payments.cash_session_id.
--
-- No destructivo e idempotente.
-- =============================================================================

SET @has_orders := (SELECT COUNT(*) FROM information_schema.TABLES
                    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders');
SET @has_lines := (SELECT COUNT(*) FROM information_schema.TABLES
                   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product_orders');
SET @has_products := (SELECT COUNT(*) FROM information_schema.TABLES
                      WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'products');
SET @has_payments := (SELECT COUNT(*) FROM information_schema.TABLES
                      WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'payments');
SET @ready := (@has_orders = 1 AND @has_lines = 1 AND @has_products = 1);

-- 1. tax_rates ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tax_rates (
    id_tax_rate   BIGINT        NOT NULL AUTO_INCREMENT,
    code          VARCHAR(30)   NOT NULL,
    name          VARCHAR(120)  NOT NULL,
    description   VARCHAR(255)  NULL,
    kind          VARCHAR(20)   NOT NULL,
    rate          DECIMAL(6,3)  NOT NULL DEFAULT 0,
    is_default    TINYINT(1)    NOT NULL DEFAULT 0,
    active        TINYINT(1)    NOT NULL DEFAULT 1,
    position      INT           NOT NULL DEFAULT 0,
    usr_crea      INT           NOT NULL,
    usr_mod       INT           NULL,
    created_at    DATETIME(6)   NULL,
    updated_at    DATETIME(6)   NULL,
    deleted       TINYINT(1)    NOT NULL DEFAULT 0,
    code_active   VARCHAR(30)   GENERATED ALWAYS AS (IF(deleted = 0, code, NULL)) STORED,
    PRIMARY KEY (id_tax_rate),
    UNIQUE KEY uk_tax_rates_code_active (code_active),
    CONSTRAINT ck_tax_rates_rate CHECK (rate >= 0 AND rate <= 100),
    CONSTRAINT ck_tax_rates_kind CHECK (kind IN ('IVA', 'INC', 'EXCLUDED'))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

INSERT INTO tax_rates (code, name, description, kind, rate, is_default, active, position, usr_crea, created_at, deleted)
SELECT s.code, s.name, s.description, s.kind, s.rate, s.is_default, 1, s.position, 1, NOW(6), 0
  FROM (
    SELECT 'IVA_19' code, 'IVA 19 %' name, 'Tarifa general del IVA' description, 'IVA' kind, 19.000 rate, 1 is_default, 10 position
    UNION ALL SELECT 'IVA_5', 'IVA 5 %', 'Tarifa diferencial del IVA', 'IVA', 5.000, 0, 20
    UNION ALL SELECT 'IVA_0', 'IVA exento (0 %)', 'Bienes y servicios exentos: se facturan con IVA 0 %', 'IVA', 0.000, 0, 30
    UNION ALL SELECT 'EXCLUIDO', 'Excluido de IVA', 'No causan IVA', 'EXCLUDED', 0.000, 0, 40
    UNION ALL SELECT 'INC_8', 'Impuesto al consumo 8 %', 'Impuesto nacional al consumo (restaurantes, bares…)', 'INC', 8.000, 0, 50
  ) s
 WHERE NOT EXISTS (SELECT 1 FROM tax_rates t WHERE t.code = s.code AND t.deleted = 0);

-- 2. products.tax_rate_id ----------------------------------------------------------
SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'products' AND COLUMN_NAME = 'tax_rate_id');
SET @sql := IF(@has_products = 1 AND @missing = 1,
  'ALTER TABLE products ADD COLUMN tax_rate_id BIGINT NULL,
                        ADD CONSTRAINT fk_products_tax_rate FOREIGN KEY (tax_rate_id) REFERENCES tax_rates (id_tax_rate)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3. product_orders: impuesto congelado --------------------------------------------
SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product_orders' AND COLUMN_NAME = 'tax_amount');
SET @sql := IF(@ready = 1 AND @missing = 1,
  'ALTER TABLE product_orders ADD COLUMN tax_rate_id BIGINT NULL,
                              ADD COLUMN tax_code VARCHAR(30) NULL,
                              ADD COLUMN tax_kind VARCHAR(20) NULL,
                              ADD COLUMN tax_rate DECIMAL(6,3) NULL,
                              ADD COLUMN tax_base DECIMAL(15,2) NULL,
                              ADD COLUMN tax_amount DECIMAL(15,2) NULL', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Líneas existentes: tarifa del producto o la por defecto (los precios incluyen IVA).
SET @sql := IF(@ready = 1,
  'UPDATE product_orders l
     JOIN products p ON p.id_product = l.product_id
     JOIN tax_rates t ON t.id_tax_rate = COALESCE(p.tax_rate_id,
            (SELECT d.id_tax_rate FROM tax_rates d WHERE d.is_default = 1 AND d.deleted = 0 ORDER BY d.id_tax_rate LIMIT 1))
      SET l.tax_rate_id = t.id_tax_rate, l.tax_code = t.code, l.tax_kind = t.kind, l.tax_rate = t.rate,
          l.tax_base = ROUND(l.total / (1 + t.rate / 100), 2),
          l.tax_amount = l.total - ROUND(l.total / (1 + t.rate / 100), 2)
    WHERE l.tax_amount IS NULL', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 4. orders: impuestos, canal y caja -----------------------------------------------
SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders' AND COLUMN_NAME = 'tax_amount');
SET @sql := IF(@ready = 1 AND @missing = 1,
  'ALTER TABLE orders ADD COLUMN tax_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
                      ADD COLUMN net_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
                      ADD COLUMN channel VARCHAR(20) NOT NULL DEFAULT ''BACKOFFICE'',
                      ADD COLUMN cash_session_id BIGINT NULL', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(@ready = 1,
  'UPDATE orders o
      SET o.tax_amount = COALESCE((SELECT SUM(l.tax_amount) FROM product_orders l WHERE l.order_id = o.id_order AND l.deleted = 0), 0),
          o.net_amount = COALESCE((SELECT SUM(COALESCE(l.tax_base, l.total)) FROM product_orders l WHERE l.order_id = o.id_order AND l.deleted = 0), 0)
    WHERE o.tax_amount = 0 AND o.net_amount = 0', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 5. billing_settings (una fila) -----------------------------------------------------
CREATE TABLE IF NOT EXISTS billing_settings (
    id_billing_settings BIGINT       NOT NULL,
    nit                 VARCHAR(20)  NULL,
    legal_name          VARCHAR(200) NULL,
    trade_name          VARCHAR(200) NULL,
    tax_regime          VARCHAR(30)  NOT NULL DEFAULT 'RESPONSABLE_IVA',
    responsibilities    VARCHAR(120) NULL,
    address             VARCHAR(200) NULL,
    city                VARCHAR(120) NULL,
    department          VARCHAR(120) NULL,
    email               VARCHAR(160) NULL,
    phone               VARCHAR(40)  NULL,
    environment         VARCHAR(20)  NOT NULL DEFAULT 'TEST',
    provider            VARCHAR(30)  NOT NULL DEFAULT 'SIMULATOR',
    software_id         VARCHAR(80)  NULL,
    software_pin        VARCHAR(40)  NULL,
    auto_invoice        TINYINT(1)   NOT NULL DEFAULT 0,
    invoice_footer      VARCHAR(500) NULL,
    usr_crea            INT          NOT NULL,
    usr_mod             INT          NULL,
    created_at          DATETIME(6)  NULL,
    updated_at          DATETIME(6)  NULL,
    deleted             TINYINT(1)   NOT NULL DEFAULT 0,
    PRIMARY KEY (id_billing_settings)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

INSERT INTO billing_settings (id_billing_settings, tax_regime, environment, provider, auto_invoice, usr_crea, created_at, deleted)
SELECT 1, 'RESPONSABLE_IVA', 'TEST', 'SIMULATOR', 0, 1, NOW(6), 0 FROM DUAL
 WHERE NOT EXISTS (SELECT 1 FROM billing_settings WHERE id_billing_settings = 1);

-- 6. billing_resolutions --------------------------------------------------------------
CREATE TABLE IF NOT EXISTS billing_resolutions (
    id_billing_resolution BIGINT       NOT NULL AUTO_INCREMENT,
    kind                  VARCHAR(20)  NOT NULL,
    prefix                VARCHAR(10)  NOT NULL DEFAULT '',
    resolution_number     VARCHAR(40)  NULL,
    resolution_date       DATE         NULL,
    technical_key         VARCHAR(120) NULL,
    from_number           BIGINT       NOT NULL,
    to_number             BIGINT       NOT NULL,
    next_number           BIGINT       NOT NULL,
    valid_from            DATE         NULL,
    valid_to              DATE         NULL,
    environment           VARCHAR(20)  NOT NULL DEFAULT 'TEST',
    active                TINYINT(1)   NOT NULL DEFAULT 1,
    version               BIGINT       NOT NULL DEFAULT 0,
    usr_crea              INT          NOT NULL,
    usr_mod               INT          NULL,
    created_at            DATETIME(6)  NULL,
    updated_at            DATETIME(6)  NULL,
    deleted               TINYINT(1)   NOT NULL DEFAULT 0,
    PRIMARY KEY (id_billing_resolution),
    KEY idx_billing_resolutions_kind (kind, environment, active),
    CONSTRAINT ck_billing_resolutions_kind CHECK (kind IN ('INVOICE', 'CREDIT_NOTE')),
    CONSTRAINT ck_billing_resolutions_range CHECK (from_number >= 0 AND to_number >= from_number
                                                   AND next_number >= from_number AND next_number <= to_number + 1)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- Numeración del set de pruebas de habilitación de la DIAN (ambiente de pruebas).
INSERT INTO billing_resolutions (kind, prefix, resolution_number, resolution_date, technical_key, from_number, to_number,
                                 next_number, valid_from, valid_to, environment, active, usr_crea, created_at, deleted)
SELECT 'INVOICE', 'SETT', '18760000001', '2019-01-19', 'fc8eac422eba16e22ffd8c6f94b3f40a6e38162c', 1, 5000000, 1,
       '2019-01-19', '2030-01-19', 'TEST', 1, 1, NOW(6), 0 FROM DUAL
 WHERE NOT EXISTS (SELECT 1 FROM billing_resolutions WHERE kind = 'INVOICE' AND deleted = 0);
INSERT INTO billing_resolutions (kind, prefix, resolution_number, from_number, to_number, next_number, environment, active,
                                 usr_crea, created_at, deleted)
SELECT 'CREDIT_NOTE', 'NC', NULL, 1, 99999999, 1, 'TEST', 1, 1, NOW(6), 0 FROM DUAL
 WHERE NOT EXISTS (SELECT 1 FROM billing_resolutions WHERE kind = 'CREDIT_NOTE' AND deleted = 0);

-- 7. invoices / invoice_lines ------------------------------------------------------------
SET @sql := IF(@ready = 1,
  'CREATE TABLE IF NOT EXISTS invoices (
       id_invoice          BIGINT        NOT NULL AUTO_INCREMENT,
       kind                VARCHAR(20)   NOT NULL,
       order_id            BIGINT        NOT NULL,
       related_invoice_id  BIGINT        NULL,
       resolution_id       BIGINT        NOT NULL,
       prefix              VARCHAR(10)   NOT NULL DEFAULT '''',
       number              BIGINT        NOT NULL,
       full_number         VARCHAR(30)   NOT NULL,
       issued_at           DATETIME(6)   NOT NULL,
       status              VARCHAR(20)   NOT NULL,
       environment         VARCHAR(20)   NOT NULL,
       buyer_doc_type      VARCHAR(10)   NOT NULL,
       buyer_doc_number    VARCHAR(30)   NOT NULL,
       buyer_dv            VARCHAR(2)    NULL,
       buyer_name          VARCHAR(200)  NOT NULL,
       buyer_email         VARCHAR(160)  NULL,
       buyer_phone         VARCHAR(40)   NULL,
       buyer_address       VARCHAR(200)  NULL,
       buyer_city          VARCHAR(120)  NULL,
       net_amount          DECIMAL(15,2) NOT NULL,
       tax_amount          DECIMAL(15,2) NOT NULL,
       discount_amount     DECIMAL(15,2) NOT NULL DEFAULT 0,
       total               DECIMAL(15,2) NOT NULL,
       currency            VARCHAR(3)    NOT NULL DEFAULT ''COP'',
       payment_form        VARCHAR(20)   NOT NULL DEFAULT ''CASH'',
       payment_means       VARCHAR(200)  NULL,
       cufe                VARCHAR(120)  NULL,
       qr_data             TEXT          NULL,
       provider            VARCHAR(30)   NOT NULL,
       provider_reference  VARCHAR(120)  NULL,
       status_detail       VARCHAR(500)  NULL,
       reason              VARCHAR(500)  NULL,
       version             BIGINT        NOT NULL DEFAULT 0,
       usr_crea            INT           NOT NULL,
       usr_mod             INT           NULL,
       created_at          DATETIME(6)   NULL,
       updated_at          DATETIME(6)   NULL,
       deleted             TINYINT(1)    NOT NULL DEFAULT 0,
       active_order_invoice BIGINT GENERATED ALWAYS AS
           (IF(kind = ''INVOICE'' AND status <> ''VOIDED'' AND deleted = 0, order_id, NULL)) STORED,
       PRIMARY KEY (id_invoice),
       UNIQUE KEY uk_invoices_number (kind, prefix, number),
       UNIQUE KEY uk_invoices_active_order (active_order_invoice),
       KEY idx_invoices_order (order_id),
       KEY idx_invoices_status_issued (status, issued_at),
       CONSTRAINT fk_invoices_order FOREIGN KEY (order_id) REFERENCES orders (id_order),
       CONSTRAINT fk_invoices_related FOREIGN KEY (related_invoice_id) REFERENCES invoices (id_invoice),
       CONSTRAINT fk_invoices_resolution FOREIGN KEY (resolution_id) REFERENCES billing_resolutions (id_billing_resolution),
       CONSTRAINT ck_invoices_kind CHECK (kind IN (''INVOICE'', ''CREDIT_NOTE'')),
       CONSTRAINT ck_invoices_status CHECK (status IN (''PENDING'', ''ISSUED'', ''REJECTED'', ''VOIDED''))
   ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(@ready = 1,
  'CREATE TABLE IF NOT EXISTS invoice_lines (
       id_invoice_line  BIGINT        NOT NULL AUTO_INCREMENT,
       invoice_id       BIGINT        NOT NULL,
       order_line_id    BIGINT        NULL,
       position         INT           NOT NULL,
       sku_code         VARCHAR(64)   NULL,
       description      VARCHAR(255)  NOT NULL,
       quantity         INT           NOT NULL,
       unit_price       DECIMAL(15,2) NOT NULL,
       discount         DECIMAL(15,2) NOT NULL DEFAULT 0,
       tax_code         VARCHAR(30)   NULL,
       tax_kind         VARCHAR(20)   NULL,
       tax_rate         DECIMAL(6,3)  NOT NULL DEFAULT 0,
       tax_base         DECIMAL(15,2) NOT NULL,
       tax_amount       DECIMAL(15,2) NOT NULL,
       total            DECIMAL(15,2) NOT NULL,
       PRIMARY KEY (id_invoice_line),
       KEY idx_invoice_lines_invoice (invoice_id, position),
       CONSTRAINT fk_invoice_lines_invoice FOREIGN KEY (invoice_id) REFERENCES invoices (id_invoice)
   ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 8. Caja: cajas, turnos, arqueo y movimientos ---------------------------------------------
CREATE TABLE IF NOT EXISTS cash_registers (
    id_cash_register BIGINT       NOT NULL AUTO_INCREMENT,
    code             VARCHAR(30)  NOT NULL,
    name             VARCHAR(120) NOT NULL,
    location         VARCHAR(160) NULL,
    warehouse_id     BIGINT       NULL,
    active           TINYINT(1)   NOT NULL DEFAULT 1,
    usr_crea         INT          NOT NULL,
    usr_mod          INT          NULL,
    created_at       DATETIME(6)  NULL,
    updated_at       DATETIME(6)  NULL,
    deleted          TINYINT(1)   NOT NULL DEFAULT 0,
    code_active      VARCHAR(30)  GENERATED ALWAYS AS (IF(deleted = 0, code, NULL)) STORED,
    PRIMARY KEY (id_cash_register),
    UNIQUE KEY uk_cash_registers_code_active (code_active)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

INSERT INTO cash_registers (code, name, location, active, usr_crea, created_at, deleted)
SELECT 'CAJA_1', 'Caja principal', 'Tienda', 1, 1, NOW(6), 0 FROM DUAL
 WHERE NOT EXISTS (SELECT 1 FROM cash_registers WHERE deleted = 0);

CREATE TABLE IF NOT EXISTS cash_sessions (
    id_cash_session   BIGINT        NOT NULL AUTO_INCREMENT,
    cash_register_id  BIGINT        NOT NULL,
    status            VARCHAR(20)   NOT NULL,
    opened_by         VARCHAR(120)  NOT NULL,
    opened_at         DATETIME(6)   NOT NULL,
    opening_amount    DECIMAL(15,2) NOT NULL DEFAULT 0,
    opening_notes     VARCHAR(500)  NULL,
    closed_by         VARCHAR(120)  NULL,
    closed_at         DATETIME(6)   NULL,
    expected_cash     DECIMAL(15,2) NULL,
    counted_cash      DECIMAL(15,2) NULL,
    cash_difference   DECIMAL(15,2) NULL,
    closing_notes     VARCHAR(500)  NULL,
    version           BIGINT        NOT NULL DEFAULT 0,
    usr_crea          INT           NOT NULL,
    usr_mod           INT           NULL,
    created_at        DATETIME(6)   NULL,
    updated_at        DATETIME(6)   NULL,
    deleted           TINYINT(1)    NOT NULL DEFAULT 0,
    open_register     BIGINT GENERATED ALWAYS AS (IF(status = 'OPEN' AND deleted = 0, cash_register_id, NULL)) STORED,
    PRIMARY KEY (id_cash_session),
    UNIQUE KEY uk_cash_sessions_open_register (open_register),
    KEY idx_cash_sessions_register (cash_register_id, opened_at),
    CONSTRAINT fk_cash_sessions_register FOREIGN KEY (cash_register_id) REFERENCES cash_registers (id_cash_register),
    CONSTRAINT ck_cash_sessions_status CHECK (status IN ('OPEN', 'CLOSED')),
    CONSTRAINT ck_cash_sessions_opening CHECK (opening_amount >= 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

SET @sql := IF(@has_payments = 1,
  'CREATE TABLE IF NOT EXISTS cash_session_counts (
       id_cash_session_count BIGINT        NOT NULL AUTO_INCREMENT,
       cash_session_id       BIGINT        NOT NULL,
       payment_method_id     BIGINT        NOT NULL,
       method_name           VARCHAR(120)  NOT NULL,
       kind                  VARCHAR(30)   NOT NULL,
       payments_count        INT           NOT NULL DEFAULT 0,
       expected_amount       DECIMAL(15,2) NOT NULL DEFAULT 0,
       counted_amount        DECIMAL(15,2) NOT NULL DEFAULT 0,
       difference            DECIMAL(15,2) NOT NULL DEFAULT 0,
       PRIMARY KEY (id_cash_session_count),
       UNIQUE KEY uk_cash_session_counts (cash_session_id, payment_method_id),
       CONSTRAINT fk_cash_session_counts_session FOREIGN KEY (cash_session_id) REFERENCES cash_sessions (id_cash_session)
   ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS cash_movements (
    id_cash_movement BIGINT        NOT NULL AUTO_INCREMENT,
    cash_session_id  BIGINT        NOT NULL,
    type             VARCHAR(10)   NOT NULL,
    amount           DECIMAL(15,2) NOT NULL,
    reason           VARCHAR(255)  NOT NULL,
    reference        VARCHAR(120)  NULL,
    created_by       VARCHAR(120)  NULL,
    created_at       DATETIME(6)   NOT NULL,
    PRIMARY KEY (id_cash_movement),
    KEY idx_cash_movements_session (cash_session_id, created_at),
    CONSTRAINT fk_cash_movements_session FOREIGN KEY (cash_session_id) REFERENCES cash_sessions (id_cash_session),
    CONSTRAINT ck_cash_movements_type CHECK (type IN ('IN', 'OUT')),
    CONSTRAINT ck_cash_movements_amount CHECK (amount > 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'payments' AND COLUMN_NAME = 'cash_session_id');
SET @sql := IF(@has_payments = 1 AND @missing = 1,
  'ALTER TABLE payments ADD COLUMN cash_session_id BIGINT NULL,
                        ADD KEY idx_payments_cash_session (cash_session_id, status),
                        ADD CONSTRAINT fk_payments_cash_session FOREIGN KEY (cash_session_id) REFERENCES cash_sessions (id_cash_session)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.TABLE_CONSTRAINTS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders' AND CONSTRAINT_NAME = 'fk_orders_cash_session');
SET @sql := IF(@ready = 1 AND @missing = 1,
  'ALTER TABLE orders ADD KEY idx_orders_cash_session (cash_session_id),
                      ADD CONSTRAINT fk_orders_cash_session FOREIGN KEY (cash_session_id) REFERENCES cash_sessions (id_cash_session)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
