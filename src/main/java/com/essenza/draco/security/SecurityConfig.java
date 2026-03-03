// package com.essenza.tienda.security;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.CorsConfigurationSource;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// import java.util.List;

// @Configuration
// @EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
// public class SecurityConfig {

//     private static final String[] PUBLIC_ENDPOINTS = {
//             "/v2/api-docs",
//             "/swagger-resources/configuration/ui",
//             "/swagger-resources",
//             "/swagger-resources/configuration/security",
//             "/swagger-ui.html",
//             "/webjars/**",
//             "/swagger/**",
//             "/swagger-ui/**",
//             "/graphql",
//             "/graphiql",
//             "/vendor/**",
//     };

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             .csrf(csrf -> csrf.disable())
//             .cors(cors -> {})
//             .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
//                 .anyRequest().permitAll()
//             );
//         return http.build();
//     }

//     @Bean
//     public CorsConfigurationSource corsConfigurationSource() {
//         CorsConfiguration config = new CorsConfiguration();
//         config.setAllowedOrigins(List.of("*"));
//         config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
//         config.setAllowedHeaders(List.of("*"));
//         config.setAllowCredentials(false);

//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/graphql", config);
//         source.registerCorsConfiguration("/graphiql", config);
//         source.registerCorsConfiguration("/swagger-ui/**", config);
//         source.registerCorsConfiguration("/v3/api-docs/**", config);
//         return source;
//     }
// }
