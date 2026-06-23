package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.ProductoInventario;
import com.mycompany.knstore.repository.ProductoInventarioRepository;
import com.mycompany.knstore.service.ProductoInventarioService;
import com.mycompany.knstore.service.dto.ProductoInventarioDTO;
import com.mycompany.knstore.service.mapper.ProductoInventarioMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.ProductoInventario}.
 */
@Service
public class ProductoInventarioServiceImpl implements ProductoInventarioService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductoInventarioServiceImpl.class);

    private final ProductoInventarioRepository productoInventarioRepository;

    private final ProductoInventarioMapper productoInventarioMapper;

    public ProductoInventarioServiceImpl(
        ProductoInventarioRepository productoInventarioRepository,
        ProductoInventarioMapper productoInventarioMapper
    ) {
        this.productoInventarioRepository = productoInventarioRepository;
        this.productoInventarioMapper = productoInventarioMapper;
    }

    @Override
    public ProductoInventarioDTO save(ProductoInventarioDTO productoInventarioDTO) {
        LOG.debug("Request to save ProductoInventario : {}", productoInventarioDTO);
        ProductoInventario productoInventario = productoInventarioMapper.toEntity(productoInventarioDTO);
        productoInventario = productoInventarioRepository.save(productoInventario);
        return productoInventarioMapper.toDto(productoInventario);
    }

    @Override
    public ProductoInventarioDTO update(ProductoInventarioDTO productoInventarioDTO) {
        LOG.debug("Request to update ProductoInventario : {}", productoInventarioDTO);
        ProductoInventario productoInventario = productoInventarioMapper.toEntity(productoInventarioDTO);
        productoInventario = productoInventarioRepository.save(productoInventario);
        return productoInventarioMapper.toDto(productoInventario);
    }

    @Override
    public Optional<ProductoInventarioDTO> partialUpdate(ProductoInventarioDTO productoInventarioDTO) {
        LOG.debug("Request to partially update ProductoInventario : {}", productoInventarioDTO);

        return productoInventarioRepository
            .findById(productoInventarioDTO.getId())
            .map(existingProductoInventario -> {
                productoInventarioMapper.partialUpdate(existingProductoInventario, productoInventarioDTO);

                return existingProductoInventario;
            })
            .map(productoInventarioRepository::save)
            .map(productoInventarioMapper::toDto);
    }

    @Override
    public List<ProductoInventarioDTO> findAll() {
        LOG.debug("Request to get all ProductoInventarios");
        return productoInventarioRepository
            .findAll()
            .stream()
            .map(productoInventarioMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get all the productoInventarios where Producto is {@code null}.
     *  @return the list of entities.
     */

    public List<ProductoInventarioDTO> findAllWhereProductoIsNull() {
        LOG.debug("Request to get all productoInventarios where Producto is null");
        return StreamSupport.stream(productoInventarioRepository.findAll().spliterator(), false)
            .filter(productoInventario -> productoInventario.getProducto() == null)
            .map(productoInventarioMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Optional<ProductoInventarioDTO> findOne(String id) {
        LOG.debug("Request to get ProductoInventario : {}", id);
        return productoInventarioRepository.findById(id).map(productoInventarioMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete ProductoInventario : {}", id);
        productoInventarioRepository.deleteById(id);
    }
}
