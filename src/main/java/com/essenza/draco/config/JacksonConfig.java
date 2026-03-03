//package com.essenza.draco.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.PropertyNamingStrategies;
//import io.swagger.v3.core.jackson.ModelResolver;
//import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
//
//@Configuration
//public class JacksonConfig {
//    @Bean
//    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
//        return builder -> builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
//    }
//
//    @Bean
//    @Primary
//    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
//        return builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE).build();
//    }
//
//    @Bean
//    public ModelResolver modelResolver(ObjectMapper objectMapper) {
//        ObjectMapper openApiObjectMapper = objectMapper.copy();
//        openApiObjectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
//        return new ModelResolver(openApiObjectMapper);
//    }
//}