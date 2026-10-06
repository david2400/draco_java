package com.essenza.draco.modules.catalog.domain.model.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Reglas de la ficha técnica y de los ejes de variante de un producto.
 *
 * <ul>
 *   <li>Con plantilla: solo se aceptan sus atributos; los ejes van en los SKU, no en la ficha.</li>
 *   <li>Sin plantilla: ficha libre con cualquier atributo existente y sin ejes de variante.</li>
 *   <li>Dos variantes no pueden tener la misma combinación completa de opciones.</li>
 *   <li>Al publicar (ACTIVE): obligatorios con valor y cada variante con todos los ejes.</li>
 * </ul>
 */
public final class ProductAttributesPolicy {

    private ProductAttributesPolicy() {
    }

    public record Result(List<AttributeValue> values, List<VariantSelection> variants) {
    }

    /**
     * Valida y normaliza lo que se va a guardar.
     *
     * @param template    plantilla del producto o {@code null}
     * @param definitions atributos implicados, por id
     * @param variantSkus SKU de variante del producto (ids)
     * @param publishing  el producto está publicado: se exigen obligatorios y ejes completos
     */
    public static Result check(ProductTemplate template, Map<Long, Attribute> definitions,
                               List<AttributeValue> values, List<VariantSelection> variants,
                               Set<Long> variantSkus, boolean publishing) {
        List<AttributeValue> cleanValues = checkValues(template, definitions, values);
        List<VariantSelection> cleanVariants = checkVariants(template, definitions, variants, variantSkus);
        if (publishing) {
            List<String> missing = missing(template, definitions, cleanValues, cleanVariants, variantSkus);
            if (!missing.isEmpty()) {
                throw new AttributeRuleViolation("El producto está publicado y le falta: " + String.join("; ", missing) + ".");
            }
        }
        return new Result(cleanValues, cleanVariants);
    }

    /** Lo que falta para poder publicar (lista vacía si está completo). */
    public static List<String> missing(ProductTemplate template, Map<Long, Attribute> definitions,
                                       List<AttributeValue> values, List<VariantSelection> variants,
                                       Set<Long> variantSkus) {
        List<String> missing = new ArrayList<>();
        if (template == null) {
            return missing;
        }
        Set<Long> filled = new HashSet<>();
        values.stream().filter(value -> !value.isEmpty()).forEach(value -> filled.add(value.attributeId()));
        for (TemplateAttribute item : template.sheetAttributes()) {
            if (item.required() && !filled.contains(item.attributeId())) {
                missing.add(nameOf(definitions, item.attributeId()));
            }
        }
        List<TemplateAttribute> axes = template.axes();
        if (!axes.isEmpty()) {
            Map<Long, VariantSelection> bySku = new HashMap<>();
            variants.forEach(variant -> bySku.put(variant.skuId(), variant));
            int incomplete = 0;
            for (Long skuId : variantSkus) {
                VariantSelection selection = bySku.get(skuId);
                boolean complete = selection != null && axes.stream()
                        .allMatch(axis -> selection.optionsByAttribute().containsKey(axis.attributeId()));
                if (!complete) {
                    incomplete++;
                }
            }
            if (incomplete > 0) {
                missing.add(incomplete == 1 ? "1 variante sin todos los ejes" : incomplete + " variantes sin todos los ejes");
            }
        }
        return missing;
    }

    private static List<AttributeValue> checkValues(ProductTemplate template, Map<Long, Attribute> definitions,
                                                    List<AttributeValue> values) {
        Map<Long, AttributeValue> result = new LinkedHashMap<>();
        for (AttributeValue value : values == null ? List.<AttributeValue>of() : values) {
            if (value.isEmpty()) {
                continue;
            }
            Attribute definition = definitions.get(value.attributeId());
            if (definition == null) {
                throw new AttributeRuleViolation("El atributo " + value.attributeId() + " no existe.");
            }
            if (template != null) {
                TemplateAttribute item = template.find(value.attributeId()).orElseThrow(() ->
                        new AttributeRuleViolation("\"" + definition.getName() + "\" no pertenece a la plantilla."));
                if (item.variantAxis()) {
                    throw new AttributeRuleViolation(
                            "\"" + definition.getName() + "\" es eje de variante: se asigna en cada variante, no en la ficha.");
                }
            }
            if (result.put(value.attributeId(), value.normalizedFor(definition)) != null) {
                throw new AttributeRuleViolation("\"" + definition.getName() + "\" está repetido.");
            }
        }
        return List.copyOf(result.values());
    }

    private static List<VariantSelection> checkVariants(ProductTemplate template, Map<Long, Attribute> definitions,
                                                        List<VariantSelection> variants, Set<Long> variantSkus) {
        List<VariantSelection> result = new ArrayList<>();
        Map<Map<Long, Long>, Long> combinations = new HashMap<>();
        Set<Long> seenSkus = new HashSet<>();
        Set<Long> axisIds = new HashSet<>();
        if (template != null) {
            template.axes().forEach(axis -> axisIds.add(axis.attributeId()));
        }
        for (VariantSelection variant : variants == null ? List.<VariantSelection>of() : variants) {
            if (!variantSkus.contains(variant.skuId())) {
                throw new AttributeRuleViolation("El SKU " + variant.skuId() + " no es una variante de este producto.");
            }
            if (!seenSkus.add(variant.skuId())) {
                throw new AttributeRuleViolation("La variante " + variant.skuId() + " está repetida.");
            }
            Map<Long, Long> options = new TreeMap<>();
            variant.optionsByAttribute().forEach((attributeId, optionId) -> {
                if (optionId == null) {
                    return;
                }
                Attribute definition = definitions.get(attributeId);
                if (definition == null || !axisIds.contains(attributeId)) {
                    String name = definition == null ? String.valueOf(attributeId) : definition.getName();
                    throw new AttributeRuleViolation("\"" + name + "\" no es un eje de variante de la plantilla.");
                }
                if (definition.option(optionId).isEmpty()) {
                    throw new AttributeRuleViolation(
                            "\"" + definition.getName() + "\": la opción elegida no pertenece al atributo.");
                }
                options.put(attributeId, optionId);
            });
            if (!options.isEmpty() && options.size() == axisIds.size()) {
                Long other = combinations.putIfAbsent(Map.copyOf(options), variant.skuId());
                if (other != null) {
                    throw new AttributeRuleViolation("Dos variantes tienen la misma combinación de opciones ("
                            + describe(definitions, options) + ").");
                }
            }
            result.add(new VariantSelection(variant.skuId(), options));
        }
        return result;
    }

    private static String describe(Map<Long, Attribute> definitions, Map<Long, Long> options) {
        List<String> parts = new ArrayList<>();
        options.forEach((attributeId, optionId) -> {
            Attribute definition = definitions.get(attributeId);
            parts.add(definition.getName() + ": " + definition.option(optionId).map(AttributeOption::value).orElse("?"));
        });
        return String.join(", ", parts);
    }

    private static String nameOf(Map<Long, Attribute> definitions, Long attributeId) {
        Attribute definition = definitions.get(attributeId);
        return definition == null ? "atributo " + attributeId : definition.getName();
    }
}
