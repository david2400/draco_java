package com.essenza.draco.shared.common.domain.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado de una operación en lote. Cada id se procesa de forma
 * independiente: un fallo no revierte los demás y se informa en
 * {@code failed} con su motivo, para que la UI pueda mostrarlo.
 */
public record BulkOperationResult(int requested, int succeeded, List<Failure> failed) {

    public record Failure(Long id, String reason) {
    }

    /** Acumulador mutable para construir el resultado. */
    public static final class Builder {
        private final int requested;
        private int succeeded;
        private final List<Failure> failed = new ArrayList<>();

        public Builder(int requested) {
            this.requested = requested;
        }

        public Builder success() {
            this.succeeded++;
            return this;
        }

        public Builder failure(Long id, String reason) {
            this.failed.add(new Failure(id, reason));
            return this;
        }

        public BulkOperationResult build() {
            return new BulkOperationResult(requested, succeeded, List.copyOf(failed));
        }
    }
}
