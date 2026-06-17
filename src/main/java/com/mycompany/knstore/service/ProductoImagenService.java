package com.mycompany.knstore.service;

import com.mycompany.knstore.service.dto.ProductoImagenDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.knstore.domain.ProductoImagen}.
 */
public interface ProductoImagenService {
    /**
     * Save a productoImagen.
     *
     * @param productoImagenDTO the entity to save.
     * @return the persisted entity.
     */
    ProductoImagenDTO save(ProductoImagenDTO productoImagenDTO);

    /**
     * Updates a productoImagen.
     *
     * @param productoImagenDTO the entity to update.
     * @return the persisted entity.
     */
    ProductoImagenDTO update(ProductoImagenDTO productoImagenDTO);

    /**
     * Partially updates a productoImagen.
     *
     * @param productoImagenDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProductoImagenDTO> partialUpdate(ProductoImagenDTO productoImagenDTO);

    /**
     * Get all the productoImagens.
     *
     * @return the list of entities.
     */
    List<ProductoImagenDTO> findAll();

    /**
     * Get the "id" productoImagen.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProductoImagenDTO> findOne(String id);

    /**
     * Delete the "id" productoImagen.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
