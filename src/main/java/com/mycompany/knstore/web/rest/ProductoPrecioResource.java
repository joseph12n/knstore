package com.mycompany.knstore.web.rest;

import com.mycompany.knstore.repository.ProductoPrecioRepository;
import com.mycompany.knstore.service.ProductoPrecioService;
import com.mycompany.knstore.service.dto.ProductoPrecioDTO;
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
 * REST controller for managing {@link com.mycompany.knstore.domain.ProductoPrecio}.
 */
@RestController
@RequestMapping("/api/producto-precios")
public class ProductoPrecioResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductoPrecioResource.class);

    private static final String ENTITY_NAME = "productoPrecio";

    @Value("${jhipster.clientApp.name:knstore}")
    private String applicationName;

    private final ProductoPrecioService productoPrecioService;

    private final ProductoPrecioRepository productoPrecioRepository;

    public ProductoPrecioResource(ProductoPrecioService productoPrecioService, ProductoPrecioRepository productoPrecioRepository) {
        this.productoPrecioService = productoPrecioService;
        this.productoPrecioRepository = productoPrecioRepository;
    }

    /**
     * {@code POST  /producto-precios} : Create a new productoPrecio.
     *
     * @param productoPrecioDTO the productoPrecioDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productoPrecioDTO, or with status {@code 400 (Bad Request)} if the productoPrecio has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProductoPrecioDTO> createProductoPrecio(@Valid @RequestBody ProductoPrecioDTO productoPrecioDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ProductoPrecio : {}", productoPrecioDTO);
        if (productoPrecioDTO.getId() != null) {
            throw new BadRequestAlertException("A new productoPrecio cannot already have an ID", ENTITY_NAME, "idexists");
        }
        productoPrecioDTO = productoPrecioService.save(productoPrecioDTO);
        return ResponseEntity.created(new URI("/api/producto-precios/" + productoPrecioDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, productoPrecioDTO.getId()))
            .body(productoPrecioDTO);
    }

    /**
     * {@code PUT  /producto-precios/:id} : Updates an existing productoPrecio.
     *
     * @param id the id of the productoPrecioDTO to save.
     * @param productoPrecioDTO the productoPrecioDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productoPrecioDTO,
     * or with status {@code 400 (Bad Request)} if the productoPrecioDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productoPrecioDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductoPrecioDTO> updateProductoPrecio(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody ProductoPrecioDTO productoPrecioDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProductoPrecio : {}, {}", id, productoPrecioDTO);
        if (productoPrecioDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productoPrecioDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productoPrecioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        productoPrecioDTO = productoPrecioService.update(productoPrecioDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, productoPrecioDTO.getId()))
            .body(productoPrecioDTO);
    }

    /**
     * {@code PATCH  /producto-precios/:id} : Partial updates given fields of an existing productoPrecio, field will ignore if it is null
     *
     * @param id the id of the productoPrecioDTO to save.
     * @param productoPrecioDTO the productoPrecioDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productoPrecioDTO,
     * or with status {@code 400 (Bad Request)} if the productoPrecioDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productoPrecioDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productoPrecioDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductoPrecioDTO> partialUpdateProductoPrecio(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody ProductoPrecioDTO productoPrecioDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProductoPrecio partially : {}, {}", id, productoPrecioDTO);
        if (productoPrecioDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productoPrecioDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productoPrecioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductoPrecioDTO> result = productoPrecioService.partialUpdate(productoPrecioDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, productoPrecioDTO.getId())
        );
    }

    /**
     * {@code GET  /producto-precios} : get all the Producto Precios.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Producto Precios in body.
     */
    @GetMapping("")
    public List<ProductoPrecioDTO> getAllProductoPrecios(@RequestParam(name = "filter", required = false) String filter) {
        if ("producto-is-null".equals(filter)) {
            LOG.debug("REST request to get all ProductoPrecios where producto is null");
            return productoPrecioService.findAllWhereProductoIsNull();
        }
        LOG.debug("REST request to get all ProductoPrecios");
        return productoPrecioService.findAll();
    }

    /**
     * {@code GET  /producto-precios/:id} : get the "id" productoPrecio.
     *
     * @param id the id of the productoPrecioDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productoPrecioDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoPrecioDTO> getProductoPrecio(@PathVariable("id") String id) {
        LOG.debug("REST request to get ProductoPrecio : {}", id);
        Optional<ProductoPrecioDTO> productoPrecioDTO = productoPrecioService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productoPrecioDTO);
    }

    /**
     * {@code DELETE  /producto-precios/:id} : delete the "id" productoPrecio.
     *
     * @param id the id of the productoPrecioDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductoPrecio(@PathVariable("id") String id) {
        LOG.debug("REST request to delete ProductoPrecio : {}", id);
        productoPrecioService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id))
            .build();
    }
}
