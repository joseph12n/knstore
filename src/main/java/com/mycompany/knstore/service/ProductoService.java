package com.mycompany.knstore.service;

import com.mycompany.knstore.service.dto.ProductoDTO;
import java.math.BigDecimal;
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
     * Buscar productos publicos por texto y filtros.
     *
     * @param q texto libre para nombre, descripcion, sku, referencia o slug.
     * @param categoriaId id de categoria opcional.
     * @param subcategoriaId id de subcategoria opcional.
     * @param marcaId id de marca opcional.
     * @param minPrecio precio minimo opcional.
     * @param maxPrecio precio maximo opcional.
     * @param destacado filtro opcional para productos destacados.
     * @param soloActivos si true, limita a productos activos.
     * @param pageable paginacion.
     * @return pagina de productos filtrados.
     */
    Page<ProductoDTO> buscarPublico(
        String q,
        String categoriaId,
        String subcategoriaId,
        String marcaId,
        BigDecimal minPrecio,
        BigDecimal maxPrecio,
        Boolean destacado,
        boolean soloActivos,
        Pageable pageable
    );

    /**
     * Delete the "id" producto.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
