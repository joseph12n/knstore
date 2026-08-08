package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Factura;
import com.mycompany.knstore.domain.Pago;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.enumeration.EstadoPago;
import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.FacturaRepository;
import com.mycompany.knstore.repository.PagoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.PagoService;
import com.mycompany.knstore.service.dto.PagoDTO;
import com.mycompany.knstore.service.mapper.PagoMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.Pago}.
 */
@Service
public class PagoServiceImpl implements PagoService {

    private static final Logger LOG = LoggerFactory.getLogger(PagoServiceImpl.class);

    private final PagoRepository pagoRepository;

    private final PedidoRepository pedidoRepository;

    private final CuentaRepository cuentaRepository;

    private final FacturaRepository facturaRepository;

    private final PagoMapper pagoMapper;

    public PagoServiceImpl(
        PagoRepository pagoRepository,
        PedidoRepository pedidoRepository,
        CuentaRepository cuentaRepository,
        FacturaRepository facturaRepository,
        PagoMapper pagoMapper
    ) {
        this.pagoRepository = pagoRepository;
        this.pedidoRepository = pedidoRepository;
        this.cuentaRepository = cuentaRepository;
        this.facturaRepository = facturaRepository;
        this.pagoMapper = pagoMapper;
    }

    @Override
    public PagoDTO save(PagoDTO pagoDTO) {
        LOG.debug("Request to save Pago : {}", pagoDTO);
        Pago pago = pagoMapper.toEntity(pagoDTO);
        pago = pagoRepository.save(pago);
        return pagoMapper.toDto(pago);
    }

    @Override
    public PagoDTO update(PagoDTO pagoDTO) {
        LOG.debug("Request to update Pago : {}", pagoDTO);
        Pago pago = pagoMapper.toEntity(pagoDTO);
        pago = pagoRepository.save(pago);
        return pagoMapper.toDto(pago);
    }

    @Override
    public Optional<PagoDTO> partialUpdate(PagoDTO pagoDTO) {
        LOG.debug("Request to partially update Pago : {}", pagoDTO);

        return pagoRepository
            .findById(pagoDTO.getId())
            .map(existingPago -> {
                pagoMapper.partialUpdate(existingPago, pagoDTO);

                return existingPago;
            })
            .map(pagoRepository::save)
            .map(pagoMapper::toDto);
    }

    @Override
    public Page<PagoDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Pagos");
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return getCurrentAccountId()
                .map(cuentaId -> {
                    LinkedList<PagoDTO> pagos = pedidoRepository
                        .findByCuentaId(cuentaId, Pageable.unpaged())
                        .getContent()
                        .stream()
                        .flatMap(pedido -> pagoRepository.findByPedidoId(pedido.getId(), Pageable.unpaged()).getContent().stream())
                        .map(pagoMapper::toDto)
                        .collect(Collectors.toCollection(LinkedList::new));
                    Page<PagoDTO> page = new PageImpl<>(pagos, pageable, pagos.size());
                    return page;
                })
                .orElse(Page.empty(pageable));
        }
        return pagoRepository.findAll(pageable).map(pagoMapper::toDto);
    }

    @Override
    public Optional<PagoDTO> findOne(String id) {
        LOG.debug("Request to get Pago : {}", id);
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return getCurrentAccountId()
                .flatMap(cuentaId ->
                    pedidoRepository
                        .findByCuentaId(cuentaId, Pageable.unpaged())
                        .getContent()
                        .stream()
                        .map(pedido -> pagoRepository.findByIdAndPedidoId(id, pedido.getId()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst()
                )
                .map(pagoMapper::toDto);
        }
        return pagoRepository.findById(id).map(pagoMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Pago : {}", id);
        pagoRepository.deleteById(id);
    }

    @Override
    public PagoDTO iniciarPago(String pedidoId) {
        LOG.debug("Request to iniciar pago for pedido : {}", pedidoId);
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            boolean ownsPedido = getCurrentAccountId()
                .map(cuentaId -> cuentaId.equals(pedido.getCuenta() != null ? pedido.getCuenta().getId() : null))
                .orElse(false);
            if (!ownsPedido) {
                throw new IllegalArgumentException("No tienes permiso para pagar este pedido");
            }
        }

        Pago pago = pagoRepository
            .findByPedidoId(pedidoId, org.springframework.data.domain.Pageable.unpaged())
            .getContent()
            .stream()
            .findFirst()
            .orElseGet(() -> {
                Pago nuevo = new Pago();
                nuevo.setPedido(pedido);
                nuevo.setMonto(pedido.getTotal());
                nuevo.setMetodoPago(com.mycompany.knstore.domain.enumeration.MetodoPago.NEQUI);
                nuevo.setEstado(EstadoPago.PENDING);
                nuevo.setIntentos(0);
                return nuevo;
            });

        pago.setIntentos((pago.getIntentos() == null ? 0 : pago.getIntentos()) + 1);

        // Simulación de pasarela: aprobamos en el primer intento; rechazamos tras 3 intentos.
        boolean aprobado = pago.getIntentos() <= 2;
        if (aprobado) {
            pago.setEstado(EstadoPago.APPROVED);
            pago.setReferenciaPasarela(
                "PAGO-" + pedido.getNumeroPedido() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
            );
            pago.setCodigoAutorizacion("AUT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            pago.setDescripcionRespuesta("Pago aprobado por la pasarela");
            pago.setFechaPago(Instant.now());

            pedido.setEstado(EstadoPedido.CONFIRMED);
            pedidoRepository.save(pedido);

            crearFacturaSiNoExiste(pago, pedido);
        } else {
            pago.setEstado(EstadoPago.REJECTED);
            pago.setDescripcionRespuesta("Pago rechazado por la pasarela. Puedes reintentar.");
        }

        pago = pagoRepository.save(pago);
        return pagoMapper.toDto(pago);
    }

    private void crearFacturaSiNoExiste(Pago pago, Pedido pedido) {
        boolean existeFactura = facturaRepository
            .findByPagoId(pago.getId(), org.springframework.data.domain.Pageable.unpaged())
            .hasContent();
        if (!existeFactura) {
            Factura factura = new Factura();
            factura.setPrefijo("FE");
            factura.setSubtotal(pedido.getSubtotal());
            factura.setDescuentos(pedido.getDescuento() == null ? BigDecimal.ZERO : pedido.getDescuento());
            factura.setBaseGravableIva(pedido.getSubtotal());
            factura.setValorIva(pedido.getIvaTotal());
            factura.setTotal(pedido.getTotal());
            factura.setEnviada(false);
            factura.setFechaEmision(Instant.now());
            factura.setFechaVencimiento(LocalDate.now().plusDays(30));
            factura.setPago(pago);
            facturaRepository.save(factura);
        }
    }

    private Optional<String> getCurrentAccountId() {
        return SecurityUtils.getCurrentUserId()
            .flatMap(cuentaRepository::findOneByUserId)
            .map(cuenta -> cuenta.getId());
    }
}
