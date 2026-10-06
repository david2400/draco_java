package com.essenza.draco.architecture;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Reglas de arquitectura (fitness functions) del monolito modular.
 *
 * <p>Se verifican leyendo los {@code import} del código fuente, sin dependencias
 * extra. Si una regla falla, el mensaje indica archivo e import. Para aceptar
 * una excepción hay que añadirla a {@link #ALLOWED_CROSS_MODULE} con su motivo.
 *
 * <ol>
 *   <li>El dominio es Java puro: sin Spring, JPA, validación ni capas externas.</li>
 *   <li>La capa de aplicación no depende de infraestructura (usa puertos).</li>
 *   <li>Un módulo no importa clases de otro módulo, salvo excepciones explícitas.</li>
 *   <li>El producto vive en {@code catalog}, no en {@code inventory}.</li>
 * </ol>
 */
class ArchitectureRulesTest {

    private static final String MODULES = "com.essenza.draco.modules.";
    private static final Pattern IMPORT = Pattern.compile("^import\\s+(?:static\\s+)?([\\w.]+)(?:\\.\\*)?;", Pattern.MULTILINE);
    private static final Pattern PACKAGE = Pattern.compile("^package\\s+([\\w.]+);", Pattern.MULTILINE);

    /**
     * Dependencias entre módulos aceptadas (deuda conocida), como prefijo
     * "origen -> paquete importado".
     */
    private static final Set<String> ALLOWED_CROSS_MODULE = Set.of(
            // Lectura analítica sobre las tablas de ventas (adaptador de salida de analytics).
            // Pendiente: exponer un puerto de consulta en sales.
            "analytics -> com.essenza.draco.modules.sales.infrastructure.outbound"
    );

    private static List<SourceFile> sources;

    record SourceFile(Path path, String pkg, List<String> imports) {
        String module() {
            return moduleOf(pkg);
        }

        boolean inLayer(String layer) {
            return pkg.startsWith(MODULES) && pkg.contains("." + layer + ".") || pkg.endsWith("." + layer);
        }
    }

    @BeforeAll
    static void load() throws IOException {
        Path root = Paths.get("src", "main", "java");
        assertThat(root).as("ejecutar desde la raíz del proyecto").isDirectory();
        try (Stream<Path> files = Files.walk(root)) {
            sources = files.filter(p -> p.toString().endsWith(".java")).map(ArchitectureRulesTest::parse).toList();
        }
        assertThat(sources).isNotEmpty();
    }

    private static SourceFile parse(Path path) {
        try {
            String code = Files.readString(path, StandardCharsets.UTF_8);
            Matcher pkg = PACKAGE.matcher(code);
            List<String> imports = new ArrayList<>();
            Matcher m = IMPORT.matcher(code);
            while (m.find()) {
                imports.add(m.group(1));
            }
            return new SourceFile(path, pkg.find() ? pkg.group(1) : "", imports);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String moduleOf(String pkg) {
        if (!pkg.startsWith(MODULES)) {
            return null;
        }
        String rest = pkg.substring(MODULES.length());
        int dot = rest.indexOf('.');
        return dot < 0 ? rest : rest.substring(0, dot);
    }

    private static List<String> violations(java.util.function.Predicate<SourceFile> scope,
                                           java.util.function.BiPredicate<SourceFile, String> forbidden) {
        List<String> found = new ArrayList<>();
        for (SourceFile file : sources) {
            if (!scope.test(file)) {
                continue;
            }
            for (String imp : file.imports()) {
                if (forbidden.test(file, imp)) {
                    found.add(file.path() + " -> " + imp);
                }
            }
        }
        return found;
    }

    @Test
    @DisplayName("El dominio no depende de frameworks ni de otras capas")
    void domainIsFrameworkFree() {
        List<String> found = violations(
                f -> f.inLayer("domain"),
                (f, imp) -> imp.startsWith("org.springframework")
                        || imp.startsWith("jakarta.persistence")
                        || imp.startsWith("jakarta.validation")
                        || imp.startsWith("org.hibernate")
                        || imp.contains(".application.")
                        || imp.contains(".infrastructure."));
        assertThat(found).as("imports prohibidos en domain").isEmpty();
    }

    @Test
    @DisplayName("La aplicación depende de puertos, no de infraestructura")
    void applicationDoesNotDependOnInfrastructure() {
        List<String> found = violations(
                f -> f.inLayer("application"),
                (f, imp) -> imp.startsWith(MODULES) && imp.contains(".infrastructure."));
        assertThat(found).as("la aplicación importa infraestructura").isEmpty();
    }

    @Test
    @DisplayName("Los módulos no se importan entre sí (salvo excepciones documentadas)")
    void modulesAreIsolated() {
        List<String> found = violations(
                f -> f.module() != null,
                (f, imp) -> {
                    String target = moduleOf(imp);
                    if (target == null || target.equals(f.module())) {
                        return false;
                    }
                    return ALLOWED_CROSS_MODULE.stream().noneMatch(rule -> {
                        String[] parts = rule.split(" -> ");
                        return parts[0].equals(f.module()) && imp.startsWith(parts[1]);
                    });
                });
        assertThat(found).as("dependencias entre módulos no permitidas").isEmpty();
    }

    @Test
    @DisplayName("El producto pertenece al catálogo")
    void productLivesInCatalog() {
        List<String> found = sources.stream()
                .filter(f -> "inventory".equals(f.module()))
                .map(f -> f.path().getFileName().toString())
                .filter(name -> name.matches("Product.*\\.java|.*Combo.*\\.java"))
                .toList();
        assertThat(found).as("clases de producto dentro de inventory").isEmpty();
    }
}
