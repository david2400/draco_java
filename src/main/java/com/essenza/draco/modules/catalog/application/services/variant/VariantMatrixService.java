package com.essenza.draco.modules.catalog.application.services.variant;

import com.essenza.draco.modules.catalog.application.dto.variant.CreateAxisOptionDto;
import com.essenza.draco.modules.catalog.application.dto.variant.SaveVariantMatrixDto;
import com.essenza.draco.modules.catalog.application.dto.variant.SaveVariantMatrixResultDto;
import com.essenza.draco.modules.catalog.application.dto.variant.VariantMatrixDto;
import com.essenza.draco.modules.catalog.application.input.variant.VariantMatrixUseCase;
import com.essenza.draco.modules.catalog.application.output.repository.AttributeRepository;
import com.essenza.draco.modules.catalog.application.output.repository.ProductAttributesStore;
import com.essenza.draco.modules.catalog.application.output.repository.ProductTemplateRepository;
import com.essenza.draco.modules.catalog.application.output.repository.VariantMatrixStore;
import com.essenza.draco.modules.catalog.application.output.repository.VariantMatrixStore.AxisRecord;
import com.essenza.draco.modules.catalog.application.output.repository.VariantMatrixStore.ChildData;
import com.essenza.draco.modules.catalog.application.output.repository.VariantMatrixStore.ProductInfo;
import com.essenza.draco.modules.catalog.application.output.repository.VariantMatrixStore.VariantRecord;
import com.essenza.draco.modules.catalog.domain.model.attribute.Attribute;
import com.essenza.draco.modules.catalog.domain.model.attribute.AttributeOption;
import com.essenza.draco.modules.catalog.domain.model.attribute.ProductTemplate;
import com.essenza.draco.modules.catalog.domain.model.attribute.TemplateAttribute;
import com.essenza.draco.modules.catalog.domain.model.attribute.VariantSelection;
import com.essenza.draco.modules.catalog.domain.model.variant.VariantCodes;
import com.essenza.draco.modules.catalog.domain.model.variant.VariantPlanner;
import com.essenza.draco.modules.catalog.domain.model.variant.VariantRuleViolation;
import com.essenza.draco.shared.common.inventory.StockEntryRequested;
import com.essenza.draco.shared.exceptions.ConflictException;
import com.essenza.draco.shared.exceptions.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generador de variantes: ejes (los de la plantilla más los que se agregan al producto), valores,
 * matriz de combinaciones con precio, costo, precio antes, SKU, código de barras, imagen y stock
 * inicial, todo guardado en una sola transacción.
 */
@Service
@Transactional
public class VariantMatrixService implements VariantMatrixUseCase {

    private final VariantMatrixStore store;
    private final ProductAttributesStore attributesStore;
    private final AttributeRepository attributes;
    private final ProductTemplateRepository templates;
    private final ApplicationEventPublisher events;

    public VariantMatrixService(VariantMatrixStore store, ProductAttributesStore attributesStore,
                                AttributeRepository attributes, ProductTemplateRepository templates,
                                ApplicationEventPublisher events) {
        this.store = store;
        this.attributesStore = attributesStore;
        this.attributes = attributes;
        this.templates = templates;
        this.events = events;
    }

    // ── Lectura ─────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public VariantMatrixDto get(Long productId) {
        ProductInfo product = product(productId);
        ProductTemplate template = template(product);
        Map<Long, TemplateAttribute> templateAxes = templateAxes(template);
        List<AxisRecord> axes = store.axes(productId);
        if (axes.isEmpty()) {
            axes = templateAxes.values().stream()
                    .sorted(Comparator.comparingInt(TemplateAttribute::position))
                    .map(axis -> new AxisRecord(axis.attributeId(), axis.position(), false))
                    .toList();
        }
        Map<Long, Attribute> optionAttributes = attributes.findAll().stream()
                .filter(Attribute::isOption)
                .collect(Collectors.toMap(Attribute::getId, Function.identity(), (a, b) -> a, LinkedHashMap::new));
        List<VariantRecord> variants = store.variants(productId);
        List<Long> skuIds = variants.stream().map(VariantRecord::skuId).filter(Objects::nonNull).toList();
        Map<Long, Map<Long, Long>> skuOptions = attributesStore.skuOptions(skuIds);
        Set<Long> history = store.withHistory(skuIds);
        Map<Long, String> images = store.optionImages(productId);

