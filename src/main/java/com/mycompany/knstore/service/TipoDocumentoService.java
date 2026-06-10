package com.mycompany.knstore.service;

import com.mycompany.knstore.service.dto.TipoDocumentoDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.knstore.domain.TipoDocumento}.
 */
public interface TipoDocumentoService {
    /**
     * Save a tipoDocumento.
     *
     * @param tipoDocumentoDTO the entity to save.
     * @return the persisted entity.
     */
    TipoDocumentoDTO save(TipoDocumentoDTO tipoDocumentoDTO);

    /**
     * Updates a tipoDocumento.
     *
     * @param tipoDocumentoDTO the entity to update.
     * @return the persisted entity.
     */
    TipoDocumentoDTO update(TipoDocumentoDTO tipoDocumentoDTO);

    /**
     * Partially updates a tipoDocumento.
     *
     * @param tipoDocumentoDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TipoDocumentoDTO> partialUpdate(TipoDocumentoDTO tipoDocumentoDTO);

    /**
     * Get all the tipoDocumentos.
     *
     * @return the list of entities.
     */
    List<TipoDocumentoDTO> findAll();

    /**
     * Get the "id" tipoDocumento.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TipoDocumentoDTO> findOne(String id);

    /**
     * Delete the "id" tipoDocumento.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
