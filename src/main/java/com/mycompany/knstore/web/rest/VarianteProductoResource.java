package com.mycompany.knstore.web.rest;

import com.mycompany.knstore.repository.VarianteProductoRepository;
import com.mycompany.knstore.service.VarianteProductoService;
import com.mycompany.knstore.service.dto.VarianteProductoDTO;
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
 * REST controller for managing {@link com.mycompany.knstore.domain.VarianteProducto}.
 */
@RestController
@RequestMapping("/api/variante-productos")
public class VarianteProductoResource {

    private static final Logger LOG = LoggerFactory.getLogger(VarianteProductoResource.class);

    private static final String ENTITY_NAME = "varianteProducto";

    @Value("${jhipster.clientApp.name:knstore}")
    private String applicationName;

    private final VarianteProductoService varianteProductoService;

    private final VarianteProductoRepository varianteProductoRepository;

    public VarianteProductoResource(
        VarianteProductoService varianteProductoService,
        VarianteProductoRepository varianteProductoRepository
    ) {
        this.varianteProductoService = varianteProductoService;
        this.varianteProductoRepository = varianteProductoRepository;
    }

    /**
     * {@code POST  /variante-productos} : Create a new varianteProducto.
     *
     * @param varianteProductoDTO the varianteProductoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new varianteProductoDTO, or with status {@code 400 (Bad Request)} if the varianteProducto has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<VarianteProductoDTO> createVarianteProducto(@Valid @RequestBody VarianteProductoDTO varianteProductoDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save VarianteProducto : {}", varianteProductoDTO);
        if (varianteProductoDTO.getId() != null) {
            throw new BadRequestAlertException("A new varianteProducto cannot already have an ID", ENTITY_NAME, "idexists");
        }
        varianteProductoDTO = varianteProductoService.save(varianteProductoDTO);
        return ResponseEntity.created(new URI("/api/variante-productos/" + varianteProductoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, varianteProductoDTO.getId()))
            .body(varianteProductoDTO);
    }

    /**
     * {@code PUT  /variante-productos/:id} : Updates an existing varianteProducto.
     *
     * @param id the id of the varianteProductoDTO to save.
     * @param varianteProductoDTO the varianteProductoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated varianteProductoDTO,
     * or with status {@code 400 (Bad Request)} if the varianteProductoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the varianteProductoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VarianteProductoDTO> updateVarianteProducto(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody VarianteProductoDTO varianteProductoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update VarianteProducto : {}, {}", id, varianteProductoDTO);
        if (varianteProductoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, varianteProductoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!varianteProductoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        varianteProductoDTO = varianteProductoService.update(varianteProductoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, varianteProductoDTO.getId()))
            .body(varianteProductoDTO);
    }

    /**
     * {@code PATCH  /variante-productos/:id} : Partial updates given fields of an existing varianteProducto, field will ignore if it is null
     *
     * @param id the id of the varianteProductoDTO to save.
     * @param varianteProductoDTO the varianteProductoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated varianteProductoDTO,
     * or with status {@code 400 (Bad Request)} if the varianteProductoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the varianteProductoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the varianteProductoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VarianteProductoDTO> partialUpdateVarianteProducto(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody VarianteProductoDTO varianteProductoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update VarianteProducto partially : {}, {}", id, varianteProductoDTO);
        if (varianteProductoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, varianteProductoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!varianteProductoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VarianteProductoDTO> result = varianteProductoService.partialUpdate(varianteProductoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, varianteProductoDTO.getId())
        );
    }

    /**
     * {@code GET  /variante-productos} : get all the Variante Productos.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Variante Productos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<VarianteProductoDTO>> getAllVarianteProductos(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of VarianteProductos");
        Page<VarianteProductoDTO> page;
        if (eagerload) {
            page = varianteProductoService.findAllWithEagerRelationships(pageable);
        } else {
            page = varianteProductoService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /variante-productos/:id} : get the "id" varianteProducto.
     *
     * @param id the id of the varianteProductoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the varianteProductoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VarianteProductoDTO> getVarianteProducto(@PathVariable("id") String id) {
        LOG.debug("REST request to get VarianteProducto : {}", id);
        Optional<VarianteProductoDTO> varianteProductoDTO = varianteProductoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(varianteProductoDTO);
    }

    /**
     * {@code DELETE  /variante-productos/:id} : delete the "id" varianteProducto.
     *
     * @param id the id of the varianteProductoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVarianteProducto(@PathVariable("id") String id) {
        LOG.debug("REST request to delete VarianteProducto : {}", id);
        varianteProductoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id))
            .build();
    }
}
