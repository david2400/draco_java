package com.essenza.draco.modules.devolution.domain.model;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Estados de una devolución (se guardan con un carácter):
 * P pendiente → A aprobada → R recibida → I inspeccionada → F reembolsada; P/A/R → X rechazada.
 */
public enum DevolutionState {
    PENDING("P"),
    APPROVED("A"),
    RECEIVED("R"),
    INSPECTED("I"),
    REFUNDED("F"),
    REJECTED("X");

    private final String code;

    DevolutionState(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static Optional<DevolutionState> fromCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        String value = code.trim().toUpperCase(Locale.ROOT);
        for (DevolutionState state : values()) {
            if (state.code.equals(value) || state.name().equals(value)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }

    /** Las líneas solo se editan mientras está pendiente. */
    public boolean linesEditable() {
        return this == PENDING;
    }

    /** Datos generales (motivo, observación, formas de devolución/reembolso) editables. */
    public boolean headerEditable() {
        return this == PENDING || this == APPROVED;
    }

    public boolean canReject() {
        return this == PENDING || this == APPROVED || this == RECEIVED;
    }

    /** Acciones que el panel puede ofrecer en este estado. */
    public List<String> actions() {
        return switch (this) {
            case PENDING -> List.of("approve", "reject", "edit_lines");
            case APPROVED -> List.of("receive", "reject");
            case RECEIVED -> List.of("inspect", "reject");
            case INSPECTED -> List.of("refund");
            case REFUNDED, REJECTED -> List.of();
        };
    }
}
