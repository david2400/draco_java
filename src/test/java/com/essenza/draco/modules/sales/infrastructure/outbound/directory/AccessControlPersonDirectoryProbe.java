package com.essenza.draco.modules.sales.infrastructure.outbound.directory;

import com.fasterxml.jackson.databind.JsonNode;

import com.essenza.draco.modules.sales.application.output.gateway.PersonDirectory;

/** Acceso de prueba a los helpers package-private. */
public final class AccessControlPersonDirectoryProbe {
    private AccessControlPersonDirectoryProbe() {
    }

    public static PersonDirectory.Person toPerson(JsonNode node) {
        return AccessControlPersonDirectory.toPerson(node);
    }

    public static String lookupUrl(Long companyId, String docType, String docNumber) {
        return new AccessControlPersonDirectory("http://ac/", "", "svc", "x").lookupUrl(companyId, docType, docNumber);
    }

    public static PersonDirectory.Result find(Long companyId, String secret) {
        return new AccessControlPersonDirectory("http://127.0.0.1:1", "", "svc", secret).findByDocument(companyId, "CC", "123");
    }
}
