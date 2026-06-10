package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.TipoDocumento;
import com.mycompany.knstore.repository.TipoDocumentoRepository;
import com.mycompany.knstore.service.TipoDocumentoService;
import com.mycompany.knstore.service.dto.TipoDocumentoDTO;
import com.mycompany.knstore.service.mapper.TipoDocumentoMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.TipoDocumento}.
 */
@Service
public class TipoDocumentoServiceImpl implements TipoDocumentoService {

    private static final Logger LOG = LoggerFactory.getLogger(TipoDocumentoServiceImpl.class);

    private final TipoDocumentoRepository tipoDocumentoRepository;

    private final TipoDocumentoMapper tipoDocumentoMapper;

    public TipoDocumentoServiceImpl(TipoDocumentoRepository tipoDocumentoRepository, TipoDocumentoMapper tipoDocumentoMapper) {
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.tipoDocumentoMapper = tipoDocumentoMapper;
    }

    @Override
    public TipoDocumentoDTO save(TipoDocumentoDTO tipoDocumentoDTO) {
        LOG.debug("Request to save TipoDocumento : {}", tipoDocumentoDTO);
        TipoDocumento tipoDocumento = tipoDocumentoMapper.toEntity(tipoDocumentoDTO);
        tipoDocumento = tipoDocumentoRepository.save(tipoDocumento);
        return tipoDocumentoMapper.toDto(tipoDocumento);
    }

    @Override
    public TipoDocumentoDTO update(TipoDocumentoDTO tipoDocumentoDTO) {
        LOG.debug("Request to update TipoDocumento : {}", tipoDocumentoDTO);
        TipoDocumento tipoDocumento = tipoDocumentoMapper.toEntity(tipoDocumentoDTO);
        tipoDocumento = tipoDocumentoRepository.save(tipoDocumento);
        return tipoDocumentoMapper.toDto(tipoDocumento);
    }

    @Override
    public Optional<TipoDocumentoDTO> partialUpdate(TipoDocumentoDTO tipoDocumentoDTO) {
        LOG.debug("Request to partially update TipoDocumento : {}", tipoDocumentoDTO);

        return tipoDocumentoRepository
            .findById(tipoDocumentoDTO.getId())
            .map(existingTipoDocumento -> {
                tipoDocumentoMapper.partialUpdate(existingTipoDocumento, tipoDocumentoDTO);

                return existingTipoDocumento;
            })
            .map(tipoDocumentoRepository::save)
            .map(tipoDocumentoMapper::toDto);
    }

    @Override
    public List<TipoDocumentoDTO> findAll() {
        LOG.debug("Request to get all TipoDocumentos");
        return tipoDocumentoRepository.findAll().stream().map(tipoDocumentoMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Optional<TipoDocumentoDTO> findOne(String id) {
        LOG.debug("Request to get TipoDocumento : {}", id);
        return tipoDocumentoRepository.findById(id).map(tipoDocumentoMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete TipoDocumento : {}", id);
        tipoDocumentoRepository.deleteById(id);
    }
}
