package com.essenza.draco.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@OpenAPIDefinition(
        info = @Info(
                title = "Essenza Tienda API",
                version = "v1"
        ),
        servers = {
                @Server(url = "/", description = "Default Server")
        }
)
@Configuration
public class OpenApiConfig {

    private static final String BASE_PREFIX = "/api/shop";

    @Bean
    public GroupedOpenApi analyticsApi() {
        return GroupedOpenApi.builder()
                .group("analytics")
                .displayName("Analytics")
                .pathsToMatch(BASE_PREFIX + "/analytics/**")
                .build();
    }

    @Bean
    public GroupedOpenApi reviewsApi() {
        return GroupedOpenApi.builder()
                .group("reviews")
                .displayName("Product Reviews")
                .pathsToMatch(BASE_PREFIX + "/reviews/**")
                .build();
    }

    @Bean
    public GroupedOpenApi promotionsApi() {
        return GroupedOpenApi.builder()
                .group("promotions")
                .displayName("Promotions & Coupons")
                .pathsToMatch(BASE_PREFIX + "/promotions/**")
                .build();
    }

    @Bean
    public GroupedOpenApi salesApi() {
        return GroupedOpenApi.builder()
                .group("sales")
                .displayName("Sales & Orders")
                .pathsToMatch(BASE_PREFIX + "/sales/**")
                .build();
    }

    @Bean
    public GroupedOpenApi inventoryApi() {
        return GroupedOpenApi.builder()
                .group("inventory")
                .displayName("Inventory & Catalog")
                .pathsToMatch(BASE_PREFIX + "/inventory/**", BASE_PREFIX + "/catalog/**")
                .build();
    }

    @Bean
    public GroupedOpenApi logisticsApi() {
        return GroupedOpenApi.builder()
                .group("shipping-logistics")
                .displayName("Shipping & Logistics")
                .pathsToMatch(BASE_PREFIX + "/dispatch/**", BASE_PREFIX + "/product_distribution/**")
                .build();
    }

    @Bean
    public GroupedOpenApi devolutionsApi() {
        return GroupedOpenApi.builder()
                .group("devolutions")
                .displayName("Devolutions & Returns")
                .pathsToMatch(BASE_PREFIX + "/devolution/**", BASE_PREFIX + "/return-methods/**")
                .build();
    }

    @Controller
    static class SwaggerUiRedirectController {

        @GetMapping({"/", ""})
        public String redirectRoot() {
            return "redirect:/swagger-ui/index.html";
        }

        @GetMapping({"/swagger-ui", "/swagger-ui/"})
        public String redirectSwaggerUi() {
            return "redirect:/swagger-ui/index.html";
        }
    }
}
