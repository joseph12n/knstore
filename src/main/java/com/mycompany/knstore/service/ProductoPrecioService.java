package com.mycompany.knstore.service;

import com.mycompany.knstore.service.dto.ProductoPrecioDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.knstore.domain.ProductoPrecio}.
 */
public interface ProductoPrecioService {
    /**
     * Save a productoPrecio.
     *
     * @param productoPrecioDTO the entity to save.
     * @return the persisted entity.
     */
    ProductoPrecioDTO save(ProductoPrecioDTO productoPrecioDTO);

    /**
     * Updates a productoPrecio.
     *
     * @param productoPrecioDTO the entity to update.
     * @return the persisted entity.
     */
    ProductoPrecioDTO update(ProductoPrecioDTO productoPrecioDTO);

    /**
     * Partially updates a productoPrecio.
     *
     * @param productoPrecioDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProductoPrecioDTO> partialUpdate(ProductoPrecioDTO productoPrecioDTO);

    /**
     * Get all the productoPrecios.
     *
     * @return the list of entities.
     */
    List<ProductoPrecioDTO> findAll();

    /**
     * Get all the ProductoPrecioDTO where Producto is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<ProductoPrecioDTO> findAllWhereProductoIsNull();

    /**
     * Get the "id" productoPrecio.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProductoPrecioDTO> findOne(String id);

    /**
     * Delete the "id" productoPrecio.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
