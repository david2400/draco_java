package com.essenza.draco.shared.common.lookup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pequeño constructor de SQL nativo para búsquedas ligeras: acumula condiciones y
 * parámetros con nombre. No depende de JPA; el adaptador ejecuta el resultado.
 */
public final class NativeLookupSql {

    private final String select;
    private final List<String> where = new ArrayList<>();
    private final List<String> order = new ArrayList<>();
    private final Map<String, Object> params = new LinkedHashMap<>();

    public NativeLookupSql(String select) {
        this.select = select;
    }

    public NativeLookupSql where(String condition) {
        where.add(condition);
        return this;
    }

    public NativeLookupSql param(String name, Object value) {
        params.put(name, value);
        return this;
    }

    public NativeLookupSql orderBy(String expression) {
        order.add(expression);
        return this;
    }

    /**
     * Aplica el filtro estándar: por ids si vienen; si no, texto "contiene" sobre
     * {@code columns} (en minúsculas) y orden "empieza por" sobre la primera columna.
     */
    public NativeLookupSql match(LookupRequest request, String idColumn, String... columns) {
        if (request.byIds()) {
            where(idColumn + " IN (:ids)");
            param("ids", request.ids());
        } else if (request.hasText()) {
            List<String> likes = new ArrayList<>();
            for (String column : columns) {
                likes.add("LOWER(" + column + ") LIKE :q");
            }
            where("(" + String.join(" OR ", likes) + ")");
            param("q", request.containsPattern());
            orderBy("CASE WHEN LOWER(" + columns[0] + ") LIKE :qp THEN 0 ELSE 1 END");
            param("qp", request.prefixPattern());
        }
        return this;
    }

    public String sql(int limit) {
        StringBuilder sb = new StringBuilder(select);
        if (!where.isEmpty()) {
            sb.append(" WHERE ").append(String.join(" AND ", where));
        }
        if (!order.isEmpty()) {
            sb.append(" ORDER BY ").append(String.join(", ", order));
        }
        sb.append(" LIMIT ").append(Math.max(1, limit));
        return sb.toString();
    }

    public Map<String, Object> params() {
        return params;
    }
}
