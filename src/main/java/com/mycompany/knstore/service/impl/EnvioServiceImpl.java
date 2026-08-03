package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Envio;
import com.mycompany.knstore.domain.enumeration.EstadoEnvio;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.EnvioRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.EnvioService;
import com.mycompany.knstore.service.HistorialEstadoService;
import com.mycompany.knstore.service.dto.EnvioDTO;
import com.mycompany.knstore.service.mapper.EnvioMapper;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
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

    private final HistorialEstadoService historialEstadoService;

    public EnvioServiceImpl(
        EnvioRepository envioRepository,
        PedidoRepository pedidoRepository,
        CuentaRepository cuentaRepository,
        EnvioMapper envioMapper,
        HistorialEstadoService historialEstadoService
    ) {
        this.envioRepository = envioRepository;
        this.pedidoRepository = pedidoRepository;
        this.cuentaRepository = cuentaRepository;
        this.envioMapper = envioMapper;
        this.historialEstadoService = historialEstadoService;
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

    public Page<EnvioDTO> findPendientesAdmin(Pageable pageable) {
        LOG.debug("Request to get pending Envios for admin");
        return envioRepository
            .findByEstadoIn(List.of(EstadoEnvio.PENDING, EstadoEnvio.DISPATCHED, EstadoEnvio.IN_TRANSIT, EstadoEnvio.IN_CITY), pageable)
            .map(envioMapper::toDto);
    }

    public Optional<EnvioDTO> asignarNumeroRastreo(String envioId, String numeroRastreo, String transportadora, String urlRastreo) {
        LOG.debug("Request to assign tracking number {} to Envio {}", numeroRastreo, envioId);
        String tracking = numeroRastreo != null ? numeroRastreo.trim() : null;
        if (tracking == null || tracking.isBlank()) {
            throw new IllegalArgumentException("El numero de rastreo es obligatorio");
        }

        Optional<Envio> duplicado = envioRepository.findByNumeroRastreo(tracking).filter(envio -> !envioId.equals(envio.getId()));
        if (duplicado.isPresent()) {
            throw new IllegalArgumentException("El numero de rastreo ya esta asignado a otro envio");
        }

        return envioRepository
            .findById(envioId)
            .map(envio -> {
                String estadoAnterior = envio.getEstado() != null ? envio.getEstado().name() : null;
                String trackingAnterior = envio.getNumeroRastreo();

                envio.setNumeroRastreo(tracking);
                if (transportadora != null && !transportadora.isBlank()) {
                    envio.setTransportadora(transportadora.trim());
                }
                if (urlRastreo != null && !urlRastreo.isBlank()) {
                    envio.setUrlRastreo(urlRastreo.trim());
                }

                if (EstadoEnvio.PENDING.equals(envio.getEstado())) {
                    envio.setEstado(EstadoEnvio.DISPATCHED);
                    if (envio.getFechaDespacho() == null) {
                        envio.setFechaDespacho(Instant.now());
                    }
                }

                Envio guardado = envioRepository.save(envio);

                historialEstadoService.registrarCambioEstado("Envio", envioId, "numeroRastreo", trackingAnterior, tracking);
                if (!estadoAnterior.equals(guardado.getEstado().name())) {
                    historialEstadoService.registrarCambioEstado("Envio", envioId, "estado", estadoAnterior, guardado.getEstado().name());
                }
                return guardado;
            })
            .map(envioMapper::toDto);
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
