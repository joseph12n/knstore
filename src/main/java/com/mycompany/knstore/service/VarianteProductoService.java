package com.mycompany.knstore.service;

import com.mycompany.knstore.service.dto.VarianteProductoDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.knstore.domain.VarianteProducto}.
 */
public interface VarianteProductoService {
    /**
     * Save a varianteProducto.
     *
     * @param varianteProductoDTO the entity to save.
     * @return the persisted entity.
     */
    VarianteProductoDTO save(VarianteProductoDTO varianteProductoDTO);

    /**
     * Updates a varianteProducto.
     *
     * @param varianteProductoDTO the entity to update.
     * @return the persisted entity.
     */
    VarianteProductoDTO update(VarianteProductoDTO varianteProductoDTO);

    /**
     * Partially updates a varianteProducto.
     *
     * @param varianteProductoDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<VarianteProductoDTO> partialUpdate(VarianteProductoDTO varianteProductoDTO);

    /**
     * Get all the varianteProductos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VarianteProductoDTO> findAll(Pageable pageable);

    /**
     * Get all the varianteProductos with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VarianteProductoDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" varianteProducto.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VarianteProductoDTO> findOne(String id);

    /**
     * Delete the "id" varianteProducto.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
