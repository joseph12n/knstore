package com.mycompany.knstore.service;

import com.mycompany.knstore.service.dto.ItemCarritoDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.knstore.domain.ItemCarrito}.
 */
public interface ItemCarritoService {
    /**
     * Save a itemCarrito.
     *
     * @param itemCarritoDTO the entity to save.
     * @return the persisted entity.
     */
    ItemCarritoDTO save(ItemCarritoDTO itemCarritoDTO);

    /**
     * Updates a itemCarrito.
     *
     * @param itemCarritoDTO the entity to update.
     * @return the persisted entity.
     */
    ItemCarritoDTO update(ItemCarritoDTO itemCarritoDTO);

    /**
     * Partially updates a itemCarrito.
     *
     * @param itemCarritoDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ItemCarritoDTO> partialUpdate(ItemCarritoDTO itemCarritoDTO);

    /**
     * Get all the itemCarritos.
     *
     * @return the list of entities.
     */
    List<ItemCarritoDTO> findAll();

    /**
     * Get all the itemCarritos with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ItemCarritoDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" itemCarrito.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ItemCarritoDTO> findOne(String id);

    /**
     * Delete the "id" itemCarrito.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
