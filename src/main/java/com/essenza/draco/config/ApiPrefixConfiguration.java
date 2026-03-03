package com.essenza.draco.config;

import org.springframework.boot.webmvc.autoconfigure.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

@Configuration
public class ApiPrefixConfiguration implements org.springframework.boot.webmvc.autoconfigure.WebMvcRegistrations {

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new RequestMappingHandlerMapping() {
            @Override
            protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
                // Añade prefijo si no está presente
                String prefix = "/api/shop";
                var newMapping = RequestMappingInfo
                        .paths(prefix)
                        .build()
                        .combine(mapping);
                super.registerHandlerMethod(handler, method, newMapping);
            }
        };
    }
}
