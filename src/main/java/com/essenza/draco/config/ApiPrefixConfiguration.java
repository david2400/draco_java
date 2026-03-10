package com.essenza.draco.config;

import org.springframework.boot.webmvc.autoconfigure.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Set;

@Configuration
public class ApiPrefixConfiguration implements WebMvcRegistrations {

    private static final String API_PREFIX = "/api/shop";
    private static final String BASE_PACKAGE = "com.essenza";
    private static final String CONFIG_PACKAGE = "com.essenza.draco.config";
    private static final String SPRINGDOC_PACKAGE = "org.springdoc";

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new RequestMappingHandlerMapping() {
            @Override
            protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
                if (mapping != null && shouldApplyPrefix(method, mapping)) {
                    RequestMappingInfo prefix = RequestMappingInfo.paths(API_PREFIX).build();
                    mapping = prefix.combine(mapping);
                }
                super.registerHandlerMethod(handler, method, mapping);
            }
        };
    }

    private boolean shouldApplyPrefix(Method method, RequestMappingInfo mappingInfo) {
        if (method == null || mappingInfo == null) {
            return false;
        }

        Class<?> declaringClass = method.getDeclaringClass();
        String packageName = declaringClass.getPackageName();

        if (!packageName.startsWith(BASE_PACKAGE) ||
                packageName.startsWith(SPRINGDOC_PACKAGE) ||
                packageName.startsWith(CONFIG_PACKAGE)) {
            return false;
        }

        Set<String> patterns = mappingInfo.getPatternValues();
        if (patterns == null || patterns.isEmpty()) {
            return true;
        }

        return patterns.stream().noneMatch(pattern -> pattern != null && pattern.startsWith(API_PREFIX));
    }
}
