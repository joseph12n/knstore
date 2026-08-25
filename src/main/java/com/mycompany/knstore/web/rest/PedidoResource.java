package com.mycompany.knstore.web.rest;

import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.CheckoutException;
import com.mycompany.knstore.service.CheckoutService;
import com.mycompany.knstore.service.HistorialEstadoService;
import com.mycompany.knstore.service.PedidoService;
import com.mycompany.knstore.service.dto.CheckoutPreviewDTO;
import com.mycompany.knstore.service.dto.CheckoutRequestDTO;
import com.mycompany.knstore.service.dto.CheckoutResultDTO;
import com.mycompany.knstore.service.dto.HistorialEstadoDTO;
import com.mycompany.knstore.service.dto.PedidoDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.knstore.domain.Pedido}.
 */
@RestController
@RequestMapping("/api/pedidos")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER','ROLE_CLIENTE')")
public class PedidoResource {

    private static final Logger LOG = LoggerFactory.getLogger(PedidoResource.class);

    private static final String ENTITY_NAME = "pedido";

    @Value("${jhipster.clientApp.name:knstore}")
    private String applicationName;

    private final PedidoService pedidoService;

    private final PedidoRepository pedidoRepository;

    private final CheckoutService checkoutService;

    private final CuentaRepository cuentaRepository;

    private final HistorialEstadoService historialEstadoService;

    public PedidoResource(
        PedidoService pedidoService,
        PedidoRepository pedidoRepository,
        CheckoutService checkoutService,
        CuentaRepository cuentaRepository,
        HistorialEstadoService historialEstadoService
    ) {
        this.pedidoService = pedidoService;
        this.pedidoRepository = pedidoRepository;
        this.checkoutService = checkoutService;
        this.cuentaRepository = cuentaRepository;
        this.historialEstadoService = historialEstadoService;
    }

