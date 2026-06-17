package com.mycompany.knstore.web.rest;

import com.mycompany.knstore.repository.ProductoImagenRepository;
import com.mycompany.knstore.service.ProductoImagenService;
import com.mycompany.knstore.service.dto.ProductoImagenDTO;
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
 * REST controller for managing {@link com.mycompany.knstore.domain.ProductoImagen}.
 */
@RestController
@RequestMapping("/api/producto-imagens")
public class ProductoImagenResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductoImagenResource.class);

    private static final String ENTITY_NAME = "productoImagen";

    @Value("${jhipster.clientApp.name:knstore}")
    private String applicationName;

    private final ProductoImagenService productoImagenService;

    private final ProductoImagenRepository productoImagenRepository;

    public ProductoImagenResource(ProductoImagenService productoImagenService, ProductoImagenRepository productoImagenRepository) {
        this.productoImagenService = productoImagenService;
        this.productoImagenRepository = productoImagenRepository;
    }

    /**
     * {@code POST  /producto-imagens} : Create a new productoImagen.
     *
     * @param productoImagenDTO the productoImagenDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productoImagenDTO, or with status {@code 400 (Bad Request)} if the productoImagen has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProductoImagenDTO> createProductoImagen(@Valid @RequestBody ProductoImagenDTO productoImagenDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ProductoImagen : {}", productoImagenDTO);
        if (productoImagenDTO.getId() != null) {
            throw new BadRequestAlertException("A new productoImagen cannot already have an ID", ENTITY_NAME, "idexists");
        }
        productoImagenDTO = productoImagenService.save(productoImagenDTO);
        return ResponseEntity.created(new URI("/api/producto-imagens/" + productoImagenDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, productoImagenDTO.getId()))
            .body(productoImagenDTO);
    }

    /**
     * {@code PUT  /producto-imagens/:id} : Updates an existing productoImagen.
     *
     * @param id the id of the productoImagenDTO to save.
     * @param productoImagenDTO the productoImagenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productoImagenDTO,
     * or with status {@code 400 (Bad Request)} if the productoImagenDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productoImagenDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductoImagenDTO> updateProductoImagen(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody ProductoImagenDTO productoImagenDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProductoImagen : {}, {}", id, productoImagenDTO);
        if (productoImagenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productoImagenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productoImagenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        productoImagenDTO = productoImagenService.update(productoImagenDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, productoImagenDTO.getId()))
            .body(productoImagenDTO);
    }

    /**
     * {@code PATCH  /producto-imagens/:id} : Partial updates given fields of an existing productoImagen, field will ignore if it is null
     *
     * @param id the id of the productoImagenDTO to save.
     * @param productoImagenDTO the productoImagenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productoImagenDTO,
     * or with status {@code 400 (Bad Request)} if the productoImagenDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productoImagenDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productoImagenDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductoImagenDTO> partialUpdateProductoImagen(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody ProductoImagenDTO productoImagenDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProductoImagen partially : {}, {}", id, productoImagenDTO);
        if (productoImagenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productoImagenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productoImagenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductoImagenDTO> result = productoImagenService.partialUpdate(productoImagenDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, productoImagenDTO.getId())
        );
    }

    /**
     * {@code GET  /producto-imagens} : get all the Producto Imagens.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Producto Imagens in body.
     */
    @GetMapping("")
    public List<ProductoImagenDTO> getAllProductoImagens() {
        LOG.debug("REST request to get all ProductoImagens");
        return productoImagenService.findAll();
    }

    /**
     * {@code GET  /producto-imagens/:id} : get the "id" productoImagen.
     *
     * @param id the id of the productoImagenDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productoImagenDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoImagenDTO> getProductoImagen(@PathVariable("id") String id) {
        LOG.debug("REST request to get ProductoImagen : {}", id);
        Optional<ProductoImagenDTO> productoImagenDTO = productoImagenService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productoImagenDTO);
    }

    /**
     * {@code DELETE  /producto-imagens/:id} : delete the "id" productoImagen.
     *
     * @param id the id of the productoImagenDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductoImagen(@PathVariable("id") String id) {
        LOG.debug("REST request to delete ProductoImagen : {}", id);
        productoImagenService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id))
            .build();
    }
}
