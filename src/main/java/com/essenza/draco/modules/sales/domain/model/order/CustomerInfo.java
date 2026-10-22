package com.essenza.draco.modules.sales.domain.model.order;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Cliente de la orden con sus datos de facturación (se congelan en la orden: cambiar el
 * cliente después no altera órdenes anteriores). {@code customerId} es el usuario de la
 * tienda si lo hay; sin él es un invitado.
 */
public record CustomerInfo(Long customerId, CustomerKind kind, String docType, String docNumber, String name,
                           String email, String phone, String billingAddress, String billingCity) {

    public static final Set<String> DOC_TYPES = Set.of("CC", "CE", "NIT", "PP", "TI", "PEP");
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^[+0-9()\\s-]{7,20}$");
    private static final Pattern DOC = Pattern.compile("^[A-Za-z0-9-]{3,20}$");

    public static final CustomerInfo EMPTY = new CustomerInfo(null, CustomerKind.PERSON, null, null, null, null, null, null, null);

    public static CustomerInfo of(Long customerId, String kind, String docType, String docNumber, String name, String email,
                                  String phone, String billingAddress, String billingCity) {
        String type = Texts.clean(docType);
        String number = Texts.clean(docNumber);
        if (number != null) {
            number = number.replace(" ", "").replace(".", "");
            if (!DOC.matcher(number).matches()) {
                throw new OrderRuleViolation("El documento del cliente usa letras, números o guion (3 a 20).");
            }
            type = type == null ? "CC" : type.toUpperCase(Locale.ROOT);
        } else if (type != null) {
            type = type.toUpperCase(Locale.ROOT);
        }
        if (type != null && !DOC_TYPES.contains(type)) {
            throw new OrderRuleViolation("Tipo de documento no válido: " + type + " (CC, CE, NIT, PP, TI o PEP).");
        }
        CustomerKind customerKind = CustomerKind.parse(kind);
        if ("NIT".equals(type) && kind == null) {
            customerKind = CustomerKind.COMPANY;
        }
        String mail = Texts.clean(email, 160, "Correo del cliente");
        if (mail != null) {
            mail = mail.toLowerCase(Locale.ROOT);
            if (!EMAIL.matcher(mail).matches()) {
                throw new OrderRuleViolation("El correo del cliente no es válido.");
            }
        }
        String tel = Texts.clean(phone, 40, "Teléfono del cliente");
        if (tel != null && !PHONE.matcher(tel).matches()) {
            throw new OrderRuleViolation("El teléfono del cliente tiene de 7 a 20 dígitos (se permiten +, espacios, guiones y paréntesis).");
        }
        return new CustomerInfo(customerId, customerKind, type, number, Texts.clean(name, 200, "Nombre del cliente"), mail, tel,
                Texts.clean(billingAddress, 200, "Dirección de facturación"), Texts.clean(billingCity, 120, "Ciudad de facturación"));
    }

    public boolean isEmpty() {
        return docNumber == null && name == null && email == null && phone == null;
    }

    /** Órdenes del panel: nombre y un medio de contacto. */
    public void requireContact() {
        if (name == null) {
            throw new OrderRuleViolation("Indica el nombre del cliente.");
        }
        if (email == null && phone == null) {
            throw new OrderRuleViolation("Indica el correo o el teléfono del cliente.");
        }
    }
}
