-- =============================================================================
-- V15 · Generador de variantes
--
--   product_variant_axes   ejes de variante de cada producto (los de la plantilla y los
--                          que se agregan solo a ese producto), con su orden y cuál define
--                          la imagen (p. ej. el color).
--   product_option_images  imagen de un valor de eje en un producto (Rojo → foto roja); se
--                          aplica a todas las variantes con ese valor.
--   product_childs         cost_price: costo propio de la variante (si es nulo se usa el
--                          costo del producto, como hasta ahora).
--   Los productos que ya tienen opciones en sus SKU reciben esos ejes en el orden de la
--   plantilla.
--
-- No destructivo e idempotente.
-- =============================================================================

CREATE TABLE IF NOT EXISTS product_variant_axes (
    product_id   BIGINT     NOT NULL,
    attribute_id BIGINT     NOT NULL,
    position     INT        NOT NULL DEFAULT 0,
    image_axis   TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (product_id, attribute_id),
    KEY ix_product_variant_axes_attribute (attribute_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS product_option_images (
    product_id BIGINT       NOT NULL,
    option_id  BIGINT       NOT NULL,
    image_url  VARCHAR(500) NOT NULL,
    PRIMARY KEY (product_id, option_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

SET @has_childs := (SELECT COUNT(*) FROM information_schema.TABLES
                    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product_childs');
SET @missing := (SELECT COUNT(*) = 0 FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product_childs' AND COLUMN_NAME = 'cost_price');
SET @sql := IF(@has_childs = 1 AND @missing = 1,
  'ALTER TABLE product_childs ADD COLUMN cost_price DECIMAL(15,2) NULL', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Ejes ya usados en los SKU de cada producto (orden: posición en la plantilla, luego id).
SET @has_values := (SELECT COUNT(DISTINCT TABLE_NAME) = 4 FROM information_schema.TABLES
                    WHERE TABLE_SCHEMA = DATABASE()
                      AND TABLE_NAME IN ('sku_attribute_values', 'product_skus', 'products', 'template_attributes'));
SET @sql := IF(@has_values = 1,
  'INSERT IGNORE INTO product_variant_axes (product_id, attribute_id, position, image_axis)
   SELECT k.product_id, v.attribute_id, MIN(COALESCE(ta.position, 100 + v.attribute_id)), 0
     FROM sku_attribute_values v
     JOIN product_skus k ON k.id_sku = v.sku_id AND k.deleted = 0
     JOIN products p ON p.id_product = k.product_id
     LEFT JOIN template_attributes ta ON ta.template_id = p.template_id AND ta.attribute_id = v.attribute_id
    GROUP BY k.product_id, v.attribute_id', 'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
