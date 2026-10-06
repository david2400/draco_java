-- =============================================================================
-- V2 · Corrige los índices únicos de stock_per_warehouse
--
-- La entidad declaraba unique=true en product_id y en warehouse_id por separado,
-- y Hibernate (ddl-auto=update) creó un índice ÚNICO por cada columna:
--   · un producto solo podía estar en UNA bodega,
--   · una bodega solo podía guardar UN producto.
-- La regla correcta es la unicidad de la PAREJA (product_id, warehouse_id).
--
-- Script idempotente: consulta information_schema y solo actúa si hace falta,
-- así funciona tanto en bases existentes como en una base nueva (tabla aún
-- inexistente: no hace nada y Hibernate la crea ya corregida).
-- =============================================================================

SET @spw_exists := (SELECT COUNT(*) FROM information_schema.TABLES
                    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'stock_per_warehouse');

-- 1) Unicidad compuesta (también sirve de índice para la FK de product_id).
SET @has_uk := (SELECT COUNT(*) FROM information_schema.STATISTICS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'stock_per_warehouse'
                  AND INDEX_NAME = 'uk_spw_product_warehouse');
SET @sql := IF(@spw_exists = 1 AND @has_uk = 0,
               'ALTER TABLE stock_per_warehouse ADD CONSTRAINT uk_spw_product_warehouse UNIQUE (product_id, warehouse_id)',
               'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2) Índice NO único en warehouse_id para su FK (antes de quitar el único).
SET @has_wh := (SELECT COUNT(*) FROM information_schema.STATISTICS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'stock_per_warehouse'
                  AND INDEX_NAME = 'idx_spw_warehouse');
SET @sql := IF(@spw_exists = 1 AND @has_wh = 0,
               'CREATE INDEX idx_spw_warehouse ON stock_per_warehouse (warehouse_id)',
               'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3) Eliminar índices únicos de UNA sola columna sobre product_id / warehouse_id.
--    Se repite cada bloque por si Hibernate generó más de uno por columna.
SET @idx := (SELECT INDEX_NAME FROM information_schema.STATISTICS
             WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'stock_per_warehouse'
               AND NON_UNIQUE = 0 AND INDEX_NAME NOT IN ('PRIMARY', 'uk_spw_product_warehouse')
             GROUP BY INDEX_NAME HAVING COUNT(*) = 1 AND MAX(COLUMN_NAME) = 'product_id' LIMIT 1);
SET @sql := IF(@idx IS NULL, 'DO 0', CONCAT('ALTER TABLE stock_per_warehouse DROP INDEX `', @idx, '`'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx := (SELECT INDEX_NAME FROM information_schema.STATISTICS
             WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'stock_per_warehouse'
               AND NON_UNIQUE = 0 AND INDEX_NAME NOT IN ('PRIMARY', 'uk_spw_product_warehouse')
             GROUP BY INDEX_NAME HAVING COUNT(*) = 1 AND MAX(COLUMN_NAME) = 'product_id' LIMIT 1);
SET @sql := IF(@idx IS NULL, 'DO 0', CONCAT('ALTER TABLE stock_per_warehouse DROP INDEX `', @idx, '`'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx := (SELECT INDEX_NAME FROM information_schema.STATISTICS
             WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'stock_per_warehouse'
               AND NON_UNIQUE = 0 AND INDEX_NAME NOT IN ('PRIMARY', 'uk_spw_product_warehouse')
             GROUP BY INDEX_NAME HAVING COUNT(*) = 1 AND MAX(COLUMN_NAME) = 'warehouse_id' LIMIT 1);
SET @sql := IF(@idx IS NULL, 'DO 0', CONCAT('ALTER TABLE stock_per_warehouse DROP INDEX `', @idx, '`'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx := (SELECT INDEX_NAME FROM information_schema.STATISTICS
             WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'stock_per_warehouse'
               AND NON_UNIQUE = 0 AND INDEX_NAME NOT IN ('PRIMARY', 'uk_spw_product_warehouse')
             GROUP BY INDEX_NAME HAVING COUNT(*) = 1 AND MAX(COLUMN_NAME) = 'warehouse_id' LIMIT 1);
SET @sql := IF(@idx IS NULL, 'DO 0', CONCAT('ALTER TABLE stock_per_warehouse DROP INDEX `', @idx, '`'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
