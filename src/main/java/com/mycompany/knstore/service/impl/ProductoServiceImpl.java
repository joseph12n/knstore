package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.repository.ProductoImagenRepository;
import com.mycompany.knstore.repository.ProductoRepository;
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

    private final ProductoMapper productoMapper;

    public ProductoServiceImpl(
        ProductoRepository productoRepository,
        ProductoImagenRepository productoImagenRepository,
        ProductoMapper productoMapper
    ) {
        this.productoRepository = productoRepository;
        this.productoImagenRepository = productoImagenRepository;
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
        return productoRepository.findAll(pageable).map(this::loadImages).map(productoMapper::toDto);
    }

    public Page<ProductoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productoRepository.findAllWithEagerRelationships(pageable).map(this::loadImages).map(productoMapper::toDto);
    }

    @Override
    public Optional<ProductoDTO> findOne(String id) {
        LOG.debug("Request to get Producto : {}", id);
        return productoRepository.findOneWithEagerRelationships(id).map(this::loadImages).map(productoMapper::toDto);
    }

    @Override
    public Optional<ProductoDTO> findBySlug(String slug) {
        LOG.debug("Request to get Producto by slug : {}", slug);
        return productoRepository.findBySlug(slug).map(this::loadImages).map(productoMapper::toDto);
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
