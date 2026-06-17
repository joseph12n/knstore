package com.mycompany.knstore.service;

import com.mycompany.knstore.service.dto.CategoriaIVADTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.knstore.domain.CategoriaIVA}.
 */
public interface CategoriaIVAService {
    /**
     * Save a categoriaIVA.
     *
     * @param categoriaIVADTO the entity to save.
     * @return the persisted entity.
     */
    CategoriaIVADTO save(CategoriaIVADTO categoriaIVADTO);

    /**
     * Updates a categoriaIVA.
     *
     * @param categoriaIVADTO the entity to update.
     * @return the persisted entity.
     */
    CategoriaIVADTO update(CategoriaIVADTO categoriaIVADTO);

    /**
     * Partially updates a categoriaIVA.
     *
     * @param categoriaIVADTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CategoriaIVADTO> partialUpdate(CategoriaIVADTO categoriaIVADTO);

    /**
     * Get all the categoriaIVAS.
     *
     * @return the list of entities.
     */
    List<CategoriaIVADTO> findAll();

    /**
     * Get the "id" categoriaIVA.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CategoriaIVADTO> findOne(String id);

    /**
     * Delete the "id" categoriaIVA.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
