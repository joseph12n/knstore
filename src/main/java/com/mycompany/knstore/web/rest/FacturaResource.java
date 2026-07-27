package com.mycompany.knstore.web.rest;

import com.mycompany.knstore.repository.FacturaRepository;
import com.mycompany.knstore.service.FacturaService;
import com.mycompany.knstore.service.dto.FacturaDTO;
import com.mycompany.knstore.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
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
 * REST controller for managing {@link com.mycompany.knstore.domain.Factura}.
 */
@RestController
@RequestMapping("/api/facturas")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER','ROLE_CLIENTE')")
public class FacturaResource {

    private static final Logger LOG = LoggerFactory.getLogger(FacturaResource.class);

    private static final String ENTITY_NAME = "factura";

    @Value("${jhipster.clientApp.name:knstore}")
    private String applicationName;

    private final FacturaService facturaService;

    private final FacturaRepository facturaRepository;

    public FacturaResource(FacturaService facturaService, FacturaRepository facturaRepository) {
        this.facturaService = facturaService;
        this.facturaRepository = facturaRepository;
    }

    /**
     * {@code POST  /facturas} : Create a new factura.
     *
     * @param facturaDTO the facturaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new facturaDTO, or with status {@code 400 (Bad Request)} if the factura has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER') or @resourceAccessService.canAccessFacturaDto(#facturaDTO)")
    public ResponseEntity<FacturaDTO> createFactura(@Valid @RequestBody FacturaDTO facturaDTO) throws URISyntaxException {
        LOG.debug("REST request to save Factura : {}", facturaDTO);
        if (facturaDTO.getId() != null) {
            throw new BadRequestAlertException("A new factura cannot already have an ID", ENTITY_NAME, "idexists");
        }
        facturaDTO = facturaService.save(facturaDTO);
        return ResponseEntity.created(new URI("/api/facturas/" + facturaDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, facturaDTO.getId()))
            .body(facturaDTO);
    }

    /**
     * {@code PUT  /facturas/:id} : Updates an existing factura.
     *
     * @param id the id of the facturaDTO to save.
     * @param facturaDTO the facturaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated facturaDTO,
     * or with status {@code 400 (Bad Request)} if the facturaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the facturaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize(
        "hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER') or (@resourceAccessService.canAccessFacturaId(#id) and @resourceAccessService.canAccessFacturaDto(#facturaDTO))"
    )
    public ResponseEntity<FacturaDTO> updateFactura(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody FacturaDTO facturaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Factura : {}, {}", id, facturaDTO);
        if (facturaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, facturaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!facturaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        facturaDTO = facturaService.update(facturaDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, facturaDTO.getId()))
            .body(facturaDTO);
    }

    /**
     * {@code PATCH  /facturas/:id} : Partial updates given fields of an existing factura, field will ignore if it is null
     *
     * @param id the id of the facturaDTO to save.
     * @param facturaDTO the facturaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated facturaDTO,
     * or with status {@code 400 (Bad Request)} if the facturaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the facturaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the facturaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize(
        "hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER') or (@resourceAccessService.canAccessFacturaId(#id) and @resourceAccessService.canAccessFacturaDto(#facturaDTO))"
    )
    public ResponseEntity<FacturaDTO> partialUpdateFactura(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody FacturaDTO facturaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Factura partially : {}, {}", id, facturaDTO);
        if (facturaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, facturaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!facturaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FacturaDTO> result = facturaService.partialUpdate(facturaDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, facturaDTO.getId())
        );
    }

    /**
     * {@code GET  /facturas} : get all the Facturas.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Facturas in body.
     */
    @GetMapping("")
    public ResponseEntity<List<FacturaDTO>> getAllFacturas(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Facturas");
        Page<FacturaDTO> page = facturaService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /facturas/:id} : get the "id" factura.
     *
     * @param id the id of the facturaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the facturaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER') or @resourceAccessService.canAccessFacturaId(#id)")
    public ResponseEntity<FacturaDTO> getFactura(@PathVariable("id") String id) {
        LOG.debug("REST request to get Factura : {}", id);
        Optional<FacturaDTO> facturaDTO = facturaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(facturaDTO);
    }

    /**
     * {@code GET  /facturas/:id/download} : download the "id" factura with QR data.
     *
     * @param id the id of the facturaDTO to download.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the factura data as downloadable JSON.
     */
    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER') or @resourceAccessService.canAccessFacturaId(#id)")
    public ResponseEntity<byte[]> downloadFactura(@PathVariable("id") String id) {
        LOG.debug("REST request to download Factura : {}", id);
        FacturaDTO facturaDTO = facturaService
            .findOne(id)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));

        String filename = "factura-" + (facturaDTO.getPrefijo() != null ? facturaDTO.getPrefijo() + "-" : "") + id + ".json";
        byte[] content = facturaDTO.toString().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
            .header("Content-Type", "application/json;charset=UTF-8")
            .body(content);
    }

    /**
     * {@code DELETE  /facturas/:id} : delete the "id" factura.
     *
     * @param id the id of the facturaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER') or @resourceAccessService.canAccessFacturaId(#id)")
    public ResponseEntity<Void> deleteFactura(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Factura : {}", id);
        facturaService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id))
            .build();
    }
}
