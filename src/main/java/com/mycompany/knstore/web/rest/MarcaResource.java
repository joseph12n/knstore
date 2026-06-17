package com.mycompany.knstore.web.rest;

import com.mycompany.knstore.repository.MarcaRepository;
import com.mycompany.knstore.service.MarcaService;
import com.mycompany.knstore.service.dto.MarcaDTO;
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
 * REST controller for managing {@link com.mycompany.knstore.domain.Marca}.
 */
@RestController
@RequestMapping("/api/marcas")
public class MarcaResource {

    private static final Logger LOG = LoggerFactory.getLogger(MarcaResource.class);

    private static final String ENTITY_NAME = "marca";

    @Value("${jhipster.clientApp.name:knstore}")
    private String applicationName;

    private final MarcaService marcaService;

    private final MarcaRepository marcaRepository;

    public MarcaResource(MarcaService marcaService, MarcaRepository marcaRepository) {
        this.marcaService = marcaService;
        this.marcaRepository = marcaRepository;
    }

    /**
     * {@code POST  /marcas} : Create a new marca.
     *
     * @param marcaDTO the marcaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new marcaDTO, or with status {@code 400 (Bad Request)} if the marca has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MarcaDTO> createMarca(@Valid @RequestBody MarcaDTO marcaDTO) throws URISyntaxException {
        LOG.debug("REST request to save Marca : {}", marcaDTO);
        if (marcaDTO.getId() != null) {
            throw new BadRequestAlertException("A new marca cannot already have an ID", ENTITY_NAME, "idexists");
        }
        marcaDTO = marcaService.save(marcaDTO);
        return ResponseEntity.created(new URI("/api/marcas/" + marcaDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, marcaDTO.getId()))
            .body(marcaDTO);
    }

    /**
     * {@code PUT  /marcas/:id} : Updates an existing marca.
     *
     * @param id the id of the marcaDTO to save.
     * @param marcaDTO the marcaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated marcaDTO,
     * or with status {@code 400 (Bad Request)} if the marcaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the marcaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MarcaDTO> updateMarca(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody MarcaDTO marcaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Marca : {}, {}", id, marcaDTO);
        if (marcaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, marcaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!marcaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        marcaDTO = marcaService.update(marcaDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, marcaDTO.getId()))
            .body(marcaDTO);
    }

    /**
     * {@code PATCH  /marcas/:id} : Partial updates given fields of an existing marca, field will ignore if it is null
     *
     * @param id the id of the marcaDTO to save.
     * @param marcaDTO the marcaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated marcaDTO,
     * or with status {@code 400 (Bad Request)} if the marcaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the marcaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the marcaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MarcaDTO> partialUpdateMarca(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody MarcaDTO marcaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Marca partially : {}, {}", id, marcaDTO);
        if (marcaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, marcaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!marcaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MarcaDTO> result = marcaService.partialUpdate(marcaDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, marcaDTO.getId())
        );
    }

    /**
     * {@code GET  /marcas} : get all the Marcas.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Marcas in body.
     */
    @GetMapping("")
    public List<MarcaDTO> getAllMarcas() {
        LOG.debug("REST request to get all Marcas");
        return marcaService.findAll();
    }

    /**
     * {@code GET  /marcas/:id} : get the "id" marca.
     *
     * @param id the id of the marcaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the marcaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MarcaDTO> getMarca(@PathVariable("id") String id) {
        LOG.debug("REST request to get Marca : {}", id);
        Optional<MarcaDTO> marcaDTO = marcaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(marcaDTO);
    }

    /**
     * {@code DELETE  /marcas/:id} : delete the "id" marca.
     *
     * @param id the id of the marcaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMarca(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Marca : {}", id);
        marcaService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id))
            .build();
    }
}
