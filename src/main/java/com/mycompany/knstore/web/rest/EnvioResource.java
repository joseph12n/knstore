package com.mycompany.knstore.web.rest;

import com.mycompany.knstore.domain.enumeration.EstadoEnvio;
import com.mycompany.knstore.repository.EnvioRepository;
import com.mycompany.knstore.service.EnvioService;
import com.mycompany.knstore.service.dto.EnvioDTO;
import com.mycompany.knstore.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
 * REST controller for managing {@link com.mycompany.knstore.domain.Envio}.
 */
@RestController
@RequestMapping("/api/envios")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER','ROLE_CLIENTE')")
public class EnvioResource {

    private static final Logger LOG = LoggerFactory.getLogger(EnvioResource.class);

    private static final String ENTITY_NAME = "envio";

    @Value("${jhipster.clientApp.name:knstore}")
    private String applicationName;

    private final EnvioService envioService;

    private final EnvioRepository envioRepository;

    public EnvioResource(EnvioService envioService, EnvioRepository envioRepository) {
        this.envioService = envioService;
        this.envioRepository = envioRepository;
    }

    /**
     * {@code POST  /envios} : Create a new envio.
     *
     * @param envioDTO the envioDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new envioDTO, or with status {@code 400 (Bad Request)} if the envio has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER') or @resourceAccessService.canAccessEnvioDto(#envioDTO)")
    public ResponseEntity<EnvioDTO> createEnvio(@Valid @RequestBody EnvioDTO envioDTO) throws URISyntaxException {
        LOG.debug("REST request to save Envio : {}", envioDTO);
        if (envioDTO.getId() != null) {
            throw new BadRequestAlertException("A new envio cannot already have an ID", ENTITY_NAME, "idexists");
        }
        envioDTO = envioService.save(envioDTO);
        return ResponseEntity.created(new URI("/api/envios/" + envioDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, envioDTO.getId()))
            .body(envioDTO);
    }

    /**
     * {@code PUT  /envios/:id} : Updates an existing envio.
     *
     * @param id the id of the envioDTO to save.
     * @param envioDTO the envioDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated envioDTO,
     * or with status {@code 400 (Bad Request)} if the envioDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the envioDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize(
        "hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER') or (@resourceAccessService.canAccessEnvioId(#id) and @resourceAccessService.canAccessEnvioDto(#envioDTO))"
    )
    public ResponseEntity<EnvioDTO> updateEnvio(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody EnvioDTO envioDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Envio : {}, {}", id, envioDTO);
        if (envioDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, envioDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!envioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        envioDTO = envioService.update(envioDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, envioDTO.getId()))
            .body(envioDTO);
    }

    /**
     * {@code PATCH  /envios/:id} : Partial updates given fields of an existing envio, field will ignore if it is null
     *
     * @param id the id of the envioDTO to save.
     * @param envioDTO the envioDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated envioDTO,
     * or with status {@code 400 (Bad Request)} if the envioDTO is not valid,
     * or with status {@code 404 (Not Found)} if the envioDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the envioDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize(
        "hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER') or (@resourceAccessService.canAccessEnvioId(#id) and @resourceAccessService.canAccessEnvioDto(#envioDTO))"
    )
    public ResponseEntity<EnvioDTO> partialUpdateEnvio(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody EnvioDTO envioDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Envio partially : {}, {}", id, envioDTO);
        if (envioDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, envioDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!envioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EnvioDTO> result = envioService.partialUpdate(envioDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, envioDTO.getId())
        );
    }

    /**
     * {@code GET  /envios} : get all the Envios.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Envios in body.
     */
    @GetMapping("")
    public ResponseEntity<List<EnvioDTO>> getAllEnvios(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Envios");
        Page<EnvioDTO> page = envioService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /envios/:id} : get the "id" envio.
     *
     * @param id the id of the envioDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the envioDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER') or @resourceAccessService.canAccessEnvioId(#id)")
    public ResponseEntity<EnvioDTO> getEnvio(@PathVariable("id") String id) {
        LOG.debug("REST request to get Envio : {}", id);
        Optional<EnvioDTO> envioDTO = envioService.findOne(id);
        return ResponseUtil.wrapOrNotFound(envioDTO);
    }

    /**
     * {@code DELETE  /envios/:id} : delete the "id" envio.
     *
     * @param id the id of the envioDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER') or @resourceAccessService.canAccessEnvioId(#id)")
    public ResponseEntity<Void> deleteEnvio(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Envio : {}", id);
        envioService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id))
            .build();
    }

    /**
     * {@code PATCH  /envios/:id/tracking} : assign transportadora and numeroRastreo to an envio.
     *
     * @param id the id of the envio.
     * @param request the tracking data.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated envioDTO.
     */
    @PatchMapping("/{id}/tracking")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<EnvioDTO> asignarTracking(@PathVariable("id") String id, @Valid @RequestBody AsignarTrackingRequest request) {
        LOG.debug("REST request to assign tracking to Envio : {}", id);
        try {
            EnvioDTO result = envioService.asignarTracking(id, request.transportadora(), request.numeroRastreo());
            return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId()))
                .body(result);
        } catch (IllegalStateException e) {
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "envioinvalido");
        }
    }

    /**
     * {@code PATCH  /envios/:id/estado} : change the estado of an envio (admin operation).
     *
     * @param id the id of the envio.
     * @param request the new estado.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated envioDTO.
     */
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<EnvioDTO> cambiarEstadoEnvio(
        @PathVariable("id") String id,
        @Valid @RequestBody CambiarEstadoEnvioRequest request
    ) {
        LOG.debug("REST request to change estado of Envio : {} -> {}", id, request.estado());
        try {
            EnvioDTO result = envioService.cambiarEstado(id, request.estado());
            return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId()))
                .body(result);
        } catch (IllegalStateException e) {
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "transicioninvalida");
        }
    }

    /**
     * {@code PATCH  /envios/:id/devolucion} : mark an envio as returned (admin operation).
     *
     * @param id the id of the envio.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated envioDTO.
     */
    @PatchMapping("/{id}/devolucion")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<EnvioDTO> marcarDevolucion(@PathVariable("id") String id) {
        LOG.debug("REST request to mark Envio as devuelto : {}", id);
        try {
            EnvioDTO result = envioService.marcarDevolucion(id);
            return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId()))
                .body(result);
        } catch (IllegalStateException e) {
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "envioinvalido");
        }
    }

    /**
     * {@code GET  /envios/pendientes} : get the page of pending Envios (logistics tray).
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Envios in body.
     */
    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<List<EnvioDTO>> getEnviosPendientes(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get pending Envios");
        Page<EnvioDTO> page = envioService.findAllPendientes(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * Request DTO for assigning tracking data to an envio.
     */
    public record AsignarTrackingRequest(@NotBlank String transportadora, @NotBlank String numeroRastreo) {}

    /**
     * Request DTO for changing the estado of an envio.
     */
    public record CambiarEstadoEnvioRequest(@NotNull EstadoEnvio estado) {}
}
