package com.mycompany.knstore.service;

import java.time.LocalDate;

/**
 * Servicio de consecutivos diarios atómicos (RNF-030). Unifica en la colección
 * {@code secuencias} los contadores que antes vivían duplicados en
 * {@code pedido_sequence} y {@code factura_sequence}: cada incremento se hace
 * con $inc dentro de la misma transacción del checkout, eliminando carreras por
 * secuencias concurrentes entre CHECKOUT, pedidos y facturas.
 */
public interface SecuenciaService {
    /**
     * Obtiene e incrementa de forma atómica (findAndModify con upsert y returnNew)
     * el contador del tipo dado para la fecha indicada. Si el documento aún no
     * existe, se crea con el valor 1.
     *
     * @param tipo prefijo del consecutivo (ej. "PED", "FE").
     * @param fecha día del consecutivo.
     * @return valor final del contador tras el incremento atómico.
     */
    long siguiente(String tipo, LocalDate fecha);

    /**
     * Genera el siguiente número de pedido diario en formato
     * {@code PED-aaaaMMdd-000001} (RNF-030).
     *
     * @return número de pedido con consecutivo atómico del día.
     */
    String siguientePedido();

    /**
     * Genera el siguiente consecutivo con formato {@code PREFIJO-000001}
     * (RNF-030), usado entre otros por las facturas (ej. "FE").
     *
     * @param prefijo prefijo del consecutivo; si llega nulo o vacío se usa "FE".
     * @return consecutivo atómico del día con la raíz {@code PREFIJO-}.
     */
    String siguienteConsecutivo(String prefijo);
}
