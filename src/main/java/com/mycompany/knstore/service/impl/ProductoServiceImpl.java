package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.repository.*;
import com.mycompany.knstore.service.ProductoService;
import com.mycompany.knstore.service.dto.ProductoDTO;
import com.mycompany.knstore.service.mapper.ProductoMapper;
import java.util.HashSet;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.Producto}.
 */
@Service
public class ProductoServiceImpl implements ProductoService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductoServiceImpl.class);

    private final ProductoRepository productoRepository;

    private final ProductoImagenRepository productoImagenRepository;

    private final ProductoPrecioRepository productoPrecioRepository;

    private final ProductoInventarioRepository productoInventarioRepository;

    private final CategoriaRepository categoriaRepository;

    private final SubcategoriaRepository subcategoriaRepository;

    private final MarcaRepository marcaRepository;

    private final CategoriaIVARepository categoriaIVARepository;

    private final ProductoMapper productoMapper;

    public ProductoServiceImpl(
        ProductoRepository productoRepository,
        ProductoImagenRepository productoImagenRepository,
        ProductoPrecioRepository productoPrecioRepository,
        ProductoInventarioRepository productoInventarioRepository,
        CategoriaRepository categoriaRepository,
        SubcategoriaRepository subcategoriaRepository,
        MarcaRepository marcaRepository,
        CategoriaIVARepository categoriaIVARepository,
        ProductoMapper productoMapper
    ) {
        this.productoRepository = productoRepository;
        this.productoImagenRepository = productoImagenRepository;
        this.productoPrecioRepository = productoPrecioRepository;
        this.productoInventarioRepository = productoInventarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.subcategoriaRepository = subcategoriaRepository;
        this.marcaRepository = marcaRepository;
        this.categoriaIVARepository = categoriaIVARepository;
        this.productoMapper = productoMapper;
    }

    @Override
    public ProductoDTO save(ProductoDTO productoDTO) {
        LOG.debug("Request to save Producto : {}", productoDTO);
        Producto producto = productoMapper.toEntity(productoDTO);
        producto = productoRepository.save(producto);
        return productoMapper.toDto(producto);
    }

    @Override
    public ProductoDTO update(ProductoDTO productoDTO) {
        LOG.debug("Request to update Producto : {}", productoDTO);
        Producto producto = productoMapper.toEntity(productoDTO);
        producto = productoRepository.save(producto);
        return productoMapper.toDto(producto);
    }

    @Override
    public Optional<ProductoDTO> partialUpdate(ProductoDTO productoDTO) {
        LOG.debug("Request to partially update Producto : {}", productoDTO);

        return productoRepository
            .findById(productoDTO.getId())
            .map(existingProducto -> {
                productoMapper.partialUpdate(existingProducto, productoDTO);

                return existingProducto;
            })
            .map(productoRepository::save)
            .map(productoMapper::toDto);
    }

    @Override
    public Page<ProductoDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Productos");
        return productoRepository.findAll(pageable).map(this::loadRelationships).map(productoMapper::toDto);
    }

    public Page<ProductoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productoRepository.findAllWithEagerRelationships(pageable).map(this::loadRelationships).map(productoMapper::toDto);
    }

    @Override
    public Optional<ProductoDTO> findOne(String id) {
        LOG.debug("Request to get Producto : {}", id);
        return productoRepository.findOneWithEagerRelationships(id).map(this::loadRelationships).map(productoMapper::toDto);
    }

    @Override
    public Optional<ProductoDTO> findBySlug(String slug) {
        LOG.debug("Request to get Producto by slug : {}", slug);
        return productoRepository.findBySlug(slug).map(this::loadRelationships).map(productoMapper::toDto);
    }

    private Producto loadRelationships(Producto producto) {
        if (producto == null || producto.getId() == null) {
            return producto;
        }

        if (producto.getPrecio() != null && producto.getPrecio().getId() != null) {
            productoPrecioRepository.findById(producto.getPrecio().getId()).ifPresent(producto::setPrecio);
        }

        if (producto.getInventario() != null && producto.getInventario().getId() != null) {
            productoInventarioRepository.findById(producto.getInventario().getId()).ifPresent(producto::setInventario);
        }

        if (producto.getCategoria() != null && producto.getCategoria().getId() != null) {
            categoriaRepository.findById(producto.getCategoria().getId()).ifPresent(producto::setCategoria);
        }

        if (producto.getSubcategoria() != null && producto.getSubcategoria().getId() != null) {
            subcategoriaRepository.findById(producto.getSubcategoria().getId()).ifPresent(subcategoria -> {
                producto.setSubcategoria(subcategoria);
                if (subcategoria.getCategoria() != null && subcategoria.getCategoria().getId() != null) {
                    categoriaRepository.findById(subcategoria.getCategoria().getId()).ifPresent(subcategoria::setCategoria);
                }
            });
        }

        if (producto.getMarca() != null && producto.getMarca().getId() != null) {
            marcaRepository.findById(producto.getMarca().getId()).ifPresent(producto::setMarca);
        }

        if (producto.getCategoriaIva() != null && producto.getCategoriaIva().getId() != null) {
            categoriaIVARepository.findById(producto.getCategoriaIva().getId()).ifPresent(producto::setCategoriaIva);
        }

        return loadImages(producto);
    }

    private Producto loadImages(Producto producto) {
        if (producto != null && producto.getId() != null) {
            producto.setImageneses(new HashSet<>(productoImagenRepository.findByProductoId(producto.getId())));
        }
        return producto;
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Producto : {}", id);
        productoRepository.deleteById(id);
    }
}
