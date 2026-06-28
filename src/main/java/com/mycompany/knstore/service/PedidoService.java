package com.mycompany.knstore.service;

import com.mycompany.knstore.service.dto.CheckoutRequestDTO;
import com.mycompany.knstore.service.dto.CheckoutResultDTO;
import com.mycompany.knstore.service.dto.PedidoDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.knstore.domain.Pedido}.
 */
public interface PedidoService {
    /**
     * Process an atomic checkout request creating the pedido, its items and the initial pago.
     *
     * @param checkoutRequest the checkout request.
     * @return the result of the checkout with the created pedido summary.
     */
    CheckoutResultDTO checkout(CheckoutRequestDTO checkoutRequest);

    /**
     * Save a pedido.
     *
     * @param pedidoDTO the entity to save.
     * @return the persisted entity.
     */
    PedidoDTO save(PedidoDTO pedidoDTO);

    /**
     * Updates a pedido.
     *
     * @param pedidoDTO the entity to update.
     * @return the persisted entity.
     */
    PedidoDTO update(PedidoDTO pedidoDTO);

    /**
     * Partially updates a pedido.
     *
     * @param pedidoDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PedidoDTO> partialUpdate(PedidoDTO pedidoDTO);

    /**
     * Get all the pedidos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PedidoDTO> findAll(Pageable pageable);

    /**
     * Get all the PedidoDTO where Envio is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<PedidoDTO> findAllWhereEnvioIsNull();

    /**
     * Get the "id" pedido.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PedidoDTO> findOne(String id);

    /**
     * Delete the "id" pedido.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
