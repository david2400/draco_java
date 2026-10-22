package com.essenza.draco.modules.catalog.domain.model.variant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * Reglas del generador de variantes (Java puro). Recibe los ejes con los valores elegidos, las
 * filas de la matriz y las variantes que ya existen, y decide qué crear, qué actualizar y qué
 * quitar: las variantes que dejan de estar en la matriz se borran si nunca tuvieron historial
 * (stock, reservas o ventas) y se desactivan si lo tuvieron.
 */
public final class VariantPlanner {

    public static final int MAX_AXES = 4;
    public static final int MAX_VARIANTS = 300;

    private VariantPlanner() {
    }

    /** Eje con sus valores elegidos (en orden): opción → texto. */
    public record Axis(Long attributeId, String name, Map<Long, String> options) {
        public Axis {
            Objects.requireNonNull(attributeId, "attributeId");
            options = options == null ? Map.of() : java.util.Collections.unmodifiableMap(new LinkedHashMap<>(options));
        }
    }

    /** Fila de la matriz tal como llega del formulario. */
    public record Row(Long childId, Map<Long, Long> options, BigDecimal price, BigDecimal cost, BigDecimal compareAt,
                      String skuCode, String barcode, String imageUrl, boolean active, int initialStock) {
        public Row {
            options = options == null ? Map.of() : Map.copyOf(options);
        }
    }

    /** Variante existente del producto. */
    public record Existing(Long childId, Long skuId, Map<Long, Long> options, boolean hasHistory) {
        public Existing {
            options = options == null ? Map.of() : Map.copyOf(options);
        }
    }

    public record Create(Row row, String name) {
    }

    public record Update(Existing current, Row row, String name) {
    }

    public record Removal(Existing current, boolean deactivate) {
    }

    public record Plan(List<Create> creates, List<Update> updates, List<Removal> removals) {
    }

    public static Plan plan(List<Axis> axes, List<Row> rows, List<Existing> existing) {
        checkAxes(axes);
        if (rows.size() > MAX_VARIANTS) {
            throw new VariantRuleViolation("Máximo " + MAX_VARIANTS + " variantes por producto (llegaron " + rows.size() + ").");
        }
        Map<Long, Existing> byChild = new HashMap<>();
        Map<Map<Long, Long>, Existing> byCombination = new HashMap<>();
        for (Existing e : existing) {
            byChild.put(e.childId(), e);
            Map<Long, Long> key = keyFor(axes, e.options());
            if (key != null) {
                byCombination.putIfAbsent(key, e);
            }
        }

        List<Create> creates = new ArrayList<>();
        List<Update> updates = new ArrayList<>();
        Set<Long> usedChildren = new HashSet<>();
        Set<Map<Long, Long>> combinations = new HashSet<>();
        for (Row row : rows) {
            Map<Long, Long> key = checkRow(axes, row);
            if (!combinations.add(key)) {
                throw new VariantRuleViolation("La combinación " + describe(axes, key) + " está repetida.");
            }
            Existing target = null;
            if (row.childId() != null) {
                target = byChild.get(row.childId());
                if (target == null) {
                    throw new VariantRuleViolation("La variante " + row.childId() + " no pertenece al producto.");
                }
            } else {
                Existing candidate = byCombination.get(key);
                if (candidate != null && !usedChildren.contains(candidate.childId())) {
                    target = candidate;
                }
            }
            String name = nameFor(axes, key);
            if (target == null) {
                creates.add(new Create(row, name));
            } else {
                if (!usedChildren.add(target.childId())) {
                    throw new VariantRuleViolation("La variante " + target.childId() + " aparece dos veces en la matriz.");
                }
                updates.add(new Update(target, row, name));
            }
        }

        List<Removal> removals = new ArrayList<>();
        for (Existing e : existing) {
            if (!usedChildren.contains(e.childId())) {
                removals.add(new Removal(e, e.hasHistory()));
            }
        }
        // Una variante desactivada conserva su combinación: no puede chocar con una nueva.
        for (Removal r : removals) {
            Map<Long, Long> key = keyFor(axes, r.current().options());
            if (r.deactivate() && key != null && combinations.contains(key)) {
                throw new VariantRuleViolation("La combinación " + describe(axes, key)
                        + " ya existe en una variante con historial: actívala en lugar de crear otra.");
            }
        }
        return new Plan(List.copyOf(creates), List.copyOf(updates), List.copyOf(removals));
    }

