package com.mycompany.knstore.web.rest;

import com.mycompany.knstore.repository.TipoDocumentoRepository;
import com.mycompany.knstore.service.TipoDocumentoService;
import com.mycompany.knstore.service.dto.TipoDocumentoDTO;
import com.mycompany.knstore.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.knstore.domain.TipoDocumento}.
 */
@RestController
@RequestMapping("/api/tipo-documentos")
public class TipoDocumentoResource {

    private static final Logger LOG = LoggerFactory.getLogger(TipoDocumentoResource.class);

    private static final String ENTITY_NAME = "tipoDocumento";

    @Value("${jhipster.clientApp.name:knstore}")
    private String applicationName;

    private final TipoDocumentoService tipoDocumentoService;

    private final TipoDocumentoRepository tipoDocumentoRepository;

    public TipoDocumentoResource(TipoDocumentoService tipoDocumentoService, TipoDocumentoRepository tipoDocumentoRepository) {
        this.tipoDocumentoService = tipoDocumentoService;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    /**
     * {@code POST  /tipo-documentos} : Create a new tipoDocumento.
     *
     * @param tipoDocumentoDTO the tipoDocumentoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tipoDocumentoDTO, or with status {@code 400 (Bad Request)} if the tipoDocumento has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TipoDocumentoDTO> createTipoDocumento(@Valid @RequestBody TipoDocumentoDTO tipoDocumentoDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TipoDocumento : {}", tipoDocumentoDTO);
        if (tipoDocumentoDTO.getId() != null) {
            throw new BadRequestAlertException("A new tipoDocumento cannot already have an ID", ENTITY_NAME, "idexists");
        }
        tipoDocumentoDTO = tipoDocumentoService.save(tipoDocumentoDTO);
        return ResponseEntity.created(new URI("/api/tipo-documentos/" + tipoDocumentoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, tipoDocumentoDTO.getId()))
            .body(tipoDocumentoDTO);
    }

    /**
     * {@code PUT  /tipo-documentos/:id} : Updates an existing tipoDocumento.
     *
     * @param id the id of the tipoDocumentoDTO to save.
     * @param tipoDocumentoDTO the tipoDocumentoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tipoDocumentoDTO,
     * or with status {@code 400 (Bad Request)} if the tipoDocumentoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tipoDocumentoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TipoDocumentoDTO> updateTipoDocumento(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody TipoDocumentoDTO tipoDocumentoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TipoDocumento : {}, {}", id, tipoDocumentoDTO);
        if (tipoDocumentoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tipoDocumentoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tipoDocumentoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        tipoDocumentoDTO = tipoDocumentoService.update(tipoDocumentoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tipoDocumentoDTO.getId()))
            .body(tipoDocumentoDTO);
    }

    /**
     * {@code PATCH  /tipo-documentos/:id} : Partial updates given fields of an existing tipoDocumento, field will ignore if it is null
     *
     * @param id the id of the tipoDocumentoDTO to save.
     * @param tipoDocumentoDTO the tipoDocumentoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tipoDocumentoDTO,
     * or with status {@code 400 (Bad Request)} if the tipoDocumentoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tipoDocumentoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tipoDocumentoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TipoDocumentoDTO> partialUpdateTipoDocumento(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody TipoDocumentoDTO tipoDocumentoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TipoDocumento partially : {}, {}", id, tipoDocumentoDTO);
        if (tipoDocumentoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tipoDocumentoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tipoDocumentoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TipoDocumentoDTO> result = tipoDocumentoService.partialUpdate(tipoDocumentoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tipoDocumentoDTO.getId())
        );
    }

    /**
     * {@code GET  /tipo-documentos} : get all the Tipo Documentos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Tipo Documentos in body.
     */
    @GetMapping("")
    public List<TipoDocumentoDTO> getAllTipoDocumentos() {
        LOG.debug("REST request to get all TipoDocumentos");
        return tipoDocumentoService.findAll();
    }

    /**
     * {@code GET  /tipo-documentos/:id} : get the "id" tipoDocumento.
     *
     * @param id the id of the tipoDocumentoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tipoDocumentoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TipoDocumentoDTO> getTipoDocumento(@PathVariable("id") String id) {
        LOG.debug("REST request to get TipoDocumento : {}", id);
        Optional<TipoDocumentoDTO> tipoDocumentoDTO = tipoDocumentoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tipoDocumentoDTO);
    }

    /**
     * {@code DELETE  /tipo-documentos/:id} : delete the "id" tipoDocumento.
     *
     * @param id the id of the tipoDocumentoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTipoDocumento(@PathVariable("id") String id) {
        LOG.debug("REST request to delete TipoDocumento : {}", id);
        tipoDocumentoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id))
            .build();
    }
}
