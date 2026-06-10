package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.VarianteProducto;
import com.mycompany.knstore.repository.VarianteProductoRepository;
import com.mycompany.knstore.service.VarianteProductoService;
import com.mycompany.knstore.service.dto.VarianteProductoDTO;
import com.mycompany.knstore.service.mapper.VarianteProductoMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.VarianteProducto}.
 */
@Service
public class VarianteProductoServiceImpl implements VarianteProductoService {

    private static final Logger LOG = LoggerFactory.getLogger(VarianteProductoServiceImpl.class);

    private final VarianteProductoRepository varianteProductoRepository;

    private final VarianteProductoMapper varianteProductoMapper;

    public VarianteProductoServiceImpl(
        VarianteProductoRepository varianteProductoRepository,
        VarianteProductoMapper varianteProductoMapper
    ) {
        this.varianteProductoRepository = varianteProductoRepository;
        this.varianteProductoMapper = varianteProductoMapper;
    }

    @Override
    public VarianteProductoDTO save(VarianteProductoDTO varianteProductoDTO) {
        LOG.debug("Request to save VarianteProducto : {}", varianteProductoDTO);
        VarianteProducto varianteProducto = varianteProductoMapper.toEntity(varianteProductoDTO);
        varianteProducto = varianteProductoRepository.save(varianteProducto);
        return varianteProductoMapper.toDto(varianteProducto);
    }

    @Override
    public VarianteProductoDTO update(VarianteProductoDTO varianteProductoDTO) {
        LOG.debug("Request to update VarianteProducto : {}", varianteProductoDTO);
        VarianteProducto varianteProducto = varianteProductoMapper.toEntity(varianteProductoDTO);
        varianteProducto = varianteProductoRepository.save(varianteProducto);
        return varianteProductoMapper.toDto(varianteProducto);
    }

    @Override
    public Optional<VarianteProductoDTO> partialUpdate(VarianteProductoDTO varianteProductoDTO) {
        LOG.debug("Request to partially update VarianteProducto : {}", varianteProductoDTO);

        return varianteProductoRepository
            .findById(varianteProductoDTO.getId())
            .map(existingVarianteProducto -> {
                varianteProductoMapper.partialUpdate(existingVarianteProducto, varianteProductoDTO);

                return existingVarianteProducto;
            })
            .map(varianteProductoRepository::save)
            .map(varianteProductoMapper::toDto);
    }

    @Override
    public Page<VarianteProductoDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all VarianteProductos");
        return varianteProductoRepository.findAll(pageable).map(varianteProductoMapper::toDto);
    }

    public Page<VarianteProductoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return varianteProductoRepository.findAllWithEagerRelationships(pageable).map(varianteProductoMapper::toDto);
    }

    @Override
    public Optional<VarianteProductoDTO> findOne(String id) {
        LOG.debug("Request to get VarianteProducto : {}", id);
        return varianteProductoRepository.findOneWithEagerRelationships(id).map(varianteProductoMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete VarianteProducto : {}", id);
        varianteProductoRepository.deleteById(id);
    }
}
