package com.mycompany.knstore.service;

import com.mycompany.knstore.service.dto.EtiquetaProductoDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.knstore.domain.EtiquetaProducto}.
 */
public interface EtiquetaProductoService {
    /**
     * Save a etiquetaProducto.
     *
     * @param etiquetaProductoDTO the entity to save.
     * @return the persisted entity.
     */
    EtiquetaProductoDTO save(EtiquetaProductoDTO etiquetaProductoDTO);

    /**
     * Updates a etiquetaProducto.
     *
     * @param etiquetaProductoDTO the entity to update.
     * @return the persisted entity.
     */
    EtiquetaProductoDTO update(EtiquetaProductoDTO etiquetaProductoDTO);

    /**
     * Partially updates a etiquetaProducto.
     *
     * @param etiquetaProductoDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<EtiquetaProductoDTO> partialUpdate(EtiquetaProductoDTO etiquetaProductoDTO);

    /**
     * Get all the etiquetaProductos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EtiquetaProductoDTO> findAll(Pageable pageable);

    /**
     * Get all the etiquetaProductos with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EtiquetaProductoDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" etiquetaProducto.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EtiquetaProductoDTO> findOne(String id);

    /**
     * Delete the "id" etiquetaProducto.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
