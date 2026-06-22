package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Envio;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.EnvioRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.EnvioService;
import com.mycompany.knstore.service.dto.EnvioDTO;
import com.mycompany.knstore.service.mapper.EnvioMapper;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.Envio}.
 */
@Service
public class EnvioServiceImpl implements EnvioService {

    private static final Logger LOG = LoggerFactory.getLogger(EnvioServiceImpl.class);

    private final EnvioRepository envioRepository;

    private final PedidoRepository pedidoRepository;

    private final CuentaRepository cuentaRepository;

    private final EnvioMapper envioMapper;

    public EnvioServiceImpl(
        EnvioRepository envioRepository,
        PedidoRepository pedidoRepository,
        CuentaRepository cuentaRepository,
        EnvioMapper envioMapper
    ) {
        this.envioRepository = envioRepository;
        this.pedidoRepository = pedidoRepository;
        this.cuentaRepository = cuentaRepository;
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
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return getCurrentAccountId()
                .map(cuentaId -> {
                    LinkedList<EnvioDTO> envios = pedidoRepository
                        .findByCuentaId(cuentaId, Pageable.unpaged())
                        .getContent()
                        .stream()
                        .flatMap(pedido -> envioRepository.findByPedidoId(pedido.getId(), Pageable.unpaged()).getContent().stream())
                        .map(envioMapper::toDto)
                        .collect(Collectors.toCollection(LinkedList::new));
                    Page<EnvioDTO> page = new PageImpl<>(envios, pageable, envios.size());
                    return page;
                })
                .orElse(Page.empty(pageable));
        }
        return envioRepository.findAll(pageable).map(envioMapper::toDto);
    }

    @Override
    public Optional<EnvioDTO> findOne(String id) {
        LOG.debug("Request to get Envio : {}", id);
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return getCurrentAccountId()
                .flatMap(cuentaId ->
                    pedidoRepository
                        .findByCuentaId(cuentaId, Pageable.unpaged())
                        .getContent()
                        .stream()
                        .map(pedido -> envioRepository.findByIdAndPedidoId(id, pedido.getId()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst()
                )
                .map(envioMapper::toDto);
        }
        return envioRepository.findById(id).map(envioMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Envio : {}", id);
        envioRepository.deleteById(id);
    }

    private Optional<String> getCurrentAccountId() {
        return SecurityUtils.getCurrentUserId()
            .flatMap(cuentaRepository::findOneByUserId)
            .map(cuenta -> cuenta.getId());
    }
}
