package com.mycompany.knstore.web.rest;

import com.mycompany.knstore.repository.CategoriaIVARepository;
import com.mycompany.knstore.service.CategoriaIVAService;
import com.mycompany.knstore.service.dto.CategoriaIVADTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.knstore.domain.CategoriaIVA}.
 */
@RestController
@RequestMapping("/api/categoria-ivas")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
public class CategoriaIVAResource {

    private static final Logger LOG = LoggerFactory.getLogger(CategoriaIVAResource.class);

    private static final String ENTITY_NAME = "categoriaIVA";

    @Value("${jhipster.clientApp.name:knstore}")
    private String applicationName;

    private final CategoriaIVAService categoriaIVAService;

    private final CategoriaIVARepository categoriaIVARepository;

    public CategoriaIVAResource(CategoriaIVAService categoriaIVAService, CategoriaIVARepository categoriaIVARepository) {
        this.categoriaIVAService = categoriaIVAService;
        this.categoriaIVARepository = categoriaIVARepository;
    }

    /**
     * {@code POST  /categoria-ivas} : Create a new categoriaIVA.
     *
     * @param categoriaIVADTO the categoriaIVADTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new categoriaIVADTO, or with status {@code 400 (Bad Request)} if the categoriaIVA has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CategoriaIVADTO> createCategoriaIVA(@Valid @RequestBody CategoriaIVADTO categoriaIVADTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CategoriaIVA : {}", categoriaIVADTO);
        if (categoriaIVADTO.getId() != null) {
            throw new BadRequestAlertException("A new categoriaIVA cannot already have an ID", ENTITY_NAME, "idexists");
        }
        categoriaIVADTO = categoriaIVAService.save(categoriaIVADTO);
        return ResponseEntity.created(new URI("/api/categoria-ivas/" + categoriaIVADTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, categoriaIVADTO.getId()))
            .body(categoriaIVADTO);
    }

    /**
     * {@code PUT  /categoria-ivas/:id} : Updates an existing categoriaIVA.
     *
     * @param id the id of the categoriaIVADTO to save.
     * @param categoriaIVADTO the categoriaIVADTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated categoriaIVADTO,
     * or with status {@code 400 (Bad Request)} if the categoriaIVADTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the categoriaIVADTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaIVADTO> updateCategoriaIVA(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody CategoriaIVADTO categoriaIVADTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CategoriaIVA : {}, {}", id, categoriaIVADTO);
        if (categoriaIVADTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, categoriaIVADTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!categoriaIVARepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        categoriaIVADTO = categoriaIVAService.update(categoriaIVADTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, categoriaIVADTO.getId()))
            .body(categoriaIVADTO);
    }

    /**
     * {@code PATCH  /categoria-ivas/:id} : Partial updates given fields of an existing categoriaIVA, field will ignore if it is null
     *
     * @param id the id of the categoriaIVADTO to save.
     * @param categoriaIVADTO the categoriaIVADTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated categoriaIVADTO,
     * or with status {@code 400 (Bad Request)} if the categoriaIVADTO is not valid,
     * or with status {@code 404 (Not Found)} if the categoriaIVADTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the categoriaIVADTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CategoriaIVADTO> partialUpdateCategoriaIVA(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody CategoriaIVADTO categoriaIVADTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CategoriaIVA partially : {}, {}", id, categoriaIVADTO);
        if (categoriaIVADTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, categoriaIVADTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!categoriaIVARepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CategoriaIVADTO> result = categoriaIVAService.partialUpdate(categoriaIVADTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, categoriaIVADTO.getId())
        );
    }

    /**
     * {@code GET  /categoria-ivas} : get all the Categoria IVAS.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Categoria IVAS in body.
     */
    @GetMapping("")
    public List<CategoriaIVADTO> getAllCategoriaIVAS() {
        LOG.debug("REST request to get all CategoriaIVAS");
        return categoriaIVAService.findAll();
    }

    /**
     * {@code GET  /categoria-ivas/:id} : get the "id" categoriaIVA.
     *
     * @param id the id of the categoriaIVADTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the categoriaIVADTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaIVADTO> getCategoriaIVA(@PathVariable("id") String id) {
        LOG.debug("REST request to get CategoriaIVA : {}", id);
        Optional<CategoriaIVADTO> categoriaIVADTO = categoriaIVAService.findOne(id);
        return ResponseUtil.wrapOrNotFound(categoriaIVADTO);
    }

    /**
     * {@code DELETE  /categoria-ivas/:id} : delete the "id" categoriaIVA.
     *
     * @param id the id of the categoriaIVADTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoriaIVA(@PathVariable("id") String id) {
        LOG.debug("REST request to delete CategoriaIVA : {}", id);
        categoriaIVAService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id))
            .build();
    }
}
