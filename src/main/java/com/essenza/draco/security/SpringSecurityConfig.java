package com.essenza.draco.security;

import com.essenza.security.AccessControlAuthenticationFilter;
import com.essenza.security.AccessControlTokenValidator;
import com.essenza.security.AutoridadesMapper;
import com.essenza.security.EssenzaSecurityProperties;
import com.essenza.security.ObservacionAccesoFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Seguridad (Fase 4 del plan): valida el token de access_control con essenza-security (RS256 del
 * servidor de autorizacion y, en la transicion, el HMAC viejo).
 *
 * <p>Arranca en <b>modo observacion</b> ({@code DRACO_ENFORCE_AUTH=false}): no bloquea nada,
 * pero registra en el log cada ruta que se llama sin token valido. Como no se conocen todos los
 * clientes de este servicio, primero se observa, luego se marcan como publicas las rutas que deben
 * serlo ({@code essenza.security.rutas-publicas} / {@code rutas-publicas-get}) y despues se activa
 * con {@code DRACO_ENFORCE_AUTH=true}. Respaldo de la version anterior:
 * target/SpringSecurityConfig.java.antes-f4.
 */
@Configuration
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SpringSecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SpringSecurityConfig.class);

    /** Documentacion y errores: siempre publicos. */
    private static final List<String> SIEMPRE_PUBLICAS = List.of(
            "/error",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**");

    @Value("${cors.allowed-origins:*}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AccessControlTokenValidator validator,
                                                   AutoridadesMapper autoridades,
                                                   EssenzaSecurityProperties props) throws Exception {
        List<String> publicas = new ArrayList<>(SIEMPRE_PUBLICAS);
        publicas.addAll(props.getRutasPublicas());
        List<String> publicasGet = props.getRutasPublicasGet();

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Sin token valido: 401 (sin esto Spring respondia 403).
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterBefore(new AccessControlAuthenticationFilter(validator, autoridades),
                        UsernamePasswordAuthenticationFilter.class);

        if (props.isEnforceAuthentication()) {
            http.authorizeHttpRequests(auth -> {
                auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                auth.requestMatchers(publicas.toArray(String[]::new)).permitAll();
                if (!publicasGet.isEmpty()) {
                    auth.requestMatchers(HttpMethod.GET, publicasGet.toArray(String[]::new)).permitAll();
                }
                auth.anyRequest().authenticated();
            });
        } else {
            log.warn("Modo observacion: este servicio acepta peticiones SIN token y solo las registra."
                    + " Revisar el log y activar DRACO_ENFORCE_AUTH=true.");
            http.addFilterAfter(new ObservacionAccesoFilter(publicas, publicasGet), AccessControlAuthenticationFilter.class)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        }
        return http.build();
    }

    /**
     * Sin credenciales (la API usa el encabezado Authorization, no cookies). Origenes por
     * {@code CORS_ALLOWED_ORIGINS}; por defecto * como antes, para no romper clientes de navegador.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
