package com.mycompany.knstore.web.rest;

import com.mycompany.knstore.domain.Envio;
import com.mycompany.knstore.domain.enumeration.EstadoEnvio;
import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import com.mycompany.knstore.repository.EnvioRepository;
import com.mycompany.knstore.service.EnvioService;
import com.mycompany.knstore.service.HistorialEstadoService;
import com.mycompany.knstore.service.PedidoService;
import com.mycompany.knstore.service.dto.EnvioDTO;
import com.mycompany.knstore.service.dto.EnvioDevolucionRequestDTO;
import com.mycompany.knstore.service.dto.EnvioEstadoUpdateRequestDTO;
import com.mycompany.knstore.service.dto.EnvioTrackingRequestDTO;
import com.mycompany.knstore.service.dto.PedidoDTO;
import com.mycompany.knstore.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
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

    private final PedidoService pedidoService;

    private final HistorialEstadoService historialEstadoService;

    public EnvioResource(
        EnvioService envioService,
        EnvioRepository envioRepository,
        PedidoService pedidoService,
        HistorialEstadoService historialEstadoService
    ) {
        this.envioService = envioService;
        this.envioRepository = envioRepository;
        this.pedidoService = pedidoService;
        this.historialEstadoService = historialEstadoService;
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

    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<List<EnvioDTO>> getEnviosPendientes(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of pending Envios");
        Page<EnvioDTO> page = envioService.findPendientesAdmin(pageable);
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

    @PostMapping("/{id}/tracking")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<EnvioDTO> asignarTracking(@PathVariable("id") String id, @Valid @RequestBody EnvioTrackingRequestDTO request) {
        LOG.debug("REST request to assign tracking for Envio : {}", id);
        try {
            Optional<EnvioDTO> result = envioService.asignarNumeroRastreo(
                id,
                request.getNumeroRastreo(),
                request.getTransportadora(),
                request.getUrlRastreo()
            );
            return ResponseUtil.wrapOrNotFound(result, HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, id));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestAlertException(ex.getMessage(), ENTITY_NAME, "trackinginvalido");
        }
    }

    @PostMapping("/{id}/estado")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<EnvioDTO> actualizarEstadoEnvio(
        @PathVariable("id") String id,
        @Valid @RequestBody EnvioEstadoUpdateRequestDTO request
    ) {
        LOG.debug("REST request to actualizar estado Envio {} -> {}", id, request.getEstado());
        Envio envioActual = envioRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));

        if (Objects.equals(envioActual.getEstado(), request.getEstado())) {
            throw new BadRequestAlertException("El envio ya se encuentra en el estado solicitado", ENTITY_NAME, "estadoigual");
        }

        EnvioDTO patch = new EnvioDTO();
        patch.setId(id);
        patch.setEstado(request.getEstado());
        if (request.getObservacion() != null && !request.getObservacion().isBlank()) {
            patch.setObservaciones(request.getObservacion().trim());
        }
        if (EstadoEnvio.DELIVERED.equals(request.getEstado())) {
            patch.setFechaEntrega(Instant.now());
        }

        EnvioDTO actualizado = envioService
            .partialUpdate(patch)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));

        historialEstadoService.registrarCambioEstado(
            "Envio",
            id,
            "estado",
            envioActual.getEstado() != null ? envioActual.getEstado().name() : null,
            request.getEstado().name()
        );

        if (request.getObservacion() != null && !request.getObservacion().isBlank()) {
            historialEstadoService.registrarCambioEstado("Envio", id, "observacionEstado", null, request.getObservacion().trim());
        }

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, actualizado.getId()))
            .body(actualizado);
    }

    @PostMapping("/{id}/devolucion")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<EnvioDTO> registrarDevolucionEnvio(
        @PathVariable("id") String id,
        @Valid @RequestBody EnvioDevolucionRequestDTO request
    ) {
        LOG.debug("REST request to registrar devolucion Envio {}", id);
        Envio envioActual = envioRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));

        if (EstadoEnvio.RETURNED.equals(envioActual.getEstado())) {
            throw new BadRequestAlertException("El envio ya fue marcado como devuelto", ENTITY_NAME, "devolucionduplicada");
        }

        EnvioDTO patch = new EnvioDTO();
        patch.setId(id);
        patch.setEstado(EstadoEnvio.RETURNED);
        patch.setObservaciones(request.getMotivo().trim());
        EnvioDTO actualizado = envioService
            .partialUpdate(patch)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));

        historialEstadoService.registrarCambioEstado(
            "Envio",
            id,
            "estado",
            envioActual.getEstado() != null ? envioActual.getEstado().name() : null,
            EstadoEnvio.RETURNED.name()
        );
        historialEstadoService.registrarCambioEstado("Envio", id, "motivoDevolucion", null, request.getMotivo().trim());

        if (
            envioActual.getPedido() != null &&
            envioActual.getPedido().getId() != null &&
            (EstadoPedido.SHIPPED.equals(envioActual.getPedido().getEstado()) ||
                EstadoPedido.DELIVERED.equals(envioActual.getPedido().getEstado()))
        ) {
            PedidoDTO pedidoPatch = new PedidoDTO();
            pedidoPatch.setId(envioActual.getPedido().getId());
            pedidoPatch.setEstado(EstadoPedido.RETURNED);
            pedidoService.partialUpdate(pedidoPatch);
        }

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, actualizado.getId()))
            .body(actualizado);
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
}
