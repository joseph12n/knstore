package com.mycompany.knstore.web.rest;

import com.mycompany.knstore.repository.CarritoRepository;
import com.mycompany.knstore.service.CarritoService;
import com.mycompany.knstore.service.dto.CarritoDTO;
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
 * REST controller for managing {@link com.mycompany.knstore.domain.Carrito}.
 */
@RestController
@RequestMapping("/api/carritos")
public class CarritoResource {

    private static final Logger LOG = LoggerFactory.getLogger(CarritoResource.class);

    private static final String ENTITY_NAME = "carrito";

    @Value("${jhipster.clientApp.name:knstore}")
    private String applicationName;

    private final CarritoService carritoService;

    private final CarritoRepository carritoRepository;

    public CarritoResource(CarritoService carritoService, CarritoRepository carritoRepository) {
        this.carritoService = carritoService;
        this.carritoRepository = carritoRepository;
    }

    /**
     * {@code POST  /carritos} : Create a new carrito.
     *
     * @param carritoDTO the carritoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new carritoDTO, or with status {@code 400 (Bad Request)} if the carrito has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CarritoDTO> createCarrito(@Valid @RequestBody CarritoDTO carritoDTO) throws URISyntaxException {
        LOG.debug("REST request to save Carrito : {}", carritoDTO);
        if (carritoDTO.getId() != null) {
            throw new BadRequestAlertException("A new carrito cannot already have an ID", ENTITY_NAME, "idexists");
        }
        carritoDTO = carritoService.save(carritoDTO);
        return ResponseEntity.created(new URI("/api/carritos/" + carritoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, carritoDTO.getId()))
            .body(carritoDTO);
    }

    /**
     * {@code PUT  /carritos/:id} : Updates an existing carrito.
     *
     * @param id the id of the carritoDTO to save.
     * @param carritoDTO the carritoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carritoDTO,
     * or with status {@code 400 (Bad Request)} if the carritoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the carritoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CarritoDTO> updateCarrito(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody CarritoDTO carritoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Carrito : {}, {}", id, carritoDTO);
        if (carritoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carritoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carritoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        carritoDTO = carritoService.update(carritoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, carritoDTO.getId()))
            .body(carritoDTO);
    }

    /**
     * {@code PATCH  /carritos/:id} : Partial updates given fields of an existing carrito, field will ignore if it is null
     *
     * @param id the id of the carritoDTO to save.
     * @param carritoDTO the carritoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carritoDTO,
     * or with status {@code 400 (Bad Request)} if the carritoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the carritoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the carritoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CarritoDTO> partialUpdateCarrito(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody CarritoDTO carritoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Carrito partially : {}, {}", id, carritoDTO);
        if (carritoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carritoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carritoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CarritoDTO> result = carritoService.partialUpdate(carritoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, carritoDTO.getId())
        );
    }

    /**
     * {@code GET  /carritos} : get all the Carritos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Carritos in body.
     */
    @GetMapping("")
    public List<CarritoDTO> getAllCarritos() {
        LOG.debug("REST request to get all Carritos");
        return carritoService.findAll();
    }

    /**
     * {@code GET  /carritos/:id} : get the "id" carrito.
     *
     * @param id the id of the carritoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the carritoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CarritoDTO> getCarrito(@PathVariable("id") String id) {
        LOG.debug("REST request to get Carrito : {}", id);
        Optional<CarritoDTO> carritoDTO = carritoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(carritoDTO);
    }

    /**
     * {@code DELETE  /carritos/:id} : delete the "id" carrito.
     *
     * @param id the id of the carritoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarrito(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Carrito : {}", id);
        carritoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id))
            .build();
    }
}
