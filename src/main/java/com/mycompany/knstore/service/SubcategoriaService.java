package com.mycompany.knstore.service;

import com.mycompany.knstore.service.dto.SubcategoriaDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.knstore.domain.Subcategoria}.
 */
public interface SubcategoriaService {
    /**
     * Save a subcategoria.
     *
     * @param subcategoriaDTO the entity to save.
     * @return the persisted entity.
     */
    SubcategoriaDTO save(SubcategoriaDTO subcategoriaDTO);

    /**
     * Updates a subcategoria.
     *
     * @param subcategoriaDTO the entity to update.
     * @return the persisted entity.
     */
    SubcategoriaDTO update(SubcategoriaDTO subcategoriaDTO);

    /**
     * Partially updates a subcategoria.
     *
     * @param subcategoriaDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SubcategoriaDTO> partialUpdate(SubcategoriaDTO subcategoriaDTO);

    /**
     * Get all the subcategorias.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SubcategoriaDTO> findAll(Pageable pageable);

    /**
     * Get all the subcategorias with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SubcategoriaDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" subcategoria.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SubcategoriaDTO> findOne(String id);

    /**
     * Delete the "id" subcategoria.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
