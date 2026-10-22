package com.essenza.draco.modules.sales.application.output.gateway;

/**
 * Registro central de personas (servicio access_control): datos básicos e identidad del
 * usuario por tipo y número de documento, limitado a los usuarios asignados a la empresa de
 * la tienda. Solo lectura.
 */
public interface PersonDirectory {

    enum Status {
        /** La persona existe. */
        FOUND,
        /** El documento no está registrado con ese tipo o no es un usuario de la empresa. */
        NOT_FOUND,
        /** El servicio no respondió o rechazó la consulta. */
        UNAVAILABLE,
        /** La integración no está configurada (sin credenciales) o no se indicó la empresa. */
        DISABLED
    }

    record Person(Long clientId, String typeId, String cardId, String firstName, String secondName, String firstLastName,
                  String secondLastName, String fullName, String status, Long userId, String username, String userStatus) {
    }

    record Result(Status status, Person person, String detail) {

        public static Result of(Status status, String detail) {
            return new Result(status, null, detail);
        }
    }

    /**
     * @param companyId empresa (id_company de access_control) cuyos usuarios se consultan; null = DISABLED
     * @param docType   tipo en el código de access_control (CC, CE, TI, PP, NI, PE); null = cualquiera
     */
    Result findByDocument(Long companyId, String docType, String docNumber);
}
