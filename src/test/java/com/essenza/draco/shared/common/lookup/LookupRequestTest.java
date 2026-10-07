package com.essenza.draco.shared.common.lookup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class LookupRequestTest {

    @Test
    void parsesIdsAndIgnoresGarbage() {
        LookupRequest r = LookupRequest.of(" cam ", "3, 5,x,,5,-1", null);
        assertThat(r.ids()).containsExactly(3L, 5L);
        assertThat(r.byIds()).isTrue();
        assertThat(r.q()).isEqualTo("cam");
    }

    @Test
    void clampsLimit() {
        assertThat(LookupRequest.of(null, null, 0).limit()).isEqualTo(1);
        assertThat(LookupRequest.of(null, null, 999).limit()).isEqualTo(LookupRequest.MAX_LIMIT);
        assertThat(LookupRequest.of(null, null, null).limit()).isEqualTo(LookupRequest.DEFAULT_LIMIT);
        assertThat(LookupRequest.of(null, "1,2,3", 1).limit()).isEqualTo(3);
    }

    @Test
    void escapesLikeWildcards() {
        LookupRequest r = new LookupRequest("50%_Off\\", List.of(), 10);
        assertThat(r.containsPattern()).isEqualTo("%50\\%\\_off\\\\%");
        assertThat(r.prefixPattern()).isEqualTo("50\\%\\_off\\\\%");
        assertThat(r.hasText()).isTrue();
        assertThat(new LookupRequest("  ", null, 10).hasText()).isFalse();
    }

    @Test
    void buildsSqlWithIdsOrText() {
        NativeLookupSql byIds = new NativeLookupSql("SELECT b.id FROM brands b").where("b.deleted = 0")
                .match(LookupRequest.of("x", "7", 5), "b.id", "b.name").orderBy("b.name");
        assertThat(byIds.sql(5)).isEqualTo("SELECT b.id FROM brands b WHERE b.deleted = 0 AND b.id IN (:ids) ORDER BY b.name LIMIT 5");
        assertThat(byIds.params()).containsOnlyKeys("ids");

        NativeLookupSql byText = new NativeLookupSql("SELECT b.id FROM brands b")
                .match(LookupRequest.of("Ab", null, 5), "b.id", "b.name", "b.slug");
        assertThat(byText.sql(5)).contains("(LOWER(b.name) LIKE :q OR LOWER(b.slug) LIKE :q)")
                .contains("ORDER BY CASE WHEN LOWER(b.name) LIKE :qp THEN 0 ELSE 1 END");
        assertThat(byText.params()).containsEntry("q", "%ab%").containsEntry("qp", "ab%");
    }
}
