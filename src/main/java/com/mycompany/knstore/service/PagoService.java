package com.mycompany.knstore.service;

import com.mycompany.knstore.service.dto.PagoDTO;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.knstore.domain.Pago}.
 */
public interface PagoService {
    /**
     * Save a pago.
     *
     * @param pagoDTO the entity to save.
     * @return the persisted entity.
     */
    PagoDTO save(PagoDTO pagoDTO);

    /**
     * Updates a pago.
     *
     * @param pagoDTO the entity to update.
     * @return the persisted entity.
     */
    PagoDTO update(PagoDTO pagoDTO);

    /**
     * Partially updates a pago.
     *
     * @param pagoDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PagoDTO> partialUpdate(PagoDTO pagoDTO);

    /**
     * Get all the pagos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PagoDTO> findAll(Pageable pageable);

    /**
     * Get the "id" pago.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PagoDTO> findOne(String id);

    /**
     * Delete the "id" pago.
     *
     * @param id the id of the entity.
     */
    void delete(String id);

    /**
     * Inicia o reintenta el pago de un pedido con la pasarela simulada.
     *
     * @param pedidoId the id of the pedido to pay.
     * @return the persisted pago with its final state.
     */
    PagoDTO iniciarPago(String pedidoId);

    /**
     * Procesa un callback de la pasarela de pagos de forma idempotente.
     *
     * @param referencia referencia de la pasarela.
     * @param estado estado reportado (APPROVED o REJECTED).
     * @param monto monto pagado.
     * @param codigoAutorizacion codigo de autorizacion (opcional).
     * @return el pago actualizado.
     */
    PagoDTO procesarCallback(String referencia, String estado, BigDecimal monto, String codigoAutorizacion);

    /**
     * Consulta el estado actual de un pago por su referencia de pasarela.
     *
     * @param referencia referencia de la pasarela.
     * @return el pago encontrado.
     */
    Optional<PagoDTO> consultarEstado(String referencia);

    /**
     * Reembolsa un pago aprobado dejando trazabilidad completa.
     *
     * @param id id del pago.
     * @param motivo motivo del reembolso (obligatorio).
     * @return el pago reembolsado.
     * @throws IllegalStateException si el pago no esta aprobado o ya fue reembolsado.
     */
    PagoDTO reembolsar(String id, String motivo);
}
