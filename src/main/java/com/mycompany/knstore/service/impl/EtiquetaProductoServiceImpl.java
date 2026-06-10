package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.EtiquetaProducto;
import com.mycompany.knstore.repository.EtiquetaProductoRepository;
import com.mycompany.knstore.service.EtiquetaProductoService;
import com.mycompany.knstore.service.dto.EtiquetaProductoDTO;
import com.mycompany.knstore.service.mapper.EtiquetaProductoMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.EtiquetaProducto}.
 */
@Service
public class EtiquetaProductoServiceImpl implements EtiquetaProductoService {

    private static final Logger LOG = LoggerFactory.getLogger(EtiquetaProductoServiceImpl.class);

    private final EtiquetaProductoRepository etiquetaProductoRepository;

    private final EtiquetaProductoMapper etiquetaProductoMapper;

    public EtiquetaProductoServiceImpl(
        EtiquetaProductoRepository etiquetaProductoRepository,
        EtiquetaProductoMapper etiquetaProductoMapper
    ) {
        this.etiquetaProductoRepository = etiquetaProductoRepository;
        this.etiquetaProductoMapper = etiquetaProductoMapper;
    }

    @Override
    public EtiquetaProductoDTO save(EtiquetaProductoDTO etiquetaProductoDTO) {
        LOG.debug("Request to save EtiquetaProducto : {}", etiquetaProductoDTO);
        EtiquetaProducto etiquetaProducto = etiquetaProductoMapper.toEntity(etiquetaProductoDTO);
        etiquetaProducto = etiquetaProductoRepository.save(etiquetaProducto);
        return etiquetaProductoMapper.toDto(etiquetaProducto);
    }

    @Override
    public EtiquetaProductoDTO update(EtiquetaProductoDTO etiquetaProductoDTO) {
        LOG.debug("Request to update EtiquetaProducto : {}", etiquetaProductoDTO);
        EtiquetaProducto etiquetaProducto = etiquetaProductoMapper.toEntity(etiquetaProductoDTO);
        etiquetaProducto = etiquetaProductoRepository.save(etiquetaProducto);
        return etiquetaProductoMapper.toDto(etiquetaProducto);
    }

    @Override
    public Optional<EtiquetaProductoDTO> partialUpdate(EtiquetaProductoDTO etiquetaProductoDTO) {
        LOG.debug("Request to partially update EtiquetaProducto : {}", etiquetaProductoDTO);

        return etiquetaProductoRepository
            .findById(etiquetaProductoDTO.getId())
            .map(existingEtiquetaProducto -> {
                etiquetaProductoMapper.partialUpdate(existingEtiquetaProducto, etiquetaProductoDTO);

                return existingEtiquetaProducto;
            })
            .map(etiquetaProductoRepository::save)
            .map(etiquetaProductoMapper::toDto);
    }

    @Override
    public Page<EtiquetaProductoDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all EtiquetaProductos");
        return etiquetaProductoRepository.findAll(pageable).map(etiquetaProductoMapper::toDto);
    }

    public Page<EtiquetaProductoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return etiquetaProductoRepository.findAllWithEagerRelationships(pageable).map(etiquetaProductoMapper::toDto);
    }

    @Override
    public Optional<EtiquetaProductoDTO> findOne(String id) {
        LOG.debug("Request to get EtiquetaProducto : {}", id);
        return etiquetaProductoRepository.findOneWithEagerRelationships(id).map(etiquetaProductoMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete EtiquetaProducto : {}", id);
        etiquetaProductoRepository.deleteById(id);
    }
}
