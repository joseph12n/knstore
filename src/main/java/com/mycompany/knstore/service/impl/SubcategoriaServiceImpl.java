package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Subcategoria;
import com.mycompany.knstore.repository.SubcategoriaRepository;
import com.mycompany.knstore.service.SubcategoriaService;
import com.mycompany.knstore.service.dto.SubcategoriaDTO;
import com.mycompany.knstore.service.mapper.SubcategoriaMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.Subcategoria}.
 */
@Service
public class SubcategoriaServiceImpl implements SubcategoriaService {

    private static final Logger LOG = LoggerFactory.getLogger(SubcategoriaServiceImpl.class);

    private final SubcategoriaRepository subcategoriaRepository;

    private final SubcategoriaMapper subcategoriaMapper;

    public SubcategoriaServiceImpl(SubcategoriaRepository subcategoriaRepository, SubcategoriaMapper subcategoriaMapper) {
        this.subcategoriaRepository = subcategoriaRepository;
        this.subcategoriaMapper = subcategoriaMapper;
    }

    @Override
    public SubcategoriaDTO save(SubcategoriaDTO subcategoriaDTO) {
        LOG.debug("Request to save Subcategoria : {}", subcategoriaDTO);
        Subcategoria subcategoria = subcategoriaMapper.toEntity(subcategoriaDTO);
        subcategoria = subcategoriaRepository.save(subcategoria);
        return subcategoriaMapper.toDto(subcategoria);
    }

    @Override
    public SubcategoriaDTO update(SubcategoriaDTO subcategoriaDTO) {
        LOG.debug("Request to update Subcategoria : {}", subcategoriaDTO);
        Subcategoria subcategoria = subcategoriaMapper.toEntity(subcategoriaDTO);
        subcategoria = subcategoriaRepository.save(subcategoria);
        return subcategoriaMapper.toDto(subcategoria);
    }

    @Override
    public Optional<SubcategoriaDTO> partialUpdate(SubcategoriaDTO subcategoriaDTO) {
        LOG.debug("Request to partially update Subcategoria : {}", subcategoriaDTO);

        return subcategoriaRepository
            .findById(subcategoriaDTO.getId())
            .map(existingSubcategoria -> {
                subcategoriaMapper.partialUpdate(existingSubcategoria, subcategoriaDTO);

                return existingSubcategoria;
            })
            .map(subcategoriaRepository::save)
            .map(subcategoriaMapper::toDto);
    }

    @Override
    public Page<SubcategoriaDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Subcategorias");
        return subcategoriaRepository.findAll(pageable).map(subcategoriaMapper::toDto);
    }

    public Page<SubcategoriaDTO> findAllWithEagerRelationships(Pageable pageable) {
        return subcategoriaRepository.findAllWithEagerRelationships(pageable).map(subcategoriaMapper::toDto);
    }

    @Override
    public Optional<SubcategoriaDTO> findOne(String id) {
        LOG.debug("Request to get Subcategoria : {}", id);
        return subcategoriaRepository.findOneWithEagerRelationships(id).map(subcategoriaMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Subcategoria : {}", id);
        subcategoriaRepository.deleteById(id);
    }
}
