package com.mycompany.knstore.web.rest;

import com.mycompany.knstore.repository.PagoRepository;
import com.mycompany.knstore.service.PagoService;
import com.mycompany.knstore.service.dto.PagoDTO;
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
 * REST controller for managing {@link com.mycompany.knstore.domain.Pago}.
 */
@RestController
@RequestMapping("/api/pagos")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER','ROLE_CLIENTE')")
public class PagoResource {

    private static final Logger LOG = LoggerFactory.getLogger(PagoResource.class);

    private static final String ENTITY_NAME = "pago";

    @Value("${jhipster.clientApp.name:knstore}")
    private String applicationName;

    private final PagoService pagoService;

    private final PagoRepository pagoRepository;

    public PagoResource(PagoService pagoService, PagoRepository pagoRepository) {
        this.pagoService = pagoService;
        this.pagoRepository = pagoRepository;
    }

    /**
     * {@code POST  /pagos} : Create a new pago.
     *
     * @param pagoDTO the pagoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pagoDTO, or with status {@code 400 (Bad Request)} if the pago has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER') or @resourceAccessService.canAccessPagoDto(#pagoDTO)")
    public ResponseEntity<PagoDTO> createPago(@Valid @RequestBody PagoDTO pagoDTO) throws URISyntaxException {
        LOG.debug("REST request to save Pago : {}", pagoDTO);
        if (pagoDTO.getId() != null) {
            throw new BadRequestAlertException("A new pago cannot already have an ID", ENTITY_NAME, "idexists");
        }
        pagoDTO = pagoService.save(pagoDTO);
        return ResponseEntity.created(new URI("/api/pagos/" + pagoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, pagoDTO.getId()))
            .body(pagoDTO);
    }

    /**
     * {@code PUT  /pagos/:id} : Updates an existing pago.
     *
     * @param id the id of the pagoDTO to save.
     * @param pagoDTO the pagoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pagoDTO,
     * or with status {@code 400 (Bad Request)} if the pagoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pagoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize(
        "hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER') or (@resourceAccessService.canAccessPagoId(#id) and @resourceAccessService.canAccessPagoDto(#pagoDTO))"
    )
    public ResponseEntity<PagoDTO> updatePago(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody PagoDTO pagoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Pago : {}, {}", id, pagoDTO);
        if (pagoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pagoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pagoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        pagoDTO = pagoService.update(pagoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pagoDTO.getId()))
            .body(pagoDTO);
    }

    /**
     * {@code PATCH  /pagos/:id} : Partial updates given fields of an existing pago, field will ignore if it is null
     *
     * @param id the id of the pagoDTO to save.
     * @param pagoDTO the pagoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pagoDTO,
     * or with status {@code 400 (Bad Request)} if the pagoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the pagoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the pagoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize(
        "hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER') or (@resourceAccessService.canAccessPagoId(#id) and @resourceAccessService.canAccessPagoDto(#pagoDTO))"
    )
    public ResponseEntity<PagoDTO> partialUpdatePago(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody PagoDTO pagoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Pago partially : {}, {}", id, pagoDTO);
        if (pagoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pagoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pagoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PagoDTO> result = pagoService.partialUpdate(pagoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pagoDTO.getId())
        );
    }

    /**
     * {@code GET  /pagos} : get all the Pagos.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Pagos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PagoDTO>> getAllPagos(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Pagos");
        Page<PagoDTO> page = pagoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /pagos/:id} : get the "id" pago.
     *
     * @param id the id of the pagoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pagoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER') or @resourceAccessService.canAccessPagoId(#id)")
    public ResponseEntity<PagoDTO> getPago(@PathVariable("id") String id) {
        LOG.debug("REST request to get Pago : {}", id);
        Optional<PagoDTO> pagoDTO = pagoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pagoDTO);
    }

    /**
     * {@code DELETE  /pagos/:id} : delete the "id" pago.
     *
     * @param id the id of the pagoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER') or @resourceAccessService.canAccessPagoId(#id)")
    public ResponseEntity<Void> deletePago(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Pago : {}", id);
        pagoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id))
            .build();
    }
}
