package com.mycompany.knstore.web.rest;

import com.mycompany.knstore.repository.ItemCarritoRepository;
import com.mycompany.knstore.service.ItemCarritoService;
import com.mycompany.knstore.service.dto.ItemCarritoDTO;
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
 * REST controller for managing {@link com.mycompany.knstore.domain.ItemCarrito}.
 */
@RestController
@RequestMapping("/api/item-carritos")
public class ItemCarritoResource {

    private static final Logger LOG = LoggerFactory.getLogger(ItemCarritoResource.class);

    private static final String ENTITY_NAME = "itemCarrito";

    @Value("${jhipster.clientApp.name:knstore}")
    private String applicationName;

    private final ItemCarritoService itemCarritoService;

    private final ItemCarritoRepository itemCarritoRepository;

    public ItemCarritoResource(ItemCarritoService itemCarritoService, ItemCarritoRepository itemCarritoRepository) {
        this.itemCarritoService = itemCarritoService;
        this.itemCarritoRepository = itemCarritoRepository;
    }

    /**
     * {@code POST  /item-carritos} : Create a new itemCarrito.
     *
     * @param itemCarritoDTO the itemCarritoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new itemCarritoDTO, or with status {@code 400 (Bad Request)} if the itemCarrito has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ItemCarritoDTO> createItemCarrito(@Valid @RequestBody ItemCarritoDTO itemCarritoDTO) throws URISyntaxException {
        LOG.debug("REST request to save ItemCarrito : {}", itemCarritoDTO);
        if (itemCarritoDTO.getId() != null) {
            throw new BadRequestAlertException("A new itemCarrito cannot already have an ID", ENTITY_NAME, "idexists");
        }
        itemCarritoDTO = itemCarritoService.save(itemCarritoDTO);
        return ResponseEntity.created(new URI("/api/item-carritos/" + itemCarritoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, itemCarritoDTO.getId()))
            .body(itemCarritoDTO);
    }

    /**
     * {@code PUT  /item-carritos/:id} : Updates an existing itemCarrito.
     *
     * @param id the id of the itemCarritoDTO to save.
     * @param itemCarritoDTO the itemCarritoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated itemCarritoDTO,
     * or with status {@code 400 (Bad Request)} if the itemCarritoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the itemCarritoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ItemCarritoDTO> updateItemCarrito(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody ItemCarritoDTO itemCarritoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ItemCarrito : {}, {}", id, itemCarritoDTO);
        if (itemCarritoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, itemCarritoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!itemCarritoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        itemCarritoDTO = itemCarritoService.update(itemCarritoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, itemCarritoDTO.getId()))
            .body(itemCarritoDTO);
    }

    /**
     * {@code PATCH  /item-carritos/:id} : Partial updates given fields of an existing itemCarrito, field will ignore if it is null
     *
     * @param id the id of the itemCarritoDTO to save.
     * @param itemCarritoDTO the itemCarritoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated itemCarritoDTO,
     * or with status {@code 400 (Bad Request)} if the itemCarritoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the itemCarritoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the itemCarritoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ItemCarritoDTO> partialUpdateItemCarrito(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody ItemCarritoDTO itemCarritoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ItemCarrito partially : {}, {}", id, itemCarritoDTO);
        if (itemCarritoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, itemCarritoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!itemCarritoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ItemCarritoDTO> result = itemCarritoService.partialUpdate(itemCarritoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, itemCarritoDTO.getId())
        );
    }

    /**
     * {@code GET  /item-carritos} : get all the Item Carritos.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Item Carritos in body.
     */
    @GetMapping("")
    public List<ItemCarritoDTO> getAllItemCarritos(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all ItemCarritos");
        return itemCarritoService.findAll();
    }

    /**
     * {@code GET  /item-carritos/:id} : get the "id" itemCarrito.
     *
     * @param id the id of the itemCarritoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the itemCarritoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemCarritoDTO> getItemCarrito(@PathVariable("id") String id) {
        LOG.debug("REST request to get ItemCarrito : {}", id);
        Optional<ItemCarritoDTO> itemCarritoDTO = itemCarritoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(itemCarritoDTO);
    }

    /**
     * {@code DELETE  /item-carritos/:id} : delete the "id" itemCarrito.
     *
     * @param id the id of the itemCarritoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItemCarrito(@PathVariable("id") String id) {
        LOG.debug("REST request to delete ItemCarrito : {}", id);
        itemCarritoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id))
            .build();
    }
}
