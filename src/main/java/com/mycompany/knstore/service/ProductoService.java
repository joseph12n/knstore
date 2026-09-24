package com.mycompany.knstore.service;

import com.mycompany.knstore.service.dto.ProductoDTO;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.knstore.domain.Producto}.
 */
public interface ProductoService {
    /**
     * Save a producto.
     *
     * @param productoDTO the entity to save.
     * @return the persisted entity.
     */
    ProductoDTO save(ProductoDTO productoDTO);

    /**
     * Updates a producto.
     *
     * @param productoDTO the entity to update.
     * @return the persisted entity.
     */
    ProductoDTO update(ProductoDTO productoDTO);

    /**
     * Partially updates a producto.
     *
     * @param productoDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProductoDTO> partialUpdate(ProductoDTO productoDTO);

    /**
     * Get all the productos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProductoDTO> findAll(Pageable pageable);

    /**
     * Get all the productos with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProductoDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" producto.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProductoDTO> findOne(String id);

    /**
     * Get the producto by slug.
     *
     * @param slug the slug of the entity.
     * @return the entity.
     */
    Optional<ProductoDTO> findBySlug(String slug);

    /**
     * Search active productos by query with optional server-side filters.
     *
     * @param query the search query.
     * @param categoriaId optional categoria or subcategoria id filter.
     * @param marcaId optional marca id filter.
     * @param pageable the pagination information.
     * @return the page of entities.
     */
    Page<ProductoDTO> searchActive(String query, String categoriaId, String marcaId, Pageable pageable);

    /**
     * Obtiene los productos cuyos ids estan en la coleccion indicada (RNF-029).
     * Se resuelven las relaciones y las imagenes en lote para eliminar el N+1.
     *
     * @param ids coleccion de ids de productos.
     * @return lista de DTOs de productos, en el orden de los ids encontrados.
     */
    List<ProductoDTO> findAllByIds(Collection<String> ids);

    /**
     * Delete the "id" producto.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
