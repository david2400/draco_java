-- =============================================================================
-- V13 · Proveedores empresariales
--
--   suppliers           código, datos fiscales (tipo y número de documento, DV, tipo de
--                       persona, razón social y nombre comercial, régimen, gran
--                       contribuyente, autorretenedor), ubicación del servicio parametros,
--                       estado (activo, bloqueado con motivo, inactivo), condiciones
--                       comerciales (plazo, cupo, moneda, entrega, pedido mínimo,
--                       descuento), cuenta bancaria y notas. Teléfono, correo y dirección
--                       dejan de ser obligatorios en la base (los valida el dominio).
--   supplier_contacts   personas de contacto por área; una principal.
--   supplier_products   SKU que surte cada proveedor: código del proveedor, costo,
--                       entrega, cantidad mínima y proveedor preferido del SKU.
--   Los proveedores existentes reciben el código PROV_<id> y quedan activos.
--
-- No destructivo e idempotente.
-- =============================================================================

-- 1. suppliers (en una base vacía se crea con las columnas que dejó Hibernate) --------
CREATE TABLE IF NOT EXISTS suppliers (
    id_supplier BIGINT       NOT NULL AUTO_INCREMENT,
    created_at  DATETIME(6)  NULL,
    deleted     TINYINT(1)   NOT NULL DEFAULT 0,
    updated_at  DATETIME(6)  NULL,
    usr_crea    INT          NOT NULL,
    usr_mod     INT          NULL,
    address     VARCHAR(255) NULL,
    email       VARCHAR(255) NULL,
    name        VARCHAR(255) NOT NULL,
    phone       VARCHAR(255) NULL,
    PRIMARY KEY (id_supplier)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'suppliers' AND COLUMN_NAME = 'code');
SET @sql := IF(@missing = 1,
  'ALTER TABLE suppliers MODIFY address VARCHAR(255) NULL,
                         MODIFY email VARCHAR(255) NULL,
                         MODIFY phone VARCHAR(255) NULL,
                         ADD COLUMN code VARCHAR(30) NULL,
                         ADD COLUMN doc_type VARCHAR(10) NULL,
                         ADD COLUMN doc_number VARCHAR(20) NULL,
                         ADD COLUMN dv CHAR(1) NULL,
                         ADD COLUMN person_type VARCHAR(10) NOT NULL DEFAULT ''JURIDICA'',
                         ADD COLUMN trade_name VARCHAR(160) NULL,
                         ADD COLUMN tax_regime VARCHAR(20) NULL,
                         ADD COLUMN great_contributor TINYINT(1) NOT NULL DEFAULT 0,
                         ADD COLUMN self_retainer TINYINT(1) NOT NULL DEFAULT 0,
                         ADD COLUMN website VARCHAR(160) NULL,
                         ADD COLUMN country_id BIGINT NULL,
                         ADD COLUMN state_id BIGINT NULL,
                         ADD COLUMN city_id BIGINT NULL,
                         ADD COLUMN city VARCHAR(80) NULL,
                         ADD COLUMN status VARCHAR(10) NOT NULL DEFAULT ''ACTIVE'',
                         ADD COLUMN block_reason VARCHAR(255) NULL,
                         ADD COLUMN payment_term_days INT NOT NULL DEFAULT 0,
                         ADD COLUMN credit_limit DECIMAL(15,2) NULL,
                         ADD COLUMN currency CHAR(3) NOT NULL DEFAULT ''COP'',
                         ADD COLUMN lead_time_days INT NULL,
                         ADD COLUMN min_order_amount DECIMAL(15,2) NULL,
                         ADD COLUMN discount_percent DECIMAL(5,2) NULL,
                         ADD COLUMN bank_name VARCHAR(80) NULL,
                         ADD COLUMN bank_account_type VARCHAR(10) NULL,
                         ADD COLUMN bank_account_number VARCHAR(30) NULL,
                         ADD COLUMN bank_account_holder VARCHAR(160) NULL,
                         ADD COLUMN bank_holder_document VARCHAR(20) NULL,
                         ADD COLUMN notes VARCHAR(1000) NULL,
                         ADD CONSTRAINT ck_suppliers_status CHECK (status IN (''ACTIVE'', ''BLOCKED'', ''INACTIVE'')),
                         ADD CONSTRAINT ck_suppliers_person CHECK (person_type IN (''NATURAL'', ''JURIDICA''))', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

UPDATE suppliers SET code = CONCAT('PROV_', id_supplier) WHERE code IS NULL;
UPDATE suppliers SET email = NULL WHERE email = '';
UPDATE suppliers SET phone = NULL WHERE phone = '';
UPDATE suppliers SET address = NULL WHERE address = '';

-- Código y documento únicos entre los no borrados (columnas generadas, como en stores).
SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'suppliers' AND COLUMN_NAME = 'code_active');
SET @sql := IF(@missing = 1,
  'ALTER TABLE suppliers MODIFY code VARCHAR(30) NOT NULL,
                         ADD COLUMN code_active VARCHAR(30) GENERATED ALWAYS AS (IF(deleted = 0, code, NULL)) STORED,
                         ADD COLUMN document_active VARCHAR(31)
                             GENERATED ALWAYS AS (IF(deleted = 0 AND doc_number IS NOT NULL, CONCAT(doc_type, '':'', doc_number), NULL)) STORED,
                         ADD UNIQUE KEY uk_suppliers_code_active (code_active),
                         ADD UNIQUE KEY uk_suppliers_document_active (document_active),
                         ADD KEY ix_suppliers_status (status)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2. supplier_contacts ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS supplier_contacts (
    id_supplier_contact BIGINT       NOT NULL AUTO_INCREMENT,
    supplier_id         BIGINT       NOT NULL,
    name                VARCHAR(120) NOT NULL,
    position            VARCHAR(80)  NULL,
    area                VARCHAR(12)  NOT NULL DEFAULT 'VENTAS',
    phone               VARCHAR(40)  NULL,
    email               VARCHAR(120) NULL,
    is_primary          TINYINT(1)   NOT NULL DEFAULT 0,
    sort_order          INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id_supplier_contact),
    KEY ix_supplier_contacts_supplier (supplier_id),
    CONSTRAINT fk_supplier_contacts_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers (id_supplier)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- Los datos de contacto del proveedor existente se conservan en la ficha (teléfono y correo).

-- 3. supplier_products ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS supplier_products (
    id_supplier_product BIGINT        NOT NULL AUTO_INCREMENT,
    supplier_id         BIGINT        NOT NULL,
    product_id          BIGINT        NOT NULL,
    sku_id              BIGINT        NOT NULL,
    supplier_code       VARCHAR(64)   NULL,
    unit_cost           DECIMAL(15,2) NOT NULL,
    lead_time_days      INT           NULL,
    min_order_qty       INT           NOT NULL DEFAULT 1,
    preferred           TINYINT(1)    NOT NULL DEFAULT 0,
    active              TINYINT(1)    NOT NULL DEFAULT 1,
    created_at          DATETIME(6)   NULL,
    updated_at          DATETIME(6)   NULL,
    PRIMARY KEY (id_supplier_product),
    UNIQUE KEY uk_supplier_products_sku (supplier_id, sku_id),
    KEY ix_supplier_products_sku (sku_id),
    CONSTRAINT fk_supplier_products_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers (id_supplier)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
