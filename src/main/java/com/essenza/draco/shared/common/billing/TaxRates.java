package com.essenza.draco.shared.common.billing;

import java.math.BigDecimal;

/**
 * Contrato de tarifas de impuesto (lo implementa facturación). El catálogo lo usa para
 * validar la tarifa de un producto y ventas para congelar el impuesto en cada línea.
 */
public interface TaxRates {

    /**
     * Tarifa vista desde fuera de facturación.
     *
     * @param kind IVA, INC o EXCLUDED (excluido: no causa impuesto)
     * @param rate porcentaje (19.000 = 19 %)
     */
    record TaxRateView(Long id, String code, String name, String kind, BigDecimal rate) {

        public boolean taxable() {
            return !"EXCLUDED".equals(kind) && rate != null && rate.signum() > 0;
        }
    }

    /**
     * Tarifa a aplicar: la indicada o, si es {@code null}, la marcada por defecto.
     *
     * @throws com.essenza.draco.shared.exceptions.NotFoundException si la tarifa indicada no existe
     */
    TaxRateView effective(Long taxRateId);

    /** La tarifa existe y está activa (para asignarla a un producto). */
    boolean isAssignable(Long taxRateId);
}
