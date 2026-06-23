package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.CategoriaIVA;
import com.mycompany.knstore.repository.CategoriaIVARepository;
import com.mycompany.knstore.service.CategoriaIVAService;
import com.mycompany.knstore.service.dto.CategoriaIVADTO;
import com.mycompany.knstore.service.mapper.CategoriaIVAMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.CategoriaIVA}.
 */
@Service
public class CategoriaIVAServiceImpl implements CategoriaIVAService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoriaIVAServiceImpl.class);

    private final CategoriaIVARepository categoriaIVARepository;

    private final CategoriaIVAMapper categoriaIVAMapper;

    public CategoriaIVAServiceImpl(CategoriaIVARepository categoriaIVARepository, CategoriaIVAMapper categoriaIVAMapper) {
        this.categoriaIVARepository = categoriaIVARepository;
        this.categoriaIVAMapper = categoriaIVAMapper;
    }

    @Override
    public CategoriaIVADTO save(CategoriaIVADTO categoriaIVADTO) {
        LOG.debug("Request to save CategoriaIVA : {}", categoriaIVADTO);
        CategoriaIVA categoriaIVA = categoriaIVAMapper.toEntity(categoriaIVADTO);
        categoriaIVA = categoriaIVARepository.save(categoriaIVA);
        return categoriaIVAMapper.toDto(categoriaIVA);
    }

    @Override
    public CategoriaIVADTO update(CategoriaIVADTO categoriaIVADTO) {
        LOG.debug("Request to update CategoriaIVA : {}", categoriaIVADTO);
        CategoriaIVA categoriaIVA = categoriaIVAMapper.toEntity(categoriaIVADTO);
        categoriaIVA = categoriaIVARepository.save(categoriaIVA);
        return categoriaIVAMapper.toDto(categoriaIVA);
    }

    @Override
    public Optional<CategoriaIVADTO> partialUpdate(CategoriaIVADTO categoriaIVADTO) {
        LOG.debug("Request to partially update CategoriaIVA : {}", categoriaIVADTO);

        return categoriaIVARepository
            .findById(categoriaIVADTO.getId())
            .map(existingCategoriaIVA -> {
                categoriaIVAMapper.partialUpdate(existingCategoriaIVA, categoriaIVADTO);

                return existingCategoriaIVA;
            })
            .map(categoriaIVARepository::save)
            .map(categoriaIVAMapper::toDto);
    }

    @Override
    public List<CategoriaIVADTO> findAll() {
        LOG.debug("Request to get all CategoriaIVAS");
        return categoriaIVARepository.findAll().stream().map(categoriaIVAMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Optional<CategoriaIVADTO> findOne(String id) {
        LOG.debug("Request to get CategoriaIVA : {}", id);
        return categoriaIVARepository.findById(id).map(categoriaIVAMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete CategoriaIVA : {}", id);
        categoriaIVARepository.deleteById(id);
    }
}
