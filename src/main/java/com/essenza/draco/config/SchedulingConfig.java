package com.essenza.draco.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Tareas programadas (vencimiento de reservas de órdenes). Se desactiva con {@code essenza.scheduling.enabled=false}. */
@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "essenza.scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class SchedulingConfig {
}
