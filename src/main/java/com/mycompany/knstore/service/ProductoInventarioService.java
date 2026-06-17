package com.mycompany.knstore.service;

import com.mycompany.knstore.service.dto.ProductoInventarioDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.knstore.domain.ProductoInventario}.
 */
public interface ProductoInventarioService {
    /**
     * Save a productoInventario.
     *
     * @param productoInventarioDTO the entity to save.
     * @return the persisted entity.
     */
    ProductoInventarioDTO save(ProductoInventarioDTO productoInventarioDTO);

    /**
     * Updates a productoInventario.
     *
     * @param productoInventarioDTO the entity to update.
     * @return the persisted entity.
     */
    ProductoInventarioDTO update(ProductoInventarioDTO productoInventarioDTO);

    /**
     * Partially updates a productoInventario.
     *
     * @param productoInventarioDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProductoInventarioDTO> partialUpdate(ProductoInventarioDTO productoInventarioDTO);

    /**
     * Get all the productoInventarios.
     *
     * @return the list of entities.
     */
    List<ProductoInventarioDTO> findAll();

    /**
     * Get all the ProductoInventarioDTO where Producto is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<ProductoInventarioDTO> findAllWhereProductoIsNull();

    /**
     * Get the "id" productoInventario.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProductoInventarioDTO> findOne(String id);

    /**
     * Delete the "id" productoInventario.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
