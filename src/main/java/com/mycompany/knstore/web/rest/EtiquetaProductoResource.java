package com.mycompany.knstore.web.rest;

import com.mycompany.knstore.repository.EtiquetaProductoRepository;
import com.mycompany.knstore.service.EtiquetaProductoService;
import com.mycompany.knstore.service.dto.EtiquetaProductoDTO;
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
 * REST controller for managing {@link com.mycompany.knstore.domain.EtiquetaProducto}.
 */
@RestController
@RequestMapping("/api/etiqueta-productos")
public class EtiquetaProductoResource {

    private static final Logger LOG = LoggerFactory.getLogger(EtiquetaProductoResource.class);

    private static final String ENTITY_NAME = "etiquetaProducto";

    @Value("${jhipster.clientApp.name:knstore}")
    private String applicationName;

    private final EtiquetaProductoService etiquetaProductoService;

    private final EtiquetaProductoRepository etiquetaProductoRepository;

    public EtiquetaProductoResource(
        EtiquetaProductoService etiquetaProductoService,
        EtiquetaProductoRepository etiquetaProductoRepository
    ) {
        this.etiquetaProductoService = etiquetaProductoService;
        this.etiquetaProductoRepository = etiquetaProductoRepository;
    }

    /**
     * {@code POST  /etiqueta-productos} : Create a new etiquetaProducto.
     *
     * @param etiquetaProductoDTO the etiquetaProductoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new etiquetaProductoDTO, or with status {@code 400 (Bad Request)} if the etiquetaProducto has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<EtiquetaProductoDTO> createEtiquetaProducto(@Valid @RequestBody EtiquetaProductoDTO etiquetaProductoDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save EtiquetaProducto : {}", etiquetaProductoDTO);
        if (etiquetaProductoDTO.getId() != null) {
            throw new BadRequestAlertException("A new etiquetaProducto cannot already have an ID", ENTITY_NAME, "idexists");
        }
        etiquetaProductoDTO = etiquetaProductoService.save(etiquetaProductoDTO);
        return ResponseEntity.created(new URI("/api/etiqueta-productos/" + etiquetaProductoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, etiquetaProductoDTO.getId()))
            .body(etiquetaProductoDTO);
    }

    /**
     * {@code PUT  /etiqueta-productos/:id} : Updates an existing etiquetaProducto.
     *
     * @param id the id of the etiquetaProductoDTO to save.
     * @param etiquetaProductoDTO the etiquetaProductoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated etiquetaProductoDTO,
     * or with status {@code 400 (Bad Request)} if the etiquetaProductoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the etiquetaProductoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EtiquetaProductoDTO> updateEtiquetaProducto(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody EtiquetaProductoDTO etiquetaProductoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update EtiquetaProducto : {}, {}", id, etiquetaProductoDTO);
        if (etiquetaProductoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, etiquetaProductoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!etiquetaProductoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        etiquetaProductoDTO = etiquetaProductoService.update(etiquetaProductoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, etiquetaProductoDTO.getId()))
            .body(etiquetaProductoDTO);
    }

    /**
     * {@code PATCH  /etiqueta-productos/:id} : Partial updates given fields of an existing etiquetaProducto, field will ignore if it is null
     *
     * @param id the id of the etiquetaProductoDTO to save.
     * @param etiquetaProductoDTO the etiquetaProductoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated etiquetaProductoDTO,
     * or with status {@code 400 (Bad Request)} if the etiquetaProductoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the etiquetaProductoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the etiquetaProductoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EtiquetaProductoDTO> partialUpdateEtiquetaProducto(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody EtiquetaProductoDTO etiquetaProductoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update EtiquetaProducto partially : {}, {}", id, etiquetaProductoDTO);
        if (etiquetaProductoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, etiquetaProductoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!etiquetaProductoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EtiquetaProductoDTO> result = etiquetaProductoService.partialUpdate(etiquetaProductoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, etiquetaProductoDTO.getId())
        );
    }

    /**
     * {@code GET  /etiqueta-productos} : get all the Etiqueta Productos.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Etiqueta Productos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<EtiquetaProductoDTO>> getAllEtiquetaProductos(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of EtiquetaProductos");
        Page<EtiquetaProductoDTO> page;
        if (eagerload) {
            page = etiquetaProductoService.findAllWithEagerRelationships(pageable);
        } else {
            page = etiquetaProductoService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /etiqueta-productos/:id} : get the "id" etiquetaProducto.
     *
     * @param id the id of the etiquetaProductoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the etiquetaProductoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EtiquetaProductoDTO> getEtiquetaProducto(@PathVariable("id") String id) {
        LOG.debug("REST request to get EtiquetaProducto : {}", id);
        Optional<EtiquetaProductoDTO> etiquetaProductoDTO = etiquetaProductoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(etiquetaProductoDTO);
    }

    /**
     * {@code DELETE  /etiqueta-productos/:id} : delete the "id" etiquetaProducto.
     *
     * @param id the id of the etiquetaProductoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEtiquetaProducto(@PathVariable("id") String id) {
        LOG.debug("REST request to delete EtiquetaProducto : {}", id);
        etiquetaProductoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id))
            .build();
    }
}
