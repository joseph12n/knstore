package com.mycompany.knstore.service;

import com.mycompany.knstore.service.dto.CarritoDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.knstore.domain.Carrito}.
 */
public interface CarritoService {
    /**
     * Save a carrito.
     *
     * @param carritoDTO the entity to save.
     * @return the persisted entity.
     */
    CarritoDTO save(CarritoDTO carritoDTO);

    /**
     * Updates a carrito.
     *
     * @param carritoDTO the entity to update.
     * @return the persisted entity.
     */
    CarritoDTO update(CarritoDTO carritoDTO);

    /**
     * Partially updates a carrito.
     *
     * @param carritoDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CarritoDTO> partialUpdate(CarritoDTO carritoDTO);

    /**
     * Get all the carritos.
     *
     * @return the list of entities.
     */
    List<CarritoDTO> findAll();

    /**
     * Get the "id" carrito.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CarritoDTO> findOne(String id);

    /**
     * Delete the "id" carrito.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
