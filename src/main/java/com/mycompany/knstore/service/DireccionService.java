package com.mycompany.knstore.service;

import com.mycompany.knstore.service.dto.DireccionDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.knstore.domain.Direccion}.
 */
public interface DireccionService {
    /**
     * Save a direccion.
     *
     * @param direccionDTO the entity to save.
     * @return the persisted entity.
     */
    DireccionDTO save(DireccionDTO direccionDTO);

    /**
     * Updates a direccion.
     *
     * @param direccionDTO the entity to update.
     * @return the persisted entity.
     */
    DireccionDTO update(DireccionDTO direccionDTO);

    /**
     * Partially updates a direccion.
     *
     * @param direccionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DireccionDTO> partialUpdate(DireccionDTO direccionDTO);

    /**
     * Get all the direccions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DireccionDTO> findAll(Pageable pageable);

    /**
     * Get all the DireccionDTO where Pedido is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<DireccionDTO> findAllWherePedidoIsNull();

    /**
     * Get the "id" direccion.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DireccionDTO> findOne(String id);

    /**
     * Delete the "id" direccion.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
