package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Factura;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.FacturaRepository;
import com.mycompany.knstore.repository.PagoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.FacturaService;
import com.mycompany.knstore.service.dto.FacturaDTO;
import com.mycompany.knstore.service.mapper.FacturaMapper;
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
            return getCurrentAccountId()
                .map(cuentaId -> {
                    LinkedList<FacturaDTO> facturas = pedidoRepository
                        .findByCuentaId(cuentaId, Pageable.unpaged())
                        .getContent()
                        .stream()
                        .flatMap(pedido ->
                            pagoRepository
                                .findByPedidoId(pedido.getId(), Pageable.unpaged())
                                .getContent()
                                .stream()
                                .flatMap(pago -> facturaRepository.findByPagoId(pago.getId(), Pageable.unpaged()).getContent().stream())
                        )
                        .map(facturaMapper::toDto)
                        .collect(Collectors.toCollection(LinkedList::new));
                    Page<FacturaDTO> page = new PageImpl<>(facturas, pageable, facturas.size());
                    return page;
                })
                .orElse(Page.empty(pageable));
        }
        return facturaRepository.findAll(pageable).map(facturaMapper::toDto);
    }

    @Override
    public Optional<FacturaDTO> findOne(String id) {
        LOG.debug("Request to get Factura : {}", id);
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return getCurrentAccountId()
                .flatMap(cuentaId ->
                    pedidoRepository
                        .findByCuentaId(cuentaId, Pageable.unpaged())
                        .getContent()
                        .stream()
                        .flatMap(pedido ->
                            pagoRepository
                                .findByPedidoId(pedido.getId(), Pageable.unpaged())
                                .getContent()
                                .stream()
                                .map(pago -> facturaRepository.findByIdAndPagoId(id, pago.getId()))
                        )
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst()
                )
                .map(facturaMapper::toDto);
        }
        return facturaRepository.findById(id).map(facturaMapper::toDto);
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