    /**
     * {@code POST  /pedidos} : Create a new pedido.
     *
     * @param pedidoDTO the pedidoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pedidoDTO, or with status {@code 400 (Bad Request)} if the pedido has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER') or @resourceAccessService.canAccessPedidoDto(#pedidoDTO)")
    public ResponseEntity<PedidoDTO> createPedido(@Valid @RequestBody PedidoDTO pedidoDTO) throws URISyntaxException {
        LOG.debug("REST request to save Pedido : {}", pedidoDTO);
        if (pedidoDTO.getId() != null) {
            throw new BadRequestAlertException("A new pedido cannot already have an ID", ENTITY_NAME, "idexists");
        }
        pedidoDTO = pedidoService.save(pedidoDTO);
        return ResponseEntity.created(new URI("/api/pedidos/" + pedidoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, pedidoDTO.getId()))
            .body(pedidoDTO);
    }

    /**
     * {@code PUT  /pedidos/:id} : Updates an existing pedido.
     *
     * @param id the id of the pedidoDTO to save.
     * @param pedidoDTO the pedidoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pedidoDTO,
     * or with status {@code 400 (Bad Request)} if the pedidoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pedidoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<PedidoDTO> updatePedido(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody PedidoDTO pedidoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Pedido : {}, {}", id, pedidoDTO);
        if (pedidoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pedidoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pedidoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        pedidoDTO = pedidoService.update(pedidoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pedidoDTO.getId()))
            .body(pedidoDTO);
    }

    /**
     * {@code PATCH  /pedidos/:id} : Partial updates given fields of an existing pedido, field will ignore if it is null
     *
     * @param id the id of the pedidoDTO to save.
     * @param pedidoDTO the pedidoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pedidoDTO,
     * or with status {@code 400 (Bad Request)} if the pedidoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the pedidoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the pedidoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<PedidoDTO> partialUpdatePedido(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody PedidoDTO pedidoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Pedido partially : {}, {}", id, pedidoDTO);
        if (pedidoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pedidoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pedidoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PedidoDTO> result = pedidoService.partialUpdate(pedidoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pedidoDTO.getId())
        );
    }

    /**
     * {@code POST  /pedidos/preview} : calculate checkout totals without persisting the pedido.
     *
     * @param request the checkout request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the preview totals.
     */
    @PostMapping("/preview")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER','ROLE_CLIENTE')")
    public ResponseEntity<CheckoutPreviewDTO> previewCheckout(@Valid @RequestBody CheckoutRequestDTO request) {
        LOG.debug("REST request to preview checkout : {}", request);
        Optional<String> currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId.isEmpty()) {
            throw new BadRequestAlertException("Usuario no autenticado", ENTITY_NAME, "usuariorequerido");
        }
        Cuenta cuenta = cuentaRepository
            .findOneByUserId(currentUserId.orElseThrow())
            .orElseThrow(() -> new BadRequestAlertException("No se encontró la cuenta del cliente", ENTITY_NAME, "cuentarequerida"));
        try {
            CheckoutPreviewDTO result = checkoutService.preview(cuenta, request);
            return ResponseEntity.ok(result);
        } catch (CheckoutException e) {
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "checkouterror");
        }
    }

    /**
     * {@code GET  /pedidos} : get all the Pedidos.
     *
     * @param pageable the pagination information.
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Pedidos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PedidoDTO>> getAllPedidos(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "filter", required = false) String filter
    ) {
        if ("envio-is-null".equals(filter)) {
            LOG.debug("REST request to get all Pedidos where envio is null");
            return new ResponseEntity<>(pedidoService.findAllWhereEnvioIsNull(), HttpStatus.OK);
        }
        LOG.debug("REST request to get a page of Pedidos");
        Page<PedidoDTO> page = pedidoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /pedidos/:id} : get the "id" pedido.
     *
     * @param id the id of the pedidoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pedidoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER') or @resourceAccessService.canAccessPedidoId(#id)")
    public ResponseEntity<PedidoDTO> getPedido(@PathVariable("id") String id) {
        LOG.debug("REST request to get Pedido : {}", id);
        Optional<PedidoDTO> pedidoDTO = pedidoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pedidoDTO);
    }

    /**
     * {@code GET  /pedidos/:id/historial} : get the state transition history of a pedido.
     *
     * @param id the id of the pedido.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transitions.
     */
    @GetMapping("/{id}/historial")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER') or @resourceAccessService.canAccessPedidoId(#id)")
    public ResponseEntity<List<HistorialEstadoDTO>> getHistorialPedido(@PathVariable("id") String id) {
        LOG.debug("REST request to get historial of Pedido : {}", id);
        return ResponseEntity.ok(historialEstadoService.consultar("PEDIDO", id));
    }

    /**
     * {@code DELETE  /pedidos/:id} : delete the "id" pedido.
     *
     * @param id the id of the pedidoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<Void> deletePedido(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Pedido : {}", id);
        pedidoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id))
            .build();
    }

    /**
     * {@code PATCH  /pedidos/:id/estado} : change the estado of a pedido (admin operation).
     *
     * @param id the id of the pedido.
     * @param request the new estado.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pedidoDTO.
     */
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<PedidoDTO> cambiarEstadoPedido(
        @PathVariable("id") String id,
        @Valid @RequestBody CambiarEstadoPedidoRequest request
    ) {
        LOG.debug("REST request to change estado of Pedido : {} -> {}", id, request.estado());
        try {
            PedidoDTO result = pedidoService.cambiarEstado(id, request.estado());
            return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId()))
                .body(result);
        } catch (IllegalStateException e) {
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "transicioninvalida");
        }
    }

    /**
     * Request DTO for changing the estado of a pedido.
     */
    public record CambiarEstadoPedidoRequest(@NotNull EstadoPedido estado) {}

    /**
     * {@code POST  /pedidos/:id/cancelar} : cancel a pedido. Disponible para el cliente
     * propietario del pedido (dentro de la ventana de 1 hora desde la compra, y con
     * reembolso simbolico automatico si el pago habia sido aprobado) y para administracion.
     * La maquina de estados y la restauracion del stock se validan en el servicio.
     *
     * @param id the id of the pedido.
     * @param request motivo opcional de la cancelacion.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pedidoDTO.
     */
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER') or @resourceAccessService.canAccessPedidoId(#id)")
    public ResponseEntity<PedidoDTO> cancelarPedido(
        @PathVariable("id") String id,
        @RequestBody(required = false) CancelarPedidoRequest request
    ) {
        LOG.debug("REST request to cancel Pedido : {}", id);
        try {
            String motivo = request != null ? request.motivo() : null;
            PedidoDTO result = SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)
                ? pedidoService.cancelarPedidoCliente(id, motivo)
                : pedidoService.cambiarEstado(id, EstadoPedido.CANCELLED);
            return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId()))
                .body(result);
        } catch (IllegalStateException e) {
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "transicioninvalida");
        }
    }

    /** DTO for cancel request. */
    public record CancelarPedidoRequest(@jakarta.validation.constraints.Size(max = 500) String motivo) {}

    /**
     * {@code POST  /pedidos/checkout} : Procesa un checkout atómico simbólico.
     *
     * @param request datos del checkout.
     * @return el {@link ResponseEntity} con el pedido creado.
     */
    @PostMapping("/checkout")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER','ROLE_CLIENTE')")
    public ResponseEntity<CheckoutResultDTO> checkout(@Valid @RequestBody CheckoutRequestDTO request) {
        LOG.debug("REST request to checkout : {}", request);
        Optional<String> currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId.isEmpty()) {
            throw new BadRequestAlertException("Usuario no autenticado", ENTITY_NAME, "usuariorequerido");
        }
        Cuenta cuenta = cuentaRepository
            .findOneByUserId(currentUserId.orElseThrow())
            .orElseThrow(() -> new BadRequestAlertException("No se encontró la cuenta del cliente", ENTITY_NAME, "cuentarequerida"));
        try {
            CheckoutResultDTO result = checkoutService.checkout(cuenta, request);
            return ResponseEntity.ok(result);
        } catch (CheckoutException e) {
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "checkouterror");
        }
    }
}
