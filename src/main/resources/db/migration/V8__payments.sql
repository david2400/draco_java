-- =============================================================================
-- V8 · Medios de pago y pagos de órdenes (físicos y virtuales)
--
--   payment_types   pasa a ser el catálogo de medios de pago: code (único entre no
--                   borrados), channel (PHYSICAL | VIRTUAL), kind (CASH, CARD_TERMINAL,
--                   BANK_TRANSFER, DIGITAL_WALLET, GATEWAY, CASH_ON_DELIVERY, OTHER),
--                   provider de pasarela (SANDBOX, WOMPI, MERCADO_PAGO), comisión
--                   (porcentaje + fija), montos mínimo/máximo, referencia obligatoria,
--                   admite cambio (efectivo), instrucciones, activo y orden.
--                   Se precargan los medios habituales (las pasarelas reales inactivas).
--   payments        pagos de una orden: monto, comisión, efectivo recibido y cambio,
--                   estado (PENDING → APPROVED / DECLINED / EXPIRED / VOIDED;
--                   APPROVED → VOIDED / REFUNDED), referencia manual (voucher,
--                   comprobante) y de la pasarela, link de pago, reembolsado.
--   payment_events  bitácora de cambios y de webhooks (idempotencia por evento).
--
-- No destructivo e idempotente.
-- =============================================================================

SET @has_types := (SELECT COUNT(*) FROM information_schema.TABLES
                   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'payment_types');
