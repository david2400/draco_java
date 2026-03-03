//package com.essenza.tienda.config;
//
//import graphql.scalars.ExtendedScalars;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.graphql.execution.RuntimeWiringConfigurer;
//
//@Configuration
//public class GraphQLConfig {
//
//    /**
//     * Scalars personalizados (DateTime, BigDecimal, Long, JSON).
//     * Agregamos GraphQLLong para soportar campos Long en el schema (ej: PageInfo.totalElements).
//     */
//    @Bean
//    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
//        return wiringBuilder -> wiringBuilder
//                // .scalar(ExtendedScalars.DateTime)
//                // .scalar(ExtendedScalars.GraphQLBigDecimal)
//                .scalar(ExtendedScalars.GraphQLLong);
//                // .scalar(ExtendedScalars.Json);
//    }
//
//    /**
//     * Endurecimiento de consultas GraphQL para evitar abusos.
//     * Ajusta los límites según tu caso de uso.
//     */
////    @Bean
////    public GraphQlSourceBuilderCustomizer graphQlSourceBuilderCustomizer() {
////        return builder -> builder
////                .instrumentation(new MaxQueryDepthInstrumentation(20))
////                .instrumentation(new MaxQueryComplexityInstrumentation(500));
////    }
//}