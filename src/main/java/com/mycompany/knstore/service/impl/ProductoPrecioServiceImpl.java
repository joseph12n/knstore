package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.ProductoPrecio;
import com.mycompany.knstore.repository.ProductoPrecioRepository;
import com.mycompany.knstore.service.ProductoPrecioService;
import com.mycompany.knstore.service.dto.ProductoPrecioDTO;
import com.mycompany.knstore.service.mapper.ProductoPrecioMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.ProductoPrecio}.
 */
@Service
public class ProductoPrecioServiceImpl implements ProductoPrecioService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductoPrecioServiceImpl.class);

    private final ProductoPrecioRepository productoPrecioRepository;

    private final ProductoPrecioMapper productoPrecioMapper;

    public ProductoPrecioServiceImpl(ProductoPrecioRepository productoPrecioRepository, ProductoPrecioMapper productoPrecioMapper) {
        this.productoPrecioRepository = productoPrecioRepository;
        this.productoPrecioMapper = productoPrecioMapper;
    }

    @Override
    public ProductoPrecioDTO save(ProductoPrecioDTO productoPrecioDTO) {
        LOG.debug("Request to save ProductoPrecio : {}", productoPrecioDTO);
        ProductoPrecio productoPrecio = productoPrecioMapper.toEntity(productoPrecioDTO);
        productoPrecio = productoPrecioRepository.save(productoPrecio);
        return productoPrecioMapper.toDto(productoPrecio);
    }

    @Override
    public ProductoPrecioDTO update(ProductoPrecioDTO productoPrecioDTO) {
        LOG.debug("Request to update ProductoPrecio : {}", productoPrecioDTO);
        ProductoPrecio productoPrecio = productoPrecioMapper.toEntity(productoPrecioDTO);
        productoPrecio = productoPrecioRepository.save(productoPrecio);
        return productoPrecioMapper.toDto(productoPrecio);
    }

    @Override
    public Optional<ProductoPrecioDTO> partialUpdate(ProductoPrecioDTO productoPrecioDTO) {
        LOG.debug("Request to partially update ProductoPrecio : {}", productoPrecioDTO);

        return productoPrecioRepository
            .findById(productoPrecioDTO.getId())
            .map(existingProductoPrecio -> {
                productoPrecioMapper.partialUpdate(existingProductoPrecio, productoPrecioDTO);

                return existingProductoPrecio;
            })
            .map(productoPrecioRepository::save)
            .map(productoPrecioMapper::toDto);
    }

    @Override
    public List<ProductoPrecioDTO> findAll() {
        LOG.debug("Request to get all ProductoPrecios");
        return productoPrecioRepository
            .findAll()
            .stream()
            .map(productoPrecioMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get all the productoPrecios where Producto is {@code null}.
     *  @return the list of entities.
     */

    public List<ProductoPrecioDTO> findAllWhereProductoIsNull() {
        LOG.debug("Request to get all productoPrecios where Producto is null");
        return StreamSupport.stream(productoPrecioRepository.findAll().spliterator(), false)
            .filter(productoPrecio -> productoPrecio.getProducto() == null)
            .map(productoPrecioMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Optional<ProductoPrecioDTO> findOne(String id) {
        LOG.debug("Request to get ProductoPrecio : {}", id);
        return productoPrecioRepository.findById(id).map(productoPrecioMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete ProductoPrecio : {}", id);
        productoPrecioRepository.deleteById(id);
    }
}