SET @has_orders := (SELECT COUNT(*) FROM information_schema.TABLES
                    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders');

-- 1. payment_types: columnas del medio de pago -------------------------------------
SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'payment_types' AND COLUMN_NAME = 'code');
SET @sql := IF(@has_types = 1 AND @missing = 1,
  'ALTER TABLE payment_types
     ADD COLUMN code VARCHAR(40) NULL,
     ADD COLUMN description VARCHAR(255) NULL,
     ADD COLUMN instructions VARCHAR(1000) NULL,
     ADD COLUMN channel VARCHAR(20) NOT NULL DEFAULT ''PHYSICAL'',
     ADD COLUMN kind VARCHAR(30) NOT NULL DEFAULT ''OTHER'',
     ADD COLUMN provider VARCHAR(30) NULL,
     ADD COLUMN active TINYINT(1) NOT NULL DEFAULT 1,
     ADD COLUMN position INT NOT NULL DEFAULT 0,
     ADD COLUMN fee_percent DECIMAL(6,3) NOT NULL DEFAULT 0,
     ADD COLUMN fee_fixed DECIMAL(15,2) NOT NULL DEFAULT 0,
     ADD COLUMN min_amount DECIMAL(15,2) NULL,
     ADD COLUMN max_amount DECIMAL(15,2) NULL,
     ADD COLUMN requires_reference TINYINT(1) NOT NULL DEFAULT 0,
     ADD COLUMN allows_change TINYINT(1) NOT NULL DEFAULT 0', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Medios existentes sin código: código derivado del nombre y del id (único).
SET @sql := IF(@has_types = 1,
  'UPDATE payment_types
      SET code = CONCAT(LEFT(TRIM(BOTH ''_'' FROM REGEXP_REPLACE(UPPER(name), ''[^A-Z0-9]+'', ''_'')), 28), ''_'', id_payment_type)
    WHERE code IS NULL OR code = ''''', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Código único entre no borrados (columna generada, igual que en unidades de medida).
SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'payment_types' AND COLUMN_NAME = 'code_active');
SET @sql := IF(@has_types = 1 AND @missing = 1,
  'ALTER TABLE payment_types
     MODIFY COLUMN code VARCHAR(40) NOT NULL,
     ADD COLUMN code_active VARCHAR(40) GENERATED ALWAYS AS (IF(deleted = 0, code, NULL)) STORED,
     ADD UNIQUE KEY uk_payment_types_code_active (code_active),
     ADD KEY idx_payment_types_active (active, position)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2. Medios de pago habituales (solo los que faltan por código) ----------------------
SET @sql := IF(@has_types = 1,
  'INSERT INTO payment_types (code, name, description, instructions, channel, kind, provider, active, position,
                              fee_percent, fee_fixed, requires_reference, allows_change, usr_crea, created_at, deleted)
   SELECT s.code, s.name, s.description, s.instructions, s.channel, s.kind, s.provider, s.active, s.position,
          0, 0, s.requires_reference, s.allows_change, 1, NOW(6), 0
     FROM (
       SELECT ''CASH'' code, ''Efectivo'' name, ''Pago en efectivo en tienda o punto de venta'' description,
              NULL instructions, ''PHYSICAL'' channel, ''CASH'' kind, NULL provider, 1 active, 10 position,
              0 requires_reference, 1 allows_change
       UNION ALL SELECT ''CARD_TERMINAL'', ''Datáfono'', ''Tarjeta débito o crédito en datáfono'',
              ''Registra el número de aprobación del voucher.'', ''PHYSICAL'', ''CARD_TERMINAL'', NULL, 1, 20, 1, 0
       UNION ALL SELECT ''BANK_TRANSFER'', ''Transferencia bancaria'', ''Transferencia o consignación a la cuenta de la tienda'',
              ''Registra el número del comprobante y confírmalo al verlo en el banco.'', ''VIRTUAL'', ''BANK_TRANSFER'', NULL, 1, 30, 1, 0
       UNION ALL SELECT ''DIGITAL_WALLET'', ''Nequi / Daviplata'', ''Billetera digital confirmada manualmente'',
              ''Registra el número de la transacción.'', ''VIRTUAL'', ''DIGITAL_WALLET'', NULL, 1, 40, 1, 0
       UNION ALL SELECT ''CASH_ON_DELIVERY'', ''Contraentrega'', ''El cliente paga al recibir el pedido'',
              NULL, ''PHYSICAL'', ''CASH_ON_DELIVERY'', NULL, 1, 50, 0, 1
       UNION ALL SELECT ''SANDBOX'', ''Pasarela de pruebas'', ''Simulador interno de pagos en línea (solo pruebas)'',
              NULL, ''VIRTUAL'', ''GATEWAY'', ''SANDBOX'', 1, 90, 0, 0
       UNION ALL SELECT ''WOMPI'', ''Wompi'', ''Tarjeta, PSE, Nequi y Bancolombia en línea'',
              NULL, ''VIRTUAL'', ''GATEWAY'', ''WOMPI'', 0, 60, 0, 0
       UNION ALL SELECT ''MERCADO_PAGO'', ''Mercado Pago'', ''Tarjeta, PSE y efectivo vía Mercado Pago'',
              NULL, ''VIRTUAL'', ''GATEWAY'', ''MERCADO_PAGO'', 0, 70, 0, 0
     ) s
    WHERE NOT EXISTS (SELECT 1 FROM payment_types t WHERE t.code = s.code AND t.deleted = 0)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3. payments --------------------------------------------------------------------
SET @sql := IF(@has_types = 1 AND @has_orders = 1,
  'CREATE TABLE IF NOT EXISTS payments (
       id_payment          BIGINT        NOT NULL AUTO_INCREMENT,
       order_id            BIGINT        NOT NULL,
       payment_type_id     BIGINT        NOT NULL,
       channel             VARCHAR(20)   NOT NULL,
       kind                VARCHAR(30)   NOT NULL,
       provider            VARCHAR(30)   NULL,
       status              VARCHAR(20)   NOT NULL,
       amount              DECIMAL(15,2) NOT NULL,
       fee_amount          DECIMAL(15,2) NOT NULL DEFAULT 0,
       tendered_amount     DECIMAL(15,2) NULL,
       change_amount       DECIMAL(15,2) NULL,
       refunded_amount     DECIMAL(15,2) NOT NULL DEFAULT 0,
       currency            VARCHAR(3)    NOT NULL DEFAULT ''COP'',
       reference           VARCHAR(120)  NULL,
       payment_reference   VARCHAR(60)   NOT NULL,
       provider_reference  VARCHAR(120)  NULL,
       checkout_url        VARCHAR(1000) NULL,
       return_url          VARCHAR(1000) NULL,
       payer_email         VARCHAR(160)  NULL,
       notes               VARCHAR(500)  NULL,
       status_detail       VARCHAR(255)  NULL,
       approved_at         DATETIME(6)   NULL,
       expires_at          DATETIME(6)   NULL,
       version             BIGINT        NOT NULL DEFAULT 0,
       usr_crea            INT           NOT NULL,
       usr_mod             INT           NULL,
       created_at          DATETIME(6)   NULL,
       updated_at          DATETIME(6)   NULL,
       deleted             TINYINT(1)    NOT NULL DEFAULT 0,
       PRIMARY KEY (id_payment),
       UNIQUE KEY uk_payments_payment_reference (payment_reference),
       KEY idx_payments_order (order_id, status),
       KEY idx_payments_type (payment_type_id),
       KEY idx_payments_status_created (status, created_at),
       KEY idx_payments_provider_ref (provider, provider_reference),
       CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders (id_order),
       CONSTRAINT fk_payments_type FOREIGN KEY (payment_type_id) REFERENCES payment_types (id_payment_type),
       CONSTRAINT ck_payments_amount CHECK (amount > 0),
       CONSTRAINT ck_payments_refunded CHECK (refunded_amount >= 0 AND refunded_amount <= amount),
       CONSTRAINT ck_payments_status CHECK (status IN (''PENDING'', ''APPROVED'', ''DECLINED'', ''EXPIRED'', ''VOIDED'', ''REFUNDED''))
   ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 4. payment_events ----------------------------------------------------------------
SET @sql := IF(@has_types = 1 AND @has_orders = 1,
  'CREATE TABLE IF NOT EXISTS payment_events (
       id_payment_event  BIGINT        NOT NULL AUTO_INCREMENT,
       payment_id        BIGINT        NULL,
       source            VARCHAR(30)   NOT NULL,
       event_key         VARCHAR(160)  NULL,
       type              VARCHAR(40)   NOT NULL,
       from_status       VARCHAR(20)   NULL,
       to_status         VARCHAR(20)   NULL,
       amount            DECIMAL(15,2) NULL,
       detail            VARCHAR(500)  NULL,
       payload           TEXT          NULL,
       created_at        DATETIME(6)   NOT NULL,
       PRIMARY KEY (id_payment_event),
       UNIQUE KEY uk_payment_events_key (source, event_key),
       KEY idx_payment_events_payment (payment_id, created_at),
       CONSTRAINT fk_payment_events_payment FOREIGN KEY (payment_id) REFERENCES payments (id_payment)
   ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
