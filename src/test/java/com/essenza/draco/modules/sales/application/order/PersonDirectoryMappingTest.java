package com.essenza.draco.modules.sales.application.order;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.essenza.draco.modules.sales.application.output.gateway.PersonDirectory;
import com.essenza.draco.modules.sales.application.services.order.OrderInsightsServiceProbe;
import com.essenza.draco.modules.sales.infrastructure.outbound.directory.AccessControlPersonDirectoryProbe;

class PersonDirectoryMappingTest {

    @Test
    void leeLaPersonaEnCamelCaseOSnakeCaseSinDatosSensibles() throws Exception {
        String camel = "{\"clientId\":7,\"typeId\":\"CC\",\"cardId\":\"101\",\"fullName\":\"Marta Gómez\",\"status\":\"ACTIVE\","
                + "\"user\":{\"userId\":21,\"username\":\"marta@x.co\",\"status\":\"ACTIVE\"}}";
        PersonDirectory.Person a = AccessControlPersonDirectoryProbe.toPerson(new ObjectMapper().readTree(camel));
        assertThat(a.clientId()).isEqualTo(7L);
        assertThat(a.fullName()).isEqualTo("Marta Gómez");
        assertThat(a.userId()).isEqualTo(21L);
        assertThat(a.username()).isEqualTo("marta@x.co");

        String snake = "{\"client_id\":8,\"type_id\":\"NI\",\"card_id\":\"900\",\"full_name\":\"Acme\",\"user\":null}";
        PersonDirectory.Person b = AccessControlPersonDirectoryProbe.toPerson(new ObjectMapper().readTree(snake));
        assertThat(b.clientId()).isEqualTo(8L);
        assertThat(b.typeId()).isEqualTo("NI");
        assertThat(b.userId()).isNull();
    }

    @Test
    void mapeaLosTiposDeDocumentoDelRegistroCentral() {
        assertThat(OrderInsightsServiceProbe.storeDocType("cc")).isEqualTo("CC");
        assertThat(OrderInsightsServiceProbe.storeDocType("NI")).isEqualTo("NIT");
        assertThat(OrderInsightsServiceProbe.storeDocType("PE")).isEqualTo("PEP");
        assertThat(OrderInsightsServiceProbe.storeDocType("XX")).isNull();
        assertThat(OrderInsightsServiceProbe.centralDocType("NIT")).isEqualTo("NI");
        assertThat(OrderInsightsServiceProbe.centralDocType("pep")).isEqualTo("PE");
        assertThat(OrderInsightsServiceProbe.centralDocType("CC")).isEqualTo("CC");
        assertThat(OrderInsightsServiceProbe.centralDocType(" ")).isNull();
    }

    @Test
    void consultaConTipoDeDocumentoYEmpresaDeLaTienda() {
        assertThat(AccessControlPersonDirectoryProbe.lookupUrl(12L, "CC", "10 17"))
                .isEqualTo("http://ac/api/access_control/persons/lookup?cardId=10+17&companyId=12&typeId=CC");
        assertThat(AccessControlPersonDirectoryProbe.lookupUrl(12L, null, "101"))
                .isEqualTo("http://ac/api/access_control/persons/lookup?cardId=101&companyId=12");
        assertThat(AccessControlPersonDirectoryProbe.find(null, "secreto").status()).isEqualTo(PersonDirectory.Status.DISABLED);
        assertThat(AccessControlPersonDirectoryProbe.find(0L, "secreto").status()).isEqualTo(PersonDirectory.Status.DISABLED);
        assertThat(AccessControlPersonDirectoryProbe.find(12L, "").status()).isEqualTo(PersonDirectory.Status.DISABLED);
    }
}
