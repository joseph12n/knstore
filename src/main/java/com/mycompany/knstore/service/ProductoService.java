package com.mycompany.knstore.service;

import com.mycompany.knstore.service.dto.ProductoDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.knstore.domain.Producto}.
 */
public interface ProductoService {
    /**
     * Save a producto.
     *
     * @param productoDTO the entity to save.
     * @return the persisted entity.
     */
    ProductoDTO save(ProductoDTO productoDTO);

    /**
     * Updates a producto.
     *
     * @param productoDTO the entity to update.
     * @return the persisted entity.
     */
    ProductoDTO update(ProductoDTO productoDTO);

    /**
     * Partially updates a producto.
     *
     * @param productoDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProductoDTO> partialUpdate(ProductoDTO productoDTO);

    /**
     * Get all the productos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProductoDTO> findAll(Pageable pageable);

    /**
     * Get all the productos with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProductoDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" producto.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProductoDTO> findOne(String id);

    /**
     * Delete the "id" producto.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
