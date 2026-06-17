package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.ProductoImagen;
import com.mycompany.knstore.repository.ProductoImagenRepository;
import com.mycompany.knstore.service.ProductoImagenService;
import com.mycompany.knstore.service.dto.ProductoImagenDTO;
import com.mycompany.knstore.service.mapper.ProductoImagenMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.ProductoImagen}.
 */
@Service
public class ProductoImagenServiceImpl implements ProductoImagenService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductoImagenServiceImpl.class);

    private final ProductoImagenRepository productoImagenRepository;

    private final ProductoImagenMapper productoImagenMapper;

    public ProductoImagenServiceImpl(ProductoImagenRepository productoImagenRepository, ProductoImagenMapper productoImagenMapper) {
        this.productoImagenRepository = productoImagenRepository;
        this.productoImagenMapper = productoImagenMapper;
    }

    @Override
    public ProductoImagenDTO save(ProductoImagenDTO productoImagenDTO) {
        LOG.debug("Request to save ProductoImagen : {}", productoImagenDTO);
        ProductoImagen productoImagen = productoImagenMapper.toEntity(productoImagenDTO);
        productoImagen = productoImagenRepository.save(productoImagen);
        return productoImagenMapper.toDto(productoImagen);
    }

    @Override
    public ProductoImagenDTO update(ProductoImagenDTO productoImagenDTO) {
        LOG.debug("Request to update ProductoImagen : {}", productoImagenDTO);
        ProductoImagen productoImagen = productoImagenMapper.toEntity(productoImagenDTO);
        productoImagen = productoImagenRepository.save(productoImagen);
        return productoImagenMapper.toDto(productoImagen);
    }

    @Override
    public Optional<ProductoImagenDTO> partialUpdate(ProductoImagenDTO productoImagenDTO) {
        LOG.debug("Request to partially update ProductoImagen : {}", productoImagenDTO);

        return productoImagenRepository
            .findById(productoImagenDTO.getId())
            .map(existingProductoImagen -> {
                productoImagenMapper.partialUpdate(existingProductoImagen, productoImagenDTO);

                return existingProductoImagen;
            })
            .map(productoImagenRepository::save)
            .map(productoImagenMapper::toDto);
    }

    @Override
    public List<ProductoImagenDTO> findAll() {
        LOG.debug("Request to get all ProductoImagens");
        return productoImagenRepository
            .findAll()
            .stream()
            .map(productoImagenMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Optional<ProductoImagenDTO> findOne(String id) {
        LOG.debug("Request to get ProductoImagen : {}", id);
        return productoImagenRepository.findById(id).map(productoImagenMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete ProductoImagen : {}", id);
        productoImagenRepository.deleteById(id);
    }
}
