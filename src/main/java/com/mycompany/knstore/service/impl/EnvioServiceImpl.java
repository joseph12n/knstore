package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Envio;
import com.mycompany.knstore.repository.EnvioRepository;
import com.mycompany.knstore.service.EnvioService;
import com.mycompany.knstore.service.dto.EnvioDTO;
import com.mycompany.knstore.service.mapper.EnvioMapper;
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
 * Service Implementation for managing {@link com.mycompany.knstore.domain.Envio}.
 */
@Service
public class EnvioServiceImpl implements EnvioService {

    private static final Logger LOG = LoggerFactory.getLogger(EnvioServiceImpl.class);

    private final EnvioRepository envioRepository;

    private final EnvioMapper envioMapper;

    public EnvioServiceImpl(EnvioRepository envioRepository, EnvioMapper envioMapper) {
        this.envioRepository = envioRepository;
        this.envioMapper = envioMapper;
    }

    @Override
    public EnvioDTO save(EnvioDTO envioDTO) {
        LOG.debug("Request to save Envio : {}", envioDTO);
        Envio envio = envioMapper.toEntity(envioDTO);
        envio = envioRepository.save(envio);
        return envioMapper.toDto(envio);
    }

    @Override
    public EnvioDTO update(EnvioDTO envioDTO) {
        LOG.debug("Request to update Envio : {}", envioDTO);
        Envio envio = envioMapper.toEntity(envioDTO);
        envio = envioRepository.save(envio);
        return envioMapper.toDto(envio);
    }

    @Override
    public Optional<EnvioDTO> partialUpdate(EnvioDTO envioDTO) {
        LOG.debug("Request to partially update Envio : {}", envioDTO);

        return envioRepository
            .findById(envioDTO.getId())
            .map(existingEnvio -> {
                envioMapper.partialUpdate(existingEnvio, envioDTO);

                return existingEnvio;
            })
            .map(envioRepository::save)
            .map(envioMapper::toDto);
    }

    @Override
    public Page<EnvioDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Envios");
        return envioRepository.findAll(pageable).map(envioMapper::toDto);
    }

    /**
     *  Get all the envios where Pedido is {@code null}.
     *  @return the list of entities.
     */

    public List<EnvioDTO> findAllWherePedidoIsNull() {
        LOG.debug("Request to get all envios where Pedido is null");
        return StreamSupport.stream(envioRepository.findAll().spliterator(), false)
            .filter(envio -> envio.getPedido() == null)
            .map(envioMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Optional<EnvioDTO> findOne(String id) {
        LOG.debug("Request to get Envio : {}", id);
        return envioRepository.findById(id).map(envioMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Envio : {}", id);
        envioRepository.deleteById(id);
    }
}
