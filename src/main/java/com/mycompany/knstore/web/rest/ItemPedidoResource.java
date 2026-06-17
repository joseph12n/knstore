package com.mycompany.knstore.web.rest;

import com.mycompany.knstore.repository.ItemPedidoRepository;
import com.mycompany.knstore.service.ItemPedidoService;
import com.mycompany.knstore.service.dto.ItemPedidoDTO;
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
 * REST controller for managing {@link com.mycompany.knstore.domain.ItemPedido}.
 */
@RestController
@RequestMapping("/api/item-pedidos")
public class ItemPedidoResource {

    private static final Logger LOG = LoggerFactory.getLogger(ItemPedidoResource.class);

    private static final String ENTITY_NAME = "itemPedido";

    @Value("${jhipster.clientApp.name:knstore}")
    private String applicationName;

    private final ItemPedidoService itemPedidoService;

    private final ItemPedidoRepository itemPedidoRepository;

    public ItemPedidoResource(ItemPedidoService itemPedidoService, ItemPedidoRepository itemPedidoRepository) {
        this.itemPedidoService = itemPedidoService;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    /**
     * {@code POST  /item-pedidos} : Create a new itemPedido.
     *
     * @param itemPedidoDTO the itemPedidoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new itemPedidoDTO, or with status {@code 400 (Bad Request)} if the itemPedido has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ItemPedidoDTO> createItemPedido(@Valid @RequestBody ItemPedidoDTO itemPedidoDTO) throws URISyntaxException {
        LOG.debug("REST request to save ItemPedido : {}", itemPedidoDTO);
        if (itemPedidoDTO.getId() != null) {
            throw new BadRequestAlertException("A new itemPedido cannot already have an ID", ENTITY_NAME, "idexists");
        }
        itemPedidoDTO = itemPedidoService.save(itemPedidoDTO);
        return ResponseEntity.created(new URI("/api/item-pedidos/" + itemPedidoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, itemPedidoDTO.getId()))
            .body(itemPedidoDTO);
    }

    /**
     * {@code PUT  /item-pedidos/:id} : Updates an existing itemPedido.
     *
     * @param id the id of the itemPedidoDTO to save.
     * @param itemPedidoDTO the itemPedidoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated itemPedidoDTO,
     * or with status {@code 400 (Bad Request)} if the itemPedidoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the itemPedidoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ItemPedidoDTO> updateItemPedido(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody ItemPedidoDTO itemPedidoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ItemPedido : {}, {}", id, itemPedidoDTO);
        if (itemPedidoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, itemPedidoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!itemPedidoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        itemPedidoDTO = itemPedidoService.update(itemPedidoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, itemPedidoDTO.getId()))
            .body(itemPedidoDTO);
    }

    /**
     * {@code PATCH  /item-pedidos/:id} : Partial updates given fields of an existing itemPedido, field will ignore if it is null
     *
     * @param id the id of the itemPedidoDTO to save.
     * @param itemPedidoDTO the itemPedidoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated itemPedidoDTO,
     * or with status {@code 400 (Bad Request)} if the itemPedidoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the itemPedidoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the itemPedidoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ItemPedidoDTO> partialUpdateItemPedido(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody ItemPedidoDTO itemPedidoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ItemPedido partially : {}, {}", id, itemPedidoDTO);
        if (itemPedidoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, itemPedidoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!itemPedidoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ItemPedidoDTO> result = itemPedidoService.partialUpdate(itemPedidoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, itemPedidoDTO.getId())
        );
    }

    /**
     * {@code GET  /item-pedidos} : get all the Item Pedidos.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Item Pedidos in body.
     */
    @GetMapping("")
    public List<ItemPedidoDTO> getAllItemPedidos(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all ItemPedidos");
        return itemPedidoService.findAll();
    }

    /**
     * {@code GET  /item-pedidos/:id} : get the "id" itemPedido.
     *
     * @param id the id of the itemPedidoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the itemPedidoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemPedidoDTO> getItemPedido(@PathVariable("id") String id) {
        LOG.debug("REST request to get ItemPedido : {}", id);
        Optional<ItemPedidoDTO> itemPedidoDTO = itemPedidoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(itemPedidoDTO);
    }

    /**
     * {@code DELETE  /item-pedidos/:id} : delete the "id" itemPedido.
     *
     * @param id the id of the itemPedidoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItemPedido(@PathVariable("id") String id) {
        LOG.debug("REST request to delete ItemPedido : {}", id);
        itemPedidoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id))
            .build();
    }
}
