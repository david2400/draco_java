-- =============================================================================
-- V5 · Atributos, plantillas y ejes de variante (Fase 4)
--
-- Modelo nuevo (catálogo):
--   attributes            definición tipada (TEXT | NUMBER | BOOLEAN | OPTION) + unidad
--   attribute_options     valores permitidos de un atributo OPTION (color: Rojo, Azul…)
--   product_templates     plantilla por tipo de producto (antes type_products)
--   template_attributes   atributos de la plantilla: obligatorio, eje de variante, filtrable
--   product_attribute_values  ficha técnica del producto (un valor tipado por atributo)
--   sku_attribute_values      opción de cada eje de variante para cada SKU
--   products.template_id
--
-- Migra los datos de features / type_products / type_product_features /
-- product_features (las tablas antiguas NO se borran: siguen sirviendo a los
-- endpoints /product_details/** marcados como obsoletos hasta la Fase 7).
--
-- No destructivo e idempotente; en base vacía solo crea las tablas.
-- =============================================================================

SET @has_products := (SELECT COUNT(*) FROM information_schema.TABLES
                      WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'products');
SET @has_skus := (SELECT COUNT(*) FROM information_schema.TABLES
                  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product_skus');
SET @has_units := (SELECT COUNT(*) FROM information_schema.TABLES
                   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'units_measurement');
SET @has_features := (SELECT COUNT(*) FROM information_schema.TABLES
                      WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'features');
SET @has_feature_units := (SELECT COUNT(*) FROM information_schema.TABLES
                           WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'feature_unit_measurement');
SET @has_types := (SELECT COUNT(*) FROM information_schema.TABLES
                   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'type_products');
SET @has_type_features := (SELECT COUNT(*) FROM information_schema.TABLES
                           WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'type_product_features');
SET @has_product_features := (SELECT COUNT(*) FROM information_schema.TABLES
                              WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product_features');
SET @ready := (@has_products = 1 AND @has_skus = 1);

-- 1. attributes -------------------------------------------------------------------
-- code_active: código único solo entre los no borrados (el borrado es lógico).
SET @sql := IF(@ready = 1,
  'CREATE TABLE IF NOT EXISTS attributes (
       id_attribute       BIGINT       NOT NULL AUTO_INCREMENT,
       code               VARCHAR(60)  NOT NULL,
       name               VARCHAR(120) NOT NULL,
       description        VARCHAR(500) NULL,
       data_type          VARCHAR(20)  NOT NULL,
       unit_id            BIGINT       NULL,
       legacy_feature_id  BIGINT       NULL,
       version            BIGINT       NOT NULL DEFAULT 0,
       usr_crea           INT          NOT NULL,
       usr_mod            INT          NULL,
       created_at         DATETIME(6)  NULL,
       updated_at         DATETIME(6)  NULL,
       deleted            TINYINT(1)   NOT NULL DEFAULT 0,
       code_active        VARCHAR(60)  GENERATED ALWAYS AS (IF(deleted = 0, code, NULL)) VIRTUAL,
       PRIMARY KEY (id_attribute),
       CONSTRAINT uk_attributes_code_active UNIQUE (code_active),
       CONSTRAINT uk_attributes_legacy_feature UNIQUE (legacy_feature_id),
       CONSTRAINT ck_attributes_data_type CHECK (data_type IN (''TEXT'', ''NUMBER'', ''BOOLEAN'', ''OPTION''))
   ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.TABLE_CONSTRAINTS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'attributes' AND CONSTRAINT_NAME = 'fk_attributes_unit');
SET @sql := IF(@ready = 1 AND @has_units = 1 AND @missing = 1,
  'ALTER TABLE attributes ADD CONSTRAINT fk_attributes_unit FOREIGN KEY (unit_id) REFERENCES units_measurement (id_unit_measurement)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2. attribute_options ------------------------------------------------------------
SET @sql := IF(@ready = 1,
  'CREATE TABLE IF NOT EXISTS attribute_options (
       id_option     BIGINT       NOT NULL AUTO_INCREMENT,
       attribute_id  BIGINT       NOT NULL,
       value         VARCHAR(120) NOT NULL,
       position      INT          NOT NULL DEFAULT 0,
       created_at    DATETIME(6)  NULL,
       updated_at    DATETIME(6)  NULL,
       PRIMARY KEY (id_option),
       CONSTRAINT uk_attribute_options_value UNIQUE (attribute_id, value),
       CONSTRAINT fk_attribute_options_attribute FOREIGN KEY (attribute_id) REFERENCES attributes (id_attribute)
   ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3. product_templates -----------------------------------------------------------
SET @sql := IF(@ready = 1,
  'CREATE TABLE IF NOT EXISTS product_templates (
       id_template             BIGINT       NOT NULL AUTO_INCREMENT,
       name                    VARCHAR(120) NOT NULL,
       description             VARCHAR(500) NULL,
       legacy_type_product_id  BIGINT       NULL,
       version                 BIGINT       NOT NULL DEFAULT 0,
       usr_crea                INT          NOT NULL,
       usr_mod                 INT          NULL,
       created_at              DATETIME(6)  NULL,
       updated_at              DATETIME(6)  NULL,
       deleted                 TINYINT(1)   NOT NULL DEFAULT 0,
       name_active             VARCHAR(120) GENERATED ALWAYS AS (IF(deleted = 0, name, NULL)) VIRTUAL,
       PRIMARY KEY (id_template),
       CONSTRAINT uk_product_templates_name_active UNIQUE (name_active),
       CONSTRAINT uk_product_templates_legacy UNIQUE (legacy_type_product_id)
   ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 4. template_attributes ---------------------------------------------------------
SET @sql := IF(@ready = 1,
  'CREATE TABLE IF NOT EXISTS template_attributes (
       template_id   BIGINT     NOT NULL,
       attribute_id  BIGINT     NOT NULL,
       required      TINYINT(1) NOT NULL DEFAULT 0,
       variant_axis  TINYINT(1) NOT NULL DEFAULT 0,
       filterable    TINYINT(1) NOT NULL DEFAULT 0,
       position      INT        NOT NULL DEFAULT 0,
       PRIMARY KEY (template_id, attribute_id),
       KEY idx_template_attributes_attribute (attribute_id),
       CONSTRAINT fk_template_attributes_template FOREIGN KEY (template_id) REFERENCES product_templates (id_template),
       CONSTRAINT fk_template_attributes_attribute FOREIGN KEY (attribute_id) REFERENCES attributes (id_attribute)
   ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 5. products.template_id --------------------------------------------------------
SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'products' AND COLUMN_NAME = 'template_id');
SET @sql := IF(@ready = 1 AND @missing = 1,
  'ALTER TABLE products ADD COLUMN template_id BIGINT NULL,
       ADD KEY idx_products_template (template_id),
       ADD CONSTRAINT fk_products_template FOREIGN KEY (template_id) REFERENCES product_templates (id_template)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 6. product_attribute_values (ficha técnica) ------------------------------------
SET @sql := IF(@ready = 1,
  'CREATE TABLE IF NOT EXISTS product_attribute_values (
       product_id     BIGINT         NOT NULL,
       attribute_id   BIGINT         NOT NULL,
       value_text     VARCHAR(500)   NULL,
       value_number   DECIMAL(18, 4) NULL,
       value_boolean  TINYINT(1)     NULL,
       option_id      BIGINT         NULL,
       PRIMARY KEY (product_id, attribute_id),
       KEY idx_pav_attribute (attribute_id),
       KEY idx_pav_option (option_id),
       CONSTRAINT fk_pav_product FOREIGN KEY (product_id) REFERENCES products (id_product),
       CONSTRAINT fk_pav_attribute FOREIGN KEY (attribute_id) REFERENCES attributes (id_attribute),
       CONSTRAINT fk_pav_option FOREIGN KEY (option_id) REFERENCES attribute_options (id_option)
   ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 7. sku_attribute_values (ejes de variante) -------------------------------------
SET @sql := IF(@ready = 1,
  'CREATE TABLE IF NOT EXISTS sku_attribute_values (
       sku_id        BIGINT NOT NULL,
       attribute_id  BIGINT NOT NULL,
       option_id     BIGINT NOT NULL,
       PRIMARY KEY (sku_id, attribute_id),
       KEY idx_sav_attribute (attribute_id),
       KEY idx_sav_option (option_id),
       CONSTRAINT fk_sav_sku FOREIGN KEY (sku_id) REFERENCES product_skus (id_sku),
       CONSTRAINT fk_sav_attribute FOREIGN KEY (attribute_id) REFERENCES attributes (id_attribute),
       CONSTRAINT fk_sav_option FOREIGN KEY (option_id) REFERENCES attribute_options (id_option)
   ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 8. Migración de datos ------------------------------------------------------------
-- 8.1 features → attributes (NUMBER: el valor antiguo era numérico).
--     code: nombre en minúsculas con "_"; si se repite se le añade _<id>.
-- Fila a fila: código base + sufijo _<id> si ya existe, para no fallar por nombres parecidos.
DROP TABLE IF EXISTS v5_tmp_feature_codes;
SET @sql := IF(@ready = 1 AND @has_features = 1,
  'CREATE TABLE v5_tmp_feature_codes AS
   SELECT f.id_feature,
          LEFT(f.name, 120) AS name,
          NULLIF(LEFT(TRIM(BOTH ''_'' FROM REGEXP_REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(LOWER(f.name), ''á'', ''a''), ''é'', ''e''), ''í'', ''i''), ''ó'', ''o''), ''ú'', ''u''), ''ü'', ''u''), ''ñ'', ''n''), ''[^a-z0-9]+'', ''_'')), 48), '''') AS base_code
     FROM features f
    WHERE f.deleted = 0
      AND NOT EXISTS (SELECT 1 FROM attributes a WHERE a.legacy_feature_id = f.id_feature)', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(@ready = 1 AND @has_features = 1,
  'INSERT INTO attributes (code, name, data_type, legacy_feature_id, usr_crea, created_at, deleted)
   SELECT CASE
            WHEN t.base_code IS NULL THEN CONCAT(''attr_'', t.id_feature)
            WHEN EXISTS (SELECT 1 FROM attributes a WHERE a.code_active = t.base_code)
              OR EXISTS (SELECT 1 FROM v5_tmp_feature_codes t2 WHERE t2.base_code = t.base_code AND t2.id_feature < t.id_feature)
              THEN CONCAT(t.base_code, ''_'', t.id_feature)
            ELSE t.base_code
          END,
          t.name, ''NUMBER'', t.id_feature, 1, NOW(6), 0
     FROM v5_tmp_feature_codes t
    ORDER BY t.id_feature', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
DROP TABLE IF EXISTS v5_tmp_feature_codes;

-- 8.2 Unidad: la primera unidad asociada a la característica.
SET @sql := IF(@ready = 1 AND @has_feature_units = 1,
  'UPDATE attributes a
      JOIN (SELECT id_feature, MIN(id_unit_measurement) AS unit_id
              FROM feature_unit_measurement GROUP BY id_feature) fu
        ON fu.id_feature = a.legacy_feature_id
      SET a.unit_id = fu.unit_id
    WHERE a.unit_id IS NULL', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 8.3 type_products → product_templates.
SET @sql := IF(@ready = 1 AND @has_types = 1,
  'INSERT INTO product_templates (name, legacy_type_product_id, usr_crea, created_at, deleted)
   SELECT LEFT(tp.name, 120), tp.id_type_product, 1, NOW(6), 0
     FROM type_products tp
    WHERE tp.deleted = 0
      AND NOT EXISTS (SELECT 1 FROM product_templates pt WHERE pt.legacy_type_product_id = tp.id_type_product)
      AND NOT EXISTS (SELECT 1 FROM product_templates pt WHERE pt.name_active = LEFT(tp.name, 120))', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 8.4 type_product_features → template_attributes (filtrable por defecto).
SET @sql := IF(@ready = 1 AND @has_type_features = 1,
  'INSERT IGNORE INTO template_attributes (template_id, attribute_id, required, variant_axis, filterable, position)
   SELECT pt.id_template, a.id_attribute, 0, 0, 1, 0
     FROM type_product_features tpf
     JOIN product_templates pt ON pt.legacy_type_product_id = tpf.type_product_id AND pt.deleted = 0
     JOIN attributes a ON a.legacy_feature_id = tpf.feature_id AND a.deleted = 0
    WHERE tpf.deleted = 0', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 8.5 product_features → product_attribute_values (valor numérico).
SET @sql := IF(@ready = 1 AND @has_product_features = 1,
  'INSERT IGNORE INTO product_attribute_values (product_id, attribute_id, value_number)
   SELECT pf.product_id, a.id_attribute, pf.value
     FROM product_features pf
     JOIN attributes a ON a.legacy_feature_id = pf.feature_id AND a.deleted = 0
     JOIN products p ON p.id_product = pf.product_id
    WHERE pf.deleted = 0', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
