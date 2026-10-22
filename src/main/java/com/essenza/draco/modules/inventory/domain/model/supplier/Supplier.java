package com.essenza.draco.modules.inventory.domain.model.supplier;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Ficha empresarial del proveedor: identificación fiscal, contacto y ubicación, estado,
 * condiciones comerciales, cuenta bancaria y personas de contacto.
 *
 * @param name     razón social (persona jurídica) o nombre completo (persona natural)
 * @param city     nombre de la ciudad del catálogo de parametros (para listados y documentos)
 * @param contacts máximo 20; si hay contactos, exactamente uno queda como principal
 */
public record Supplier(Long id, String code, TaxId taxId, PersonType personType, String name, String tradeName,
                       TaxRegime taxRegime, boolean greatContributor, boolean selfRetainer,
                       String email, String phone, String website, String address,
                       Long countryId, Long stateId, Long cityId, String city,
                       SupplierStatus status, String blockReason, CommercialTerms terms, BankAccount bank, String notes,
                       List<SupplierContact> contacts) {

    public static final int MAX_CONTACTS = 20;

    public Supplier {
        code = code == null ? null : code.trim().toUpperCase(Locale.ROOT);
        if (code == null || !code.matches("[A-Z][A-Z0-9_]{1,29}")) {
            throw new SupplierRuleViolation("El código del proveedor usa mayúsculas, números y guion bajo (2 a 30 caracteres).");
        }
        if (name == null || name.isBlank()) {
            throw new SupplierRuleViolation("La razón social o el nombre del proveedor es obligatorio.");
        }
        name = name.trim();
        tradeName = blank(tradeName);
        email = blank(email);
        phone = blank(phone);
        website = blank(website);
        address = blank(address);
        city = blank(city);
        notes = blank(notes);
        status = status == null ? SupplierStatus.ACTIVE : status;
        terms = terms == null ? CommercialTerms.CASH : terms;
        blockReason = status == SupplierStatus.BLOCKED ? blank(blockReason) : null;

        if (personType == null) {
            personType = taxId != null && !"NIT".equals(taxId.type()) && !"NIT_EXT".equals(taxId.type())
                    ? PersonType.NATURAL : PersonType.JURIDICA;
        }
        if (personType == PersonType.JURIDICA && taxId != null && !"NIT".equals(taxId.type()) && !"NIT_EXT".equals(taxId.type())) {
            throw new SupplierRuleViolation("Una persona jurídica se identifica con NIT (o NIT extranjero).");
        }
        if (status == SupplierStatus.BLOCKED && blockReason == null) {
            throw new SupplierRuleViolation("Indica el motivo del bloqueo del proveedor.");
        }
        if (email != null && !isEmail(email)) {
            throw new SupplierRuleViolation("El correo del proveedor no es válido.");
        }
        if (website != null && !website.matches("(?i)(https?://)?[a-z0-9.-]+\\.[a-z]{2,}(/\\S*)?")) {
            throw new SupplierRuleViolation("El sitio web no es válido.");
        }
        if (cityId != null && stateId == null) {
            throw new SupplierRuleViolation("Elige el departamento de la ciudad.");
        }
        if (stateId != null && countryId == null) {
            throw new SupplierRuleViolation("Elige el país del departamento.");
        }
        contacts = normalize(contacts);
    }

    /** Ficha nueva o editada: el documento es obligatorio (los registros antiguos pueden no tenerlo). */
    public Supplier requireDocument() {
        if (taxId == null) {
            throw new SupplierRuleViolation("Indica el tipo y el número de documento del proveedor.");
        }
        return this;
    }

    public Supplier withStatus(SupplierStatus next, String reason) {
        return new Supplier(id, code, taxId, personType, name, tradeName, taxRegime, greatContributor, selfRetainer,
                email, phone, website, address, countryId, stateId, cityId, city, next, reason, terms, bank, notes, contacts);
    }

    public SupplierContact primaryContact() {
        return contacts.stream().filter(SupplierContact::primary).findFirst().orElse(null);
    }

    /** Nombre para listados: comercial si lo tiene, si no la razón social. */
    public String displayName() {
        return tradeName != null ? tradeName : name;
    }

    private static List<SupplierContact> normalize(List<SupplierContact> input) {
        if (input == null || input.isEmpty()) {
            return List.of();
        }
        if (input.size() > MAX_CONTACTS) {
            throw new SupplierRuleViolation("Máximo " + MAX_CONTACTS + " contactos por proveedor.");
        }
        long primaries = input.stream().filter(SupplierContact::primary).count();
        if (primaries > 1) {
            throw new SupplierRuleViolation("Solo un contacto puede ser el principal.");
        }
        List<SupplierContact> out = new ArrayList<>(input);
        if (primaries == 0) {
            out.set(0, out.get(0).asPrimary(true));
        }
        return List.copyOf(out);
    }

    static String blank(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    static boolean isEmail(String value) {
        return value.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");
    }
}
