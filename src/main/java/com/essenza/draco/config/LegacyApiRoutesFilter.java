package com.essenza.draco.config;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Rutas antiguas del API (Fase 6): se reenvían internamente a su ruta nueva para no
 * romper clientes existentes, y la respuesta lleva {@code Deprecation: true} y
 * {@code Link: <ruta nueva>; rel="successor-version"}. No aparecen en Swagger.
 * Se retiran en la Fase 7.
 *
 * <table>
 *   <tr><th>Antigua</th><th>Nueva</th></tr>
 *   <tr><td>/inventory/products/**</td><td>/catalog/products/**</td></tr>
 *   <tr><td>/inventory/product_children/**</td><td>/catalog/variants/**</td></tr>
 *   <tr><td>/inventory/product_combos/**</td><td>/catalog/product_combos/**</td></tr>
 *   <tr><td>/inventory/inventory_movements/**</td><td>/inventory/stock/movements/**</td></tr>
 *   <tr><td>/inventory/stock (exacta)</td><td>/inventory/stock/levels</td></tr>
 * </table>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class LegacyApiRoutesFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LegacyApiRoutesFilter.class);
    private static final String API_PREFIX = "/api/shop";

    /** Ruta antigua → nueva. {@code exact} solo reescribe la ruta idéntica (sin subrutas). */
    record Route(String legacy, String target, boolean exact) {
        Optional<String> rewrite(String path) {
            if (path.equals(legacy)) {
                return Optional.of(target);
            }
            if (!exact && path.startsWith(legacy + "/")) {
                return Optional.of(target + path.substring(legacy.length()));
            }
            return Optional.empty();
        }
    }

    static final List<Route> ROUTES = List.of(
            new Route(API_PREFIX + "/inventory/products", API_PREFIX + "/catalog/products", false),
            new Route(API_PREFIX + "/inventory/product_children", API_PREFIX + "/catalog/variants", false),
            new Route(API_PREFIX + "/inventory/product_combos", API_PREFIX + "/catalog/product_combos", false),
            new Route(API_PREFIX + "/inventory/inventory_movements", API_PREFIX + "/inventory/stock/movements", false),
            new Route(API_PREFIX + "/inventory/stock", API_PREFIX + "/inventory/stock/levels", true));

    /** Ruta nueva para {@code path}, o vacío si no es una ruta antigua. */
    static Optional<String> successorOf(String path) {
        for (Route route : ROUTES) {
            Optional<String> target = route.rewrite(path);
            if (target.isPresent()) {
                return target;
            }
        }
        return Optional.empty();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        Optional<String> target = successorOf(path);
        if (target.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }
        log.debug("Ruta obsoleta {} {} → {}", request.getMethod(), path, target.get());
        response.setHeader("Deprecation", "true");
        response.setHeader("Link", "<" + request.getContextPath() + target.get() + ">; rel=\"successor-version\"");
        // Los parámetros de la URL original se conservan en el forward.
        request.getRequestDispatcher(target.get()).forward(request, response);
    }
}
