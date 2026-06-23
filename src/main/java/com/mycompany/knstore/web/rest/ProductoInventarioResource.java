package com.mycompany.knstore.web.rest;

import com.mycompany.knstore.repository.ProductoInventarioRepository;
import com.mycompany.knstore.service.ProductoInventarioService;
import com.mycompany.knstore.service.dto.ProductoInventarioDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.knstore.domain.ProductoInventario}.
 */
@RestController
@RequestMapping("/api/producto-inventarios")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
public class ProductoInventarioResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductoInventarioResource.class);

    private static final String ENTITY_NAME = "productoInventario";

    @Value("${jhipster.clientApp.name:knstore}")
    private String applicationName;

    private final ProductoInventarioService productoInventarioService;

    private final ProductoInventarioRepository productoInventarioRepository;

    public ProductoInventarioResource(
        ProductoInventarioService productoInventarioService,
        ProductoInventarioRepository productoInventarioRepository
    ) {
        this.productoInventarioService = productoInventarioService;
        this.productoInventarioRepository = productoInventarioRepository;
    }

    /**
     * {@code POST  /producto-inventarios} : Create a new productoInventario.
     *
     * @param productoInventarioDTO the productoInventarioDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productoInventarioDTO, or with status {@code 400 (Bad Request)} if the productoInventario has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProductoInventarioDTO> createProductoInventario(@Valid @RequestBody ProductoInventarioDTO productoInventarioDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ProductoInventario : {}", productoInventarioDTO);
        if (productoInventarioDTO.getId() != null) {
            throw new BadRequestAlertException("A new productoInventario cannot already have an ID", ENTITY_NAME, "idexists");
        }
        productoInventarioDTO = productoInventarioService.save(productoInventarioDTO);
        return ResponseEntity.created(new URI("/api/producto-inventarios/" + productoInventarioDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, productoInventarioDTO.getId()))
            .body(productoInventarioDTO);
    }

    /**
     * {@code PUT  /producto-inventarios/:id} : Updates an existing productoInventario.
     *
     * @param id the id of the productoInventarioDTO to save.
     * @param productoInventarioDTO the productoInventarioDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productoInventarioDTO,
     * or with status {@code 400 (Bad Request)} if the productoInventarioDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productoInventarioDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductoInventarioDTO> updateProductoInventario(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody ProductoInventarioDTO productoInventarioDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProductoInventario : {}, {}", id, productoInventarioDTO);
        if (productoInventarioDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productoInventarioDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productoInventarioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        productoInventarioDTO = productoInventarioService.update(productoInventarioDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, productoInventarioDTO.getId()))
            .body(productoInventarioDTO);
    }

    /**
     * {@code PATCH  /producto-inventarios/:id} : Partial updates given fields of an existing productoInventario, field will ignore if it is null
     *
     * @param id the id of the productoInventarioDTO to save.
     * @param productoInventarioDTO the productoInventarioDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productoInventarioDTO,
     * or with status {@code 400 (Bad Request)} if the productoInventarioDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productoInventarioDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productoInventarioDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductoInventarioDTO> partialUpdateProductoInventario(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody ProductoInventarioDTO productoInventarioDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProductoInventario partially : {}, {}", id, productoInventarioDTO);
        if (productoInventarioDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productoInventarioDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productoInventarioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductoInventarioDTO> result = productoInventarioService.partialUpdate(productoInventarioDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, productoInventarioDTO.getId())
        );
    }

    /**
     * {@code GET  /producto-inventarios} : get all the Producto Inventarios.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Producto Inventarios in body.
     */
    @GetMapping("")
    public List<ProductoInventarioDTO> getAllProductoInventarios(@RequestParam(name = "filter", required = false) String filter) {
        if ("producto-is-null".equals(filter)) {
            LOG.debug("REST request to get all ProductoInventarios where producto is null");
            return productoInventarioService.findAllWhereProductoIsNull();
        }
        LOG.debug("REST request to get all ProductoInventarios");
        return productoInventarioService.findAll();
    }

    /**
     * {@code GET  /producto-inventarios/:id} : get the "id" productoInventario.
     *
     * @param id the id of the productoInventarioDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productoInventarioDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoInventarioDTO> getProductoInventario(@PathVariable("id") String id) {
        LOG.debug("REST request to get ProductoInventario : {}", id);
        Optional<ProductoInventarioDTO> productoInventarioDTO = productoInventarioService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productoInventarioDTO);
    }

    /**
     * {@code DELETE  /producto-inventarios/:id} : delete the "id" productoInventario.
     *
     * @param id the id of the productoInventarioDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductoInventario(@PathVariable("id") String id) {
        LOG.debug("REST request to delete ProductoInventario : {}", id);
        productoInventarioService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id))
            .build();
    }
}
