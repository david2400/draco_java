-- =============================================================================
-- V12 · Ubicación de los puntos de venta desde el catálogo del servicio parametros
--
--   stores.country_id / state_id / city_id  ids de georeferencing (país,
--   departamento, ciudad). Sin FK: el catálogo vive en otra base de datos.
--   stores.city conserva el nombre de la ciudad (lo imprime el ticket).
--
-- No destructivo e idempotente.
-- =============================================================================

SET @has_stores := (SELECT COUNT(*) FROM information_schema.TABLES
                    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'stores');
SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'stores' AND COLUMN_NAME = 'city_id');
SET @sql := IF(@has_stores = 1 AND @missing = 1,
  'ALTER TABLE stores ADD COLUMN country_id BIGINT NULL,
                      ADD COLUMN state_id BIGINT NULL,
                      ADD COLUMN city_id BIGINT NULL', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
