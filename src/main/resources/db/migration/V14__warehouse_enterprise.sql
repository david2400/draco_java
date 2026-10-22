-- =============================================================================
-- V14 · Bodegas empresariales
--
--   warehouses            tipo (ALMACEN, TIENDA, TRANSITO, DEVOLUCIONES, CONSIGNACION),
--                         operación (vende, despacha pedidos web, recibe compras,
--                         prioridad de despacho), responsable (nombre, teléfono, correo),
--                         horario, notas, capacidad (m², unidades, posiciones) y nombre
--                         de la ciudad del catálogo de parametros. Código único entre las
--                         bodegas no borradas (si hoy no hay códigos repetidos).
--   warehouse_locations   ubicaciones internas: código, zona, pasillo, estante, nivel,
--                         tipo (almacenamiento, picking, recepción, despacho, cuarentena)
--                         y capacidad.
--   Las bodegas existentes quedan como ALMACEN con todas las operaciones habilitadas.
--
-- No destructivo e idempotente.
-- =============================================================================

-- 1. warehouses (en una base vacía se crea con las columnas que dejó Hibernate) -------
CREATE TABLE IF NOT EXISTS warehouses (
    id_warehouse BIGINT       NOT NULL AUTO_INCREMENT,
    name         VARCHAR(150) NOT NULL,
    code         VARCHAR(50)  NULL,
    address      VARCHAR(255) NULL,
    active       BIT(1)       NOT NULL DEFAULT b'1',
    country_id   BIGINT       NULL,
    state_id     BIGINT       NULL,
    city_id      BIGINT       NULL,
    is_main      BIT(1)       NOT NULL DEFAULT b'0',
    usr_crea     INT          NOT NULL DEFAULT 1,
    usr_mod      INT          NULL,
    created_at   DATETIME(6)  NULL,
    updated_at   DATETIME(6)  NULL,
    deleted      TINYINT(1)   NOT NULL DEFAULT 0,
    PRIMARY KEY (id_warehouse)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'warehouses' AND COLUMN_NAME = 'warehouse_type');
SET @sql := IF(@missing = 1,
  'ALTER TABLE warehouses ADD COLUMN warehouse_type VARCHAR(15) NOT NULL DEFAULT ''ALMACEN'',
                          ADD COLUMN allow_sales TINYINT(1) NOT NULL DEFAULT 1,
                          ADD COLUMN allow_online TINYINT(1) NOT NULL DEFAULT 1,
                          ADD COLUMN allow_receiving TINYINT(1) NOT NULL DEFAULT 1,
                          ADD COLUMN fulfillment_priority INT NOT NULL DEFAULT 10,
                          ADD COLUMN manager_name VARCHAR(120) NULL,
                          ADD COLUMN manager_phone VARCHAR(40) NULL,
                          ADD COLUMN manager_email VARCHAR(120) NULL,
                          ADD COLUMN opening_hours VARCHAR(255) NULL,
                          ADD COLUMN notes VARCHAR(1000) NULL,
                          ADD COLUMN area_m2 DECIMAL(10,2) NULL,
                          ADD COLUMN capacity_units INT NULL,
                          ADD COLUMN capacity_positions INT NULL,
                          ADD COLUMN city VARCHAR(80) NULL,
                          ADD CONSTRAINT ck_warehouses_type
                              CHECK (warehouse_type IN (''ALMACEN'', ''TIENDA'', ''TRANSITO'', ''DEVOLUCIONES'', ''CONSIGNACION'')),
                          ADD KEY ix_warehouses_type (warehouse_type)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Bodegas sin código (registros antiguos): BOD_<id>.
UPDATE warehouses SET code = CONCAT('BOD_', id_warehouse) WHERE code IS NULL OR TRIM(code) = '';

-- Código único entre no borradas, solo si hoy no hay repetidos (no se tocan datos existentes).
SET @dups := (SELECT COUNT(*) FROM (SELECT code FROM warehouses WHERE deleted = 0 GROUP BY code HAVING COUNT(*) > 1) d);
SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'warehouses' AND COLUMN_NAME = 'code_active');
SET @sql := IF(@missing = 1 AND @dups = 0,
  'ALTER TABLE warehouses ADD COLUMN code_active VARCHAR(50) GENERATED ALWAYS AS (IF(deleted = 0, code, NULL)) STORED,
                          ADD UNIQUE KEY uk_warehouses_code_active (code_active)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2. warehouse_locations -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS warehouse_locations (
    id_location    BIGINT      NOT NULL AUTO_INCREMENT,
    warehouse_id   BIGINT      NOT NULL,
    code           VARCHAR(30) NOT NULL,
    zone           VARCHAR(30) NULL,
    aisle          VARCHAR(10) NULL,
    rack           VARCHAR(10) NULL,
    level          VARCHAR(10) NULL,
    location_type  VARCHAR(12) NOT NULL DEFAULT 'STORAGE',
    capacity_units INT         NULL,
    active         TINYINT(1)  NOT NULL DEFAULT 1,
    created_at     DATETIME(6) NULL,
    updated_at     DATETIME(6) NULL,
    PRIMARY KEY (id_location),
    UNIQUE KEY uk_warehouse_locations_code (warehouse_id, code),
    CONSTRAINT fk_warehouse_locations_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses (id_warehouse),
    CONSTRAINT ck_warehouse_locations_type
        CHECK (location_type IN ('STORAGE', 'PICKING', 'RECEIVING', 'DISPATCH', 'QUARANTINE'))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
