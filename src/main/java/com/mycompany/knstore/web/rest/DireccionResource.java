package com.mycompany.knstore.web.rest;

import com.mycompany.knstore.repository.DireccionRepository;
import com.mycompany.knstore.service.DireccionService;
import com.mycompany.knstore.service.dto.DireccionDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.knstore.domain.Direccion}.
 */
@RestController
@RequestMapping("/api/direccions")
public class DireccionResource {

    private static final Logger LOG = LoggerFactory.getLogger(DireccionResource.class);

    private static final String ENTITY_NAME = "direccion";

    @Value("${jhipster.clientApp.name:knstore}")
    private String applicationName;

    private final DireccionService direccionService;

    private final DireccionRepository direccionRepository;

    public DireccionResource(DireccionService direccionService, DireccionRepository direccionRepository) {
        this.direccionService = direccionService;
        this.direccionRepository = direccionRepository;
    }

    /**
     * {@code POST  /direccions} : Create a new direccion.
     *
     * @param direccionDTO the direccionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new direccionDTO, or with status {@code 400 (Bad Request)} if the direccion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DireccionDTO> createDireccion(@Valid @RequestBody DireccionDTO direccionDTO) throws URISyntaxException {
        LOG.debug("REST request to save Direccion : {}", direccionDTO);
        if (direccionDTO.getId() != null) {
            throw new BadRequestAlertException("A new direccion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        direccionDTO = direccionService.save(direccionDTO);
        return ResponseEntity.created(new URI("/api/direccions/" + direccionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, direccionDTO.getId()))
            .body(direccionDTO);
    }

    /**
     * {@code PUT  /direccions/:id} : Updates an existing direccion.
     *
     * @param id the id of the direccionDTO to save.
     * @param direccionDTO the direccionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated direccionDTO,
     * or with status {@code 400 (Bad Request)} if the direccionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the direccionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DireccionDTO> updateDireccion(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody DireccionDTO direccionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Direccion : {}, {}", id, direccionDTO);
        if (direccionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, direccionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!direccionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        direccionDTO = direccionService.update(direccionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, direccionDTO.getId()))
            .body(direccionDTO);
    }

    /**
     * {@code PATCH  /direccions/:id} : Partial updates given fields of an existing direccion, field will ignore if it is null
     *
     * @param id the id of the direccionDTO to save.
     * @param direccionDTO the direccionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated direccionDTO,
     * or with status {@code 400 (Bad Request)} if the direccionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the direccionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the direccionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DireccionDTO> partialUpdateDireccion(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody DireccionDTO direccionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Direccion partially : {}, {}", id, direccionDTO);
        if (direccionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, direccionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!direccionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DireccionDTO> result = direccionService.partialUpdate(direccionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, direccionDTO.getId())
        );
    }

    /**
     * {@code GET  /direccions} : get all the Direccions.
     *
     * @param pageable the pagination information.
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Direccions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DireccionDTO>> getAllDireccions(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "filter", required = false) String filter
    ) {
        if ("pedido-is-null".equals(filter)) {
            LOG.debug("REST request to get all Direccions where pedido is null");
            return new ResponseEntity<>(direccionService.findAllWherePedidoIsNull(), HttpStatus.OK);
        }
        LOG.debug("REST request to get a page of Direccions");
        Page<DireccionDTO> page = direccionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /direccions/:id} : get the "id" direccion.
     *
     * @param id the id of the direccionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the direccionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DireccionDTO> getDireccion(@PathVariable("id") String id) {
        LOG.debug("REST request to get Direccion : {}", id);
        Optional<DireccionDTO> direccionDTO = direccionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(direccionDTO);
    }

    /**
     * {@code DELETE  /direccions/:id} : delete the "id" direccion.
     *
     * @param id the id of the direccionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDireccion(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Direccion : {}", id);
        direccionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id))
            .build();
    }
}
