package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Factura;
import com.mycompany.knstore.domain.Pago;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.FacturaRepository;
import com.mycompany.knstore.repository.PagoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.FacturaService;
import com.mycompany.knstore.service.dto.FacturaDTO;
import com.mycompany.knstore.service.mapper.FacturaMapper;
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

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.Factura}.
 */
@Service
public class FacturaServiceImpl implements FacturaService {

    private static final Logger LOG = LoggerFactory.getLogger(FacturaServiceImpl.class);

    private final FacturaRepository facturaRepository;

    private final PagoRepository pagoRepository;

    private final PedidoRepository pedidoRepository;

    private final CuentaRepository cuentaRepository;

    private final FacturaMapper facturaMapper;

    public FacturaServiceImpl(
        FacturaRepository facturaRepository,
        PagoRepository pagoRepository,
        PedidoRepository pedidoRepository,
        CuentaRepository cuentaRepository,
        FacturaMapper facturaMapper
    ) {
        this.facturaRepository = facturaRepository;
        this.pagoRepository = pagoRepository;
        this.pedidoRepository = pedidoRepository;
        this.cuentaRepository = cuentaRepository;
        this.facturaMapper = facturaMapper;
    }

    @Override
    public FacturaDTO save(FacturaDTO facturaDTO) {
        LOG.debug("Request to save Factura : {}", facturaDTO);
        Factura factura = facturaMapper.toEntity(facturaDTO);
        factura = facturaRepository.save(factura);
        return facturaMapper.toDto(factura);
    }

    @Override
    public FacturaDTO update(FacturaDTO facturaDTO) {
        LOG.debug("Request to update Factura : {}", facturaDTO);
        Factura factura = facturaMapper.toEntity(facturaDTO);
        factura = facturaRepository.save(factura);
        return facturaMapper.toDto(factura);
    }

    @Override
    public Optional<FacturaDTO> partialUpdate(FacturaDTO facturaDTO) {
        LOG.debug("Request to partially update Factura : {}", facturaDTO);

        return facturaRepository
            .findById(facturaDTO.getId())
            .map(existingFactura -> {
                facturaMapper.partialUpdate(existingFactura, facturaDTO);

                return existingFactura;
            })
            .map(facturaRepository::save)
            .map(facturaMapper::toDto);
    }

    @Override
    public Page<FacturaDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Facturas");
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            // RNF-028: pedidos de la cuenta, pagos en lote y facturas en lote:
            // 3 consultas constantes en lugar del recorrido anidado N^2.
            return getCurrentAccountId()
                .map(cuentaId -> {
                    List<String> pedidoIds = pedidoRepository
                        .findByCuentaId(cuentaId, Pageable.unpaged())
                        .getContent()
                        .stream()
                        .map(Pedido::getId)
                        .toList();
                    List<String> pagoIds = pagoRepository
                        .findByPedidoIdIn(MongoIdUtils.toObjectIds(pedidoIds))
                        .stream()
                        .map(Pago::getId)
                        .toList();
                    return facturaRepository
                        .findByPagoIdIn(MongoIdUtils.toObjectIds(pagoIds), withSort(pageable))
                        .map(facturaMapper::toDto);
                })
                .orElse(Page.empty(pageable));
        }
        return facturaRepository.findAll(pageable).map(facturaMapper::toDto);
    }

    @Override
    public Optional<FacturaDTO> findOne(String id) {
        LOG.debug("Request to get Factura : {}", id);
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            // RNF-028: la factura se resuelve por id y el ownership con pago y
            // pedido por id (consultas constantes, sin recorrer listas).
            return getCurrentAccountId()
                .flatMap(cuentaId ->
                    facturaRepository
                        .findById(id)
                        .filter(factura -> factura.getPago() != null && factura.getPago().getId() != null)
                        .filter(factura -> {
                            Pedido pedido = pagoRepository.findById(factura.getPago().getId()).map(Pago::getPedido).orElse(null);
                            return pedidoPerteneceACuenta(pedido, cuentaId);
                        })
                )
                .map(facturaMapper::toDto);
        }
        return facturaRepository.findById(id).map(facturaMapper::toDto);
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
        LOG.debug("Request to delete Factura : {}", id);
        facturaRepository.deleteById(id);
    }

    private Optional<String> getCurrentAccountId() {
        return SecurityUtils.getCurrentUserId()
            .flatMap(cuentaRepository::findOneByUserId)
            .map(cuenta -> cuenta.getId());
    }
}
