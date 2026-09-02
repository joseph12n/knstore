package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Envio;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.enumeration.EstadoEnvio;
import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.EnvioRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.EnvioService;
import com.mycompany.knstore.service.HistorialEstadoService;
import com.mycompany.knstore.service.dto.EnvioDTO;
import com.mycompany.knstore.service.mapper.EnvioMapper;
import com.mycompany.knstore.service.util.MongoIdUtils;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            // RNF-028: una consulta para los pedidos de la cuenta y una consulta
            // en lote para sus envios (sin N+1 ni paginacion en memoria).
            return getCurrentAccountId()
                .map(cuentaId -> {
                    List<String> pedidoIds = pedidoRepository
                        .findByCuentaId(cuentaId, Pageable.unpaged())
                        .getContent()
                        .stream()
                        .map(Pedido::getId)
                        .toList();
                    return envioRepository
                        .findByPedidoIdIn(MongoIdUtils.toObjectIds(pedidoIds), withSort(pageable))
                        .map(envioMapper::toDto);
                })
                .orElse(Page.empty(pageable));
        }
        return envioRepository.findAll(pageable).map(envioMapper::toDto);
    }

    @Override
    public Optional<EnvioDTO> findOne(String id) {
        LOG.debug("Request to get Envio : {}", id);
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            // RNF-028: el envio se resuelve por id y el ownership con una sola
            // consulta del pedido (2 consultas constantes, sin recorrer listas).
            return getCurrentAccountId()
                .flatMap(cuentaId -> envioRepository.findById(id).filter(envio -> pedidoPerteneceACuenta(envio.getPedido(), cuentaId)))
                .map(envioMapper::toDto);
        }
        return envioRepository.findById(id).map(envioMapper::toDto);
    }

    private boolean pedidoPerteneceACuenta(Pedido pedido, String cuentaId) {
        if (pedido == null || pedido.getId() == null) {
            return false;
        }
        return pedidoRepository.findByIdAndCuentaId(pedido.getId(), cuentaId).isPresent();
    }

    /**
     * Si el {@link Pageable} no trae orden, se aplica un sort determinista por id
     * descendente para que la paginacion en lote sea estable.
     */
    private Pageable withSort(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));
        }
        return pageable;
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Envio : {}", id);
        envioRepository.deleteById(id);
    }

    @Override
    @Transactional
    public EnvioDTO asignarTracking(String id, String transportadora, String numeroRastreo) {
        LOG.debug("Request to assign tracking to Envio : {}", id);
        Envio envio = envioRepository.findById(id).orElseThrow(() -> new IllegalStateException("Envio no encontrado"));
        envio.setTransportadora(transportadora);
        envio.setNumeroRastreo(numeroRastreo);
        envio = envioRepository.save(envio);
        historialEstadoService.registrar("ENVIO", envio.getId(), "tracking", null, transportadora + " / " + numeroRastreo);
        return envioMapper.toDto(envio);
    }

    @Override
    @Transactional
    public EnvioDTO cambiarEstado(String id, EstadoEnvio nuevoEstado) {
        LOG.debug("Request to change estado of Envio : {} -> {}", id, nuevoEstado);
        Envio envio = envioRepository.findById(id).orElseThrow(() -> new IllegalStateException("Envio no encontrado"));
        EstadoEnvio estadoAnterior = envio.getEstado();
        validarTransicionEnvio(estadoAnterior, nuevoEstado);
        envio.setEstado(nuevoEstado);
        envio = envioRepository.save(envio);
        historialEstadoService.registrar("ENVIO", envio.getId(), "estado", estadoAnterior.name(), nuevoEstado.name());
        return envioMapper.toDto(envio);
    }

    @Override
    @Transactional
    public EnvioDTO marcarDevolucion(String id) {
        LOG.debug("Request to mark Envio as devuelto : {}", id);
        Envio envio = envioRepository.findById(id).orElseThrow(() -> new IllegalStateException("Envio no encontrado"));
        EstadoEnvio estadoAnterior = envio.getEstado();
        if (EstadoEnvio.RETURNED.equals(estadoAnterior) || EstadoEnvio.LOST.equals(estadoAnterior)) {
            throw new IllegalStateException("El envio ya se encuentra en estado " + estadoAnterior.name());
        }
        envio.setEstado(EstadoEnvio.RETURNED);
        envio = envioRepository.save(envio);
        historialEstadoService.registrar(
            "ENVIO",
            envio.getId(),
            "estado",
            estadoAnterior != null ? estadoAnterior.name() : null,
            EstadoEnvio.RETURNED.name()
        );

        if (envio.getPedido() != null) {
            Pedido pedido = envio.getPedido();
            if (pedido.getEstado() != null && !EstadoPedido.RETURNED.equals(pedido.getEstado())) {
                EstadoPedido estadoPedidoAnterior = pedido.getEstado();
                pedido.setEstado(EstadoPedido.RETURNED);
                pedidoRepository.save(pedido);
                historialEstadoService.registrar(
                    "PEDIDO",
                    pedido.getId(),
                    "estado",
                    estadoPedidoAnterior.name(),
                    EstadoPedido.RETURNED.name()
                );
            }
        }
        return envioMapper.toDto(envio);
    }

    @Override
    public Page<EnvioDTO> findAllPendientes(Pageable pageable) {
        LOG.debug("Request to get pending Envios");
        return envioRepository.findByEstado(EstadoEnvio.PENDING, pageable).map(envioMapper::toDto);
    }

    private void validarTransicionEnvio(EstadoEnvio anterior, EstadoEnvio nuevo) {
        if (anterior == null || nuevo == null) {
            throw new IllegalStateException("El estado del envio es obligatorio");
        }
        if (anterior.equals(nuevo)) {
            return;
        }
        if (EstadoEnvio.RETURNED.equals(nuevo)) {
            return;
        }
        if (EstadoEnvio.LOST.equals(anterior) || EstadoEnvio.DELIVERED.equals(anterior) || EstadoEnvio.RETURNED.equals(anterior)) {
            throw new IllegalStateException("Transicion invalida de " + anterior.name() + " a " + nuevo.name());
        }
        if (ordenEnvio(anterior) > ordenEnvio(nuevo)) {
            throw new IllegalStateException("Transicion invalida de " + anterior.name() + " a " + nuevo.name());
        }
    }

    private int ordenEnvio(EstadoEnvio estado) {
        return switch (estado) {
            case PENDING -> 0;
            case DISPATCHED -> 1;
            case IN_TRANSIT -> 2;
            case IN_CITY -> 3;
            case DELIVERED -> 4;
            default -> 5;
        };
    }

    private Optional<String> getCurrentAccountId() {
        return SecurityUtils.getCurrentUserId()
            .flatMap(cuentaRepository::findOneByUserId)
            .map(cuenta -> cuenta.getId());
    }
}
