package com.mycompany.knstore.web.rest;

import com.mycompany.knstore.repository.SubcategoriaRepository;
import com.mycompany.knstore.service.SubcategoriaService;
import com.mycompany.knstore.service.dto.SubcategoriaDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.knstore.domain.Subcategoria}.
 */
@RestController
@RequestMapping("/api/subcategorias")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
public class SubcategoriaResource {

    private static final Logger LOG = LoggerFactory.getLogger(SubcategoriaResource.class);

    private static final String ENTITY_NAME = "subcategoria";

    @Value("${jhipster.clientApp.name:knstore}")
    private String applicationName;

    private final SubcategoriaService subcategoriaService;

    private final SubcategoriaRepository subcategoriaRepository;

    public SubcategoriaResource(SubcategoriaService subcategoriaService, SubcategoriaRepository subcategoriaRepository) {
        this.subcategoriaService = subcategoriaService;
        this.subcategoriaRepository = subcategoriaRepository;
    }

    /**
     * {@code POST  /subcategorias} : Create a new subcategoria.
     *
     * @param subcategoriaDTO the subcategoriaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new subcategoriaDTO, or with status {@code 400 (Bad Request)} if the subcategoria has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SubcategoriaDTO> createSubcategoria(@Valid @RequestBody SubcategoriaDTO subcategoriaDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save Subcategoria : {}", subcategoriaDTO);
        if (subcategoriaDTO.getId() != null) {
            throw new BadRequestAlertException("A new subcategoria cannot already have an ID", ENTITY_NAME, "idexists");
        }
        subcategoriaDTO = subcategoriaService.save(subcategoriaDTO);
        return ResponseEntity.created(new URI("/api/subcategorias/" + subcategoriaDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, subcategoriaDTO.getId()))
            .body(subcategoriaDTO);
    }

    /**
     * {@code PUT  /subcategorias/:id} : Updates an existing subcategoria.
     *
     * @param id the id of the subcategoriaDTO to save.
     * @param subcategoriaDTO the subcategoriaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subcategoriaDTO,
     * or with status {@code 400 (Bad Request)} if the subcategoriaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the subcategoriaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SubcategoriaDTO> updateSubcategoria(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody SubcategoriaDTO subcategoriaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Subcategoria : {}, {}", id, subcategoriaDTO);
        if (subcategoriaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subcategoriaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!subcategoriaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        subcategoriaDTO = subcategoriaService.update(subcategoriaDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, subcategoriaDTO.getId()))
            .body(subcategoriaDTO);
    }

    /**
     * {@code PATCH  /subcategorias/:id} : Partial updates given fields of an existing subcategoria, field will ignore if it is null
     *
     * @param id the id of the subcategoriaDTO to save.
     * @param subcategoriaDTO the subcategoriaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subcategoriaDTO,
     * or with status {@code 400 (Bad Request)} if the subcategoriaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the subcategoriaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the subcategoriaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SubcategoriaDTO> partialUpdateSubcategoria(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody SubcategoriaDTO subcategoriaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Subcategoria partially : {}, {}", id, subcategoriaDTO);
        if (subcategoriaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subcategoriaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!subcategoriaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SubcategoriaDTO> result = subcategoriaService.partialUpdate(subcategoriaDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, subcategoriaDTO.getId())
        );
    }

    /**
     * {@code GET  /subcategorias} : get all the Subcategorias.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Subcategorias in body.
     */
    @GetMapping("")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<SubcategoriaDTO>> getAllSubcategorias(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Subcategorias");
        Page<SubcategoriaDTO> page;
        if (eagerload) {
            page = subcategoriaService.findAllWithEagerRelationships(pageable);
        } else {
            page = subcategoriaService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /subcategorias/:id} : get the "id" subcategoria.
     *
     * @param id the id of the subcategoriaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the subcategoriaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<SubcategoriaDTO> getSubcategoria(@PathVariable("id") String id) {
        LOG.debug("REST request to get Subcategoria : {}", id);
        Optional<SubcategoriaDTO> subcategoriaDTO = subcategoriaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(subcategoriaDTO);
    }

    /**
     * {@code DELETE  /subcategorias/:id} : delete the "id" subcategoria.
     *
     * @param id the id of the subcategoriaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubcategoria(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Subcategoria : {}", id);
        subcategoriaService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id))
            .build();
    }
}