        List<AxisRecord> sortedAxes = axes.stream().filter(axis -> optionAttributes.containsKey(axis.attributeId()))
                .sorted(Comparator.comparingInt(AxisRecord::position)).toList();
        Set<Long> axisIds = sortedAxes.stream().map(AxisRecord::attributeId).collect(Collectors.toSet());
        Map<Long, Long> usage = new HashMap<>();
        skuOptions.values().forEach(options -> options.forEach((attributeId, optionId) -> usage.merge(optionId, 1L, Long::sum)));

        List<VariantMatrixDto.Axis> axisDtos = new ArrayList<>();
        for (AxisRecord axis : sortedAxes) {
            Attribute definition = optionAttributes.get(axis.attributeId());
            List<VariantMatrixDto.Option> options = definition.getOptions().stream()
                    .sorted(Comparator.comparingInt(AttributeOption::position))
                    .map(option -> new VariantMatrixDto.Option(option.id(), option.value(), option.position(),
                            usage.containsKey(option.id()), usage.getOrDefault(option.id(), 0L), images.get(option.id())))
                    .toList();
            axisDtos.add(new VariantMatrixDto.Axis(definition.getId(), definition.getCode(), definition.getName(),
                    templateAxes.containsKey(definition.getId()), axis.imageAxis(), axis.position(), options));
        }
        List<VariantMatrixDto.AttributeRef> available = optionAttributes.values().stream()
                .filter(attribute -> !axisIds.contains(attribute.getId()))
                .map(attribute -> new VariantMatrixDto.AttributeRef(attribute.getId(), attribute.getCode(), attribute.getName(),
                        attribute.getOptions().size(), templateAxes.containsKey(attribute.getId())))
                .toList();
        List<VariantMatrixDto.Variant> variantDtos = variants.stream().map(v -> {
            Map<Long, Long> options = v.skuId() == null ? Map.of() : skuOptions.getOrDefault(v.skuId(), Map.of());
            List<VariantMatrixDto.OptionRef> refs = options.entrySet().stream()
                    .filter(entry -> axisIds.contains(entry.getKey()))
                    .map(entry -> new VariantMatrixDto.OptionRef(entry.getKey(), entry.getValue()))
                    .toList();
            boolean incomplete = axisIds.isEmpty() || !options.keySet().containsAll(axisIds);
            return new VariantMatrixDto.Variant(v.childId(), v.skuId(), v.skuCode(), v.name(), refs, v.price(), v.cost(),
                    v.compareAt(), v.barcode(), v.imageUrl(), v.active(), v.stock(),
                    v.skuId() != null && history.contains(v.skuId()), incomplete);
        }).toList();
        return new VariantMatrixDto(product.id(), product.name(), product.price(), product.cost(), baseCode(productId),
                template == null ? null : template.getId(), template == null ? null : template.getName(), axisDtos,
                available, variantDtos);
    }

    // ── Guardado ────────────────────────────────────────────────────────────

    @Override
    public SaveVariantMatrixResultDto save(Long productId, SaveVariantMatrixDto input) {
        ProductInfo product = product(productId);
        if (product.combo()) {
            throw new VariantRuleViolation("Un combo no tiene variantes: sus componentes se definen en la pestaña Combo.");
        }
        // 1. Ejes
        List<Long> axisIds = input.axes().stream().map(SaveVariantMatrixDto.AxisInput::attributeId).toList();
        Map<Long, Attribute> definitions = attributes.findByIds(axisIds).stream()
                .collect(Collectors.toMap(Attribute::getId, Function.identity()));
        long imageAxes = input.axes().stream().filter(axis -> Boolean.TRUE.equals(axis.imageAxis())).count();
        if (imageAxes > 1) {
            throw new VariantRuleViolation("Solo un eje puede definir la imagen de las variantes.");
        }
        List<VariantPlanner.Axis> axes = new ArrayList<>();
        Long imageAxisId = null;
        for (SaveVariantMatrixDto.AxisInput axis : input.axes()) {
            Attribute definition = definitions.get(axis.attributeId());
            if (definition == null) {
                throw new NotFoundException("El atributo " + axis.attributeId() + " no existe.");
            }
            if (!definition.isOption()) {
                throw new VariantRuleViolation("\"" + definition.getName() + "\" no es de tipo opción: no puede ser eje de variante.");
            }
            Map<Long, String> options = new LinkedHashMap<>();
            definition.getOptions().stream().sorted(Comparator.comparingInt(AttributeOption::position))
                    .forEach(option -> options.put(option.id(), option.value()));
            axes.add(new VariantPlanner.Axis(definition.getId(), definition.getName(), options));
            if (Boolean.TRUE.equals(axis.imageAxis())) {
                imageAxisId = definition.getId();
            }
        }

        // 2. Imágenes por valor (solo de valores de los ejes)
        Set<Long> axisOptionIds = axes.stream().flatMap(axis -> axis.options().keySet().stream()).collect(Collectors.toSet());
        Map<Long, String> optionImages = new LinkedHashMap<>();
        for (SaveVariantMatrixDto.OptionImage image : input.optionImages() == null ? List.<SaveVariantMatrixDto.OptionImage>of() : input.optionImages()) {
            if (image.imageUrl() == null || image.imageUrl().isBlank()) {
                continue;
            }
            if (!axisOptionIds.contains(image.optionId())) {
                throw new VariantRuleViolation("La imagen del valor " + image.optionId() + " no corresponde a ningún eje.");
            }
            optionImages.put(image.optionId(), image.imageUrl().trim());
        }

        // 3. Plan contra las variantes existentes
        List<VariantRecord> existingRecords = store.variants(productId);
        Map<Long, VariantRecord> recordByChild = existingRecords.stream()
                .collect(Collectors.toMap(VariantRecord::childId, Function.identity()));
        List<Long> existingSkuIds = existingRecords.stream().map(VariantRecord::skuId).filter(Objects::nonNull).toList();
        Map<Long, Map<Long, Long>> existingOptions = attributesStore.skuOptions(existingSkuIds);
        Set<Long> history = store.withHistory(existingSkuIds);
        List<VariantPlanner.Existing> existing = existingRecords.stream()
                .map(r -> new VariantPlanner.Existing(r.childId(), r.skuId(),
                        r.skuId() == null ? Map.of() : existingOptions.getOrDefault(r.skuId(), Map.of()),
                        r.skuId() != null && history.contains(r.skuId())))
                .toList();
        List<VariantPlanner.Row> rows = input.variants().stream().map(row -> {
            Map<Long, Long> options = new HashMap<>();
            for (var ref : row.options()) {
                if (ref.attributeId() == null || ref.optionId() == null) {
                    throw new VariantRuleViolation("Cada valor de una variante necesita eje y opción.");
                }
                if (options.put(ref.attributeId(), ref.optionId()) != null) {
                    throw new VariantRuleViolation("Una variante repite un eje.");
                }
            }
            return new VariantPlanner.Row(row.childId(), options, row.price(), row.costPrice(), row.compareAtPrice(),
                    blank(row.skuCode()), blank(row.barcode()), blank(row.imageUrl()), row.active() == null || row.active(),
                    row.initialStock() == null ? 0 : row.initialStock());
        }).toList();
        VariantPlanner.Plan plan = VariantPlanner.plan(axes, rows, existing);

        // 4. Códigos y códigos de barras únicos
        Set<Long> keptSkuIds = plan.updates().stream().map(u -> u.current().skuId()).filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<String> seenCodes = new HashSet<>();
        Set<String> seenBarcodes = new HashSet<>();
        for (VariantPlanner.Row row : rows) {
            if (row.skuCode() != null && !seenCodes.add(VariantCodes.normalizeCode(row.skuCode()))) {
                throw new ConflictException("El SKU " + VariantCodes.normalizeCode(row.skuCode()) + " está repetido en la matriz.");
            }
            if (row.barcode() != null && !seenBarcodes.add(row.barcode())) {
                throw new ConflictException("El código de barras " + row.barcode() + " está repetido en la matriz.");
            }
            if (row.barcode() != null && store.barcodeTaken(row.barcode(), existingSkuIds)) {
                throw new ConflictException("El código de barras " + row.barcode() + " ya lo usa otro producto.");
            }
        }

        // 5. Ejecutar: quitar, actualizar, crear
        int deactivated = 0;
        int deleted = 0;
        for (VariantPlanner.Removal removal : plan.removals()) {
            if (removal.deactivate()) {
                store.setChildActive(removal.current().childId(), false);
                deactivated++;
            } else {
                store.deleteChild(removal.current().childId());
                deleted++;
            }
        }
        Map<Long, VariantPlanner.Row> rowByChild = new LinkedHashMap<>();
        for (VariantPlanner.Update update : plan.updates()) {
            store.updateChild(update.current().childId(), childData(update.row(), update.name(), imageAxisId, optionImages));
            rowByChild.put(update.current().childId(), update.row());
        }
        Map<Long, VariantPlanner.Row> created = new LinkedHashMap<>();
        for (VariantPlanner.Create create : plan.creates()) {
            Long childId = store.createChild(productId, childData(create.row(), create.name(), imageAxisId, optionImages));
            rowByChild.put(childId, create.row());
            created.put(childId, create.row());
        }
        Map<Long, Long> skuByChild = store.syncSkus(productId);

        // 6. SKU: código, código de barras, precio antes y valores de los ejes
        String base = baseCode(productId);
        List<VariantSelection> selections = new ArrayList<>();
        for (Map.Entry<Long, VariantPlanner.Row> entry : rowByChild.entrySet()) {
            Long skuId = skuByChild.get(entry.getKey());
            if (skuId == null) {
                continue;
            }
            VariantPlanner.Row row = entry.getValue();
            VariantRecord before = recordByChild.get(entry.getKey());
            String code;
            if (row.skuCode() != null) {
                code = VariantCodes.normalizeCode(row.skuCode());
                if (store.codeTaken(code, skuId)) {
                    throw new ConflictException("El SKU " + code + " ya existe.");
                }
            } else if (before != null && before.skuCode() != null) {
                code = null; // se conserva el código actual
            } else {
                code = uniqueCode(VariantPlanner.suggestCode(base, axes, row.options()), skuId);
            }
            store.updateSkuExtras(skuId, code, row.barcode(), row.compareAt());
            selections.add(new VariantSelection(skuId, row.options()));
        }
        attributesStore.replaceSkuOptions(selections.stream().map(VariantSelection::skuId).toList(), selections);

        List<AxisRecord> axisRecords = new ArrayList<>();
        for (int i = 0; i < axes.size(); i++) {
            Long attributeId = axes.get(i).attributeId();
            axisRecords.add(new AxisRecord(attributeId, i + 1, attributeId.equals(imageAxisId)));
        }
        store.replaceAxes(productId, axisRecords);
        store.replaceOptionImages(productId, optionImages);

        // 7. Stock inicial de las nuevas (el inventario registra la entrada)
        created.forEach((childId, row) -> {
            Long skuId = skuByChild.get(childId);
            if (skuId != null && row.initialStock() > 0) {
                events.publishEvent(new StockEntryRequested(productId, skuId, input.initialWarehouseId(), row.initialStock(),
                        "VARIANT_INITIAL"));
            }
        });

        return new SaveVariantMatrixResultDto(plan.creates().size(), plan.updates().size(), deactivated, deleted, get(productId));
    }

    @Override
    public VariantMatrixDto.Option createOption(Long productId, CreateAxisOptionDto input) {
        product(productId);
        Attribute attribute = attributes.findById(input.attributeId())
                .orElseThrow(() -> new NotFoundException("El atributo " + input.attributeId() + " no existe."));
        if (!attribute.isOption()) {
            throw new VariantRuleViolation("\"" + attribute.getName() + "\" no es de tipo opción.");
        }
        String value = input.value().trim();
        if (value.length() > 120) {
            throw new VariantRuleViolation("El valor tiene máximo 120 caracteres.");
        }
        Long optionId = store.ensureOption(attribute.getId(), value);
        AttributeOption option = attributes.findById(attribute.getId()).flatMap(a -> a.option(optionId))
                .orElseThrow(() -> new NotFoundException("No se pudo crear el valor " + value + "."));
        return new VariantMatrixDto.Option(option.id(), option.value(), option.position(), false, 0, null);
    }

    // ── Apoyo ───────────────────────────────────────────────────────────────

    private ProductInfo product(Long productId) {
        return store.product(productId).orElseThrow(() -> new NotFoundException("Producto no encontrado: " + productId));
    }

    private ProductTemplate template(ProductInfo product) {
        return product.templateId() == null ? null : templates.findById(product.templateId()).orElse(null);
    }

    private static Map<Long, TemplateAttribute> templateAxes(ProductTemplate template) {
        Map<Long, TemplateAttribute> out = new LinkedHashMap<>();
        if (template != null) {
            template.axes().forEach(axis -> out.put(axis.attributeId(), axis));
        }
        return out;
    }

    private static ChildData childData(VariantPlanner.Row row, String name, Long imageAxisId, Map<Long, String> optionImages) {
        String image = row.imageUrl();
        if (image == null && imageAxisId != null) {
            image = optionImages.get(row.options().get(imageAxisId));
        }
        return new ChildData(name, row.price(), row.cost(), image, row.active());
    }

    private String uniqueCode(String suggested, Long skuId) {
        String candidate = suggested;
        int suffix = 2;
        while (store.codeTaken(candidate, skuId)) {
            String tail = "-" + suffix++;
            candidate = (suggested.length() + tail.length() > 64 ? suggested.substring(0, 64 - tail.length()) : suggested) + tail;
        }
        return candidate;
    }

    static String baseCode(Long productId) {
        return String.format("SKU-%06d", productId);
    }

    private static String blank(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
