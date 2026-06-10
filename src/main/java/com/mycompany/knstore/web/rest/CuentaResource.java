package com.mycompany.knstore.web.rest;

import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.service.CuentaService;
import com.mycompany.knstore.service.dto.CuentaDTO;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.knstore.domain.Cuenta}.
 */
@RestController
@RequestMapping("/api/cuentas")
public class CuentaResource {

    private static final Logger LOG = LoggerFactory.getLogger(CuentaResource.class);

    private static final String ENTITY_NAME = "cuenta";

    @Value("${jhipster.clientApp.name:knstore}")
    private String applicationName;

    private final CuentaService cuentaService;

    private final CuentaRepository cuentaRepository;

    public CuentaResource(CuentaService cuentaService, CuentaRepository cuentaRepository) {
        this.cuentaService = cuentaService;
        this.cuentaRepository = cuentaRepository;
    }

    /**
     * {@code POST  /cuentas} : Create a new cuenta.
     *
     * @param cuentaDTO the cuentaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cuentaDTO, or with status {@code 400 (Bad Request)} if the cuenta has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CuentaDTO> createCuenta(@Valid @RequestBody CuentaDTO cuentaDTO) throws URISyntaxException {
        LOG.debug("REST request to save Cuenta : {}", cuentaDTO);
        if (cuentaDTO.getId() != null) {
            throw new BadRequestAlertException("A new cuenta cannot already have an ID", ENTITY_NAME, "idexists");
        }
        cuentaDTO = cuentaService.save(cuentaDTO);
        return ResponseEntity.created(new URI("/api/cuentas/" + cuentaDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, cuentaDTO.getId()))
            .body(cuentaDTO);
    }

    /**
     * {@code PUT  /cuentas/:id} : Updates an existing cuenta.
     *
     * @param id the id of the cuentaDTO to save.
     * @param cuentaDTO the cuentaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cuentaDTO,
     * or with status {@code 400 (Bad Request)} if the cuentaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cuentaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CuentaDTO> updateCuenta(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody CuentaDTO cuentaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Cuenta : {}, {}", id, cuentaDTO);
        if (cuentaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cuentaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cuentaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        cuentaDTO = cuentaService.update(cuentaDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cuentaDTO.getId()))
            .body(cuentaDTO);
    }

    /**
     * {@code PATCH  /cuentas/:id} : Partial updates given fields of an existing cuenta, field will ignore if it is null
     *
     * @param id the id of the cuentaDTO to save.
     * @param cuentaDTO the cuentaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cuentaDTO,
     * or with status {@code 400 (Bad Request)} if the cuentaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cuentaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cuentaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CuentaDTO> partialUpdateCuenta(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody CuentaDTO cuentaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Cuenta partially : {}, {}", id, cuentaDTO);
        if (cuentaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cuentaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cuentaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CuentaDTO> result = cuentaService.partialUpdate(cuentaDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cuentaDTO.getId())
        );
    }

    /**
     * {@code GET  /cuentas} : get all the Cuentas.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Cuentas in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CuentaDTO>> getAllCuentas(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Cuentas");
        Page<CuentaDTO> page;
        if (eagerload) {
            page = cuentaService.findAllWithEagerRelationships(pageable);
        } else {
            page = cuentaService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cuentas/:id} : get the "id" cuenta.
     *
     * @param id the id of the cuentaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cuentaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CuentaDTO> getCuenta(@PathVariable("id") String id) {
        LOG.debug("REST request to get Cuenta : {}", id);
        Optional<CuentaDTO> cuentaDTO = cuentaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cuentaDTO);
    }

    /**
     * {@code DELETE  /cuentas/:id} : delete the "id" cuenta.
     *
     * @param id the id of the cuentaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCuenta(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Cuenta : {}", id);
        cuentaService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id))
            .build();
    }
}
