package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Direccion;
import com.mycompany.knstore.repository.DireccionRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.DireccionService;
import com.mycompany.knstore.service.dto.DireccionDTO;
import com.mycompany.knstore.service.mapper.DireccionMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.Direccion}.
 */
@Service
public class DireccionServiceImpl implements DireccionService {

    private static final Logger LOG = LoggerFactory.getLogger(DireccionServiceImpl.class);

    private final DireccionRepository direccionRepository;

    private final DireccionMapper direccionMapper;

    public DireccionServiceImpl(DireccionRepository direccionRepository, DireccionMapper direccionMapper) {
        this.direccionRepository = direccionRepository;
        this.direccionMapper = direccionMapper;
    }

    @Override
    public DireccionDTO save(DireccionDTO direccionDTO) {
        LOG.debug("Request to save Direccion : {}", direccionDTO);
        Direccion direccion = direccionMapper.toEntity(direccionDTO);
        direccion = direccionRepository.save(direccion);
        return direccionMapper.toDto(direccion);
    }

    @Override
    public DireccionDTO update(DireccionDTO direccionDTO) {
        LOG.debug("Request to update Direccion : {}", direccionDTO);
        Direccion direccion = direccionMapper.toEntity(direccionDTO);
        direccion = direccionRepository.save(direccion);
        return direccionMapper.toDto(direccion);
    }

    @Override
    public Optional<DireccionDTO> partialUpdate(DireccionDTO direccionDTO) {
        LOG.debug("Request to partially update Direccion : {}", direccionDTO);

        return direccionRepository
            .findById(direccionDTO.getId())
            .map(existingDireccion -> {
                direccionMapper.partialUpdate(existingDireccion, direccionDTO);

                return existingDireccion;
            })
            .map(direccionRepository::save)
            .map(direccionMapper::toDto);
    }

    @Override
    public Page<DireccionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Direccions");
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return SecurityUtils.getCurrentUserLogin()
                .map(login -> direccionRepository.findByCuentaUserLogin(login, pageable).map(direccionMapper::toDto))
                .orElse(Page.empty(pageable));
        }
        return direccionRepository.findAll(pageable).map(direccionMapper::toDto);
    }

    /**
     *  Get all the direccions where Pedido is {@code null}.
     *  @return the list of entities.
     */

    public List<DireccionDTO> findAllWherePedidoIsNull() {
        LOG.debug("Request to get all direccions where Pedido is null");
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return SecurityUtils.getCurrentUserLogin()
                .map(login ->
                    direccionRepository
                        .findByCuentaUserLoginAndPedidoIsNull(login)
                        .stream()
                        .map(direccionMapper::toDto)
                        .collect(Collectors.toCollection(LinkedList::new))
                )
                .orElseGet(LinkedList::new);
        }
        return StreamSupport.stream(direccionRepository.findAll().spliterator(), false)
            .filter(direccion -> direccion.getPedido() == null)
            .map(direccionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Optional<DireccionDTO> findOne(String id) {
        LOG.debug("Request to get Direccion : {}", id);
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return SecurityUtils.getCurrentUserLogin()
                .flatMap(login -> direccionRepository.findByIdAndCuentaUserLogin(id, login))
                .map(direccionMapper::toDto);
        }
        return direccionRepository.findById(id).map(direccionMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Direccion : {}", id);
        direccionRepository.deleteById(id);
    }
}