    /** "Rojo / M" en el orden de los ejes. */
    public static String nameFor(List<Axis> axes, Map<Long, Long> options) {
        List<String> parts = new ArrayList<>();
        for (Axis axis : axes) {
            Long optionId = options.get(axis.attributeId());
            if (optionId != null) {
                parts.add(axis.options().getOrDefault(optionId, String.valueOf(optionId)));
            }
        }
        String name = String.join(" / ", parts);
        return name.length() > 255 ? name.substring(0, 255) : name;
    }

    /** Código sugerido: base + abreviaturas de los valores en el orden de los ejes. */
    public static String suggestCode(String base, List<Axis> axes, Map<Long, Long> options) {
        StringBuilder code = new StringBuilder(VariantCodes.normalizeCode(base));
        for (Axis axis : axes) {
            Long optionId = options.get(axis.attributeId());
            if (optionId != null) {
                code.append('-').append(VariantCodes.abbreviation(axis.options().get(optionId)));
            }
        }
        return code.length() > 64 ? code.substring(0, 64) : code.toString();
    }

    private static void checkAxes(List<Axis> axes) {
        if (axes == null || axes.isEmpty()) {
            throw new VariantRuleViolation("Elige al menos un eje de variante (talla, color, volumen…).");
        }
        if (axes.size() > MAX_AXES) {
            throw new VariantRuleViolation("Máximo " + MAX_AXES + " ejes de variante.");
        }
        Set<Long> seen = new HashSet<>();
        for (Axis axis : axes) {
            if (!seen.add(axis.attributeId())) {
                throw new VariantRuleViolation("El eje \"" + axis.name() + "\" está repetido.");
            }
            if (axis.options().isEmpty()) {
                throw new VariantRuleViolation("Elige al menos un valor para \"" + axis.name() + "\".");
            }
        }
    }

    private static Map<Long, Long> checkRow(List<Axis> axes, Row row) {
        Map<Long, Long> key = new TreeMap<>();
        for (Axis axis : axes) {
            Long optionId = row.options().get(axis.attributeId());
            if (optionId == null) {
                throw new VariantRuleViolation("A una variante le falta el valor de \"" + axis.name() + "\".");
            }
            if (!axis.options().containsKey(optionId)) {
                throw new VariantRuleViolation("\"" + axis.name() + "\": el valor de una variante no está entre los elegidos.");
            }
            key.put(axis.attributeId(), optionId);
        }
        if (row.options().size() != axes.size()) {
            throw new VariantRuleViolation("Una variante tiene valores de ejes que no están en la matriz.");
        }
        String label = describe(axes, key);
        if (row.price() == null || row.price().signum() <= 0) {
            throw new VariantRuleViolation("Indica el precio de " + label + " (mayor que 0).");
        }
        if (row.cost() != null && row.cost().signum() < 0) {
            throw new VariantRuleViolation("El costo de " + label + " no puede ser negativo.");
        }
        if (row.compareAt() != null && row.compareAt().compareTo(row.price()) <= 0) {
            throw new VariantRuleViolation("El precio antes de " + label + " debe ser mayor que el precio de venta.");
        }
        if (row.initialStock() < 0) {
            throw new VariantRuleViolation("El stock inicial de " + label + " no puede ser negativo.");
        }
        if (row.skuCode() != null && !row.skuCode().isBlank() && !VariantCodes.validCode(VariantCodes.normalizeCode(row.skuCode()))) {
            throw new VariantRuleViolation("El SKU de " + label + " usa letras, números, punto, guion o guion bajo (2 a 64).");
        }
        if (row.barcode() != null && !row.barcode().isBlank() && !VariantCodes.validBarcode(row.barcode().trim())) {
            throw new VariantRuleViolation("El código de barras de " + label + " tiene de 4 a 32 letras, números o guiones.");
        }
        return key;
    }

    /** Combinación restringida a los ejes; null si a la variante le falta alguno. */
    private static Map<Long, Long> keyFor(List<Axis> axes, Map<Long, Long> options) {
        Map<Long, Long> key = new TreeMap<>();
        for (Axis axis : axes) {
            Long optionId = options.get(axis.attributeId());
            if (optionId == null) {
                return null;
            }
            key.put(axis.attributeId(), optionId);
        }
        return key;
    }

    private static String describe(List<Axis> axes, Map<Long, Long> key) {
        String name = nameFor(axes, key);
        return name.isEmpty() ? "(sin valores)" : "\"" + name + "\"";
    }
}
