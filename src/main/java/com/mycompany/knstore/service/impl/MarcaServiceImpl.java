package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Marca;
import com.mycompany.knstore.repository.MarcaRepository;
import com.mycompany.knstore.service.MarcaService;
import com.mycompany.knstore.service.dto.MarcaDTO;
import com.mycompany.knstore.service.mapper.MarcaMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.Marca}.
 */
@Service
public class MarcaServiceImpl implements MarcaService {

    private static final Logger LOG = LoggerFactory.getLogger(MarcaServiceImpl.class);

    private final MarcaRepository marcaRepository;

    private final MarcaMapper marcaMapper;

    public MarcaServiceImpl(MarcaRepository marcaRepository, MarcaMapper marcaMapper) {
        this.marcaRepository = marcaRepository;
        this.marcaMapper = marcaMapper;
    }

    @Override
    public MarcaDTO save(MarcaDTO marcaDTO) {
        LOG.debug("Request to save Marca : {}", marcaDTO);
        Marca marca = marcaMapper.toEntity(marcaDTO);
        marca = marcaRepository.save(marca);
        return marcaMapper.toDto(marca);
    }

    @Override
    public MarcaDTO update(MarcaDTO marcaDTO) {
        LOG.debug("Request to update Marca : {}", marcaDTO);
        Marca marca = marcaMapper.toEntity(marcaDTO);
        marca = marcaRepository.save(marca);
        return marcaMapper.toDto(marca);
    }

    @Override
    public Optional<MarcaDTO> partialUpdate(MarcaDTO marcaDTO) {
        LOG.debug("Request to partially update Marca : {}", marcaDTO);

        return marcaRepository
            .findById(marcaDTO.getId())
            .map(existingMarca -> {
                marcaMapper.partialUpdate(existingMarca, marcaDTO);

                return existingMarca;
            })
            .map(marcaRepository::save)
            .map(marcaMapper::toDto);
    }

    @Override
    public List<MarcaDTO> findAll() {
        LOG.debug("Request to get all Marcas");
        return marcaRepository.findAll().stream().map(marcaMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Optional<MarcaDTO> findOne(String id) {
        LOG.debug("Request to get Marca : {}", id);
        return marcaRepository.findById(id).map(marcaMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Marca : {}", id);
        marcaRepository.deleteById(id);
    }
}
