package com.mycompany.knstore.service;

import com.mycompany.knstore.domain.enumeration.EstadoPedido;
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
     * Cambia el estado de un pedido validando la maquina de estados
     * (PENDING » CONFIRMED » SHIPPED » DELIVERED; CANCELLED solo desde PENDING o CONFIRMED).
     *
     * @param id id del pedido.
     * @param nuevoEstado estado destino.
     * @return el pedido actualizado.
     * @throws IllegalStateException si la transicion no es valida.
     */
    PedidoDTO cambiarEstado(String id, EstadoPedido nuevoEstado);

    /**
     * Cancela un pedido como CLIENTE dentro de la ventana de 1 hora desde la compra
     * (RNF-032) y, si el pago habia sido aprobado, solicita el reembolso simbolico
     * de forma atomica con la cancelacion.
     *
     * @param id id del pedido.
     * @param motivo motivo opcional de la cancelacion (se usa como motivo del reembolso).
     * @return el pedido actualizado.
     * @throws IllegalStateException si el plazo vencio o la transicion no es valida.
     */
    PedidoDTO cancelarPedidoCliente(String id, String motivo);

    /**
     * Delete the "id" pedido.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
