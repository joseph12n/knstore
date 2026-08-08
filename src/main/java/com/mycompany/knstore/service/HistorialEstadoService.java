package com.mycompany.knstore.service;

import com.mycompany.knstore.service.dto.HistorialEstadoDTO;
import java.util.List;

/**
 * Service para el registro y consulta del historial de transiciones de estado.
 */
public interface HistorialEstadoService {
    /**
     * Registra una transicion de estado en el historial con el actor autenticado.
     *
     * @param entidad nombre de la entidad (ej. "PEDIDO", "PAGO").
     * @param idEntidad id del documento afectado.
     * @param campo campo cuyo valor cambio (ej. "estado").
     * @param valorAnterior valor previo o {@code null}.
     * @param valorNuevo valor posterior.
     */
    void registrar(String entidad, String idEntidad, String campo, String valorAnterior, String valorNuevo);

    /**
     * Consulta el historial completo de una entidad ordenado por fecha.
     *
     * @param entidad nombre de la entidad.
     * @param idEntidad id del documento.
     * @return lista de transiciones registradas.
     */
    List<HistorialEstadoDTO> consultar(String entidad, String idEntidad);
}
