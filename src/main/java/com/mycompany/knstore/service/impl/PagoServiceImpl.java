package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Pago;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.enumeration.EstadoPago;
import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.PagoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.HistorialEstadoService;
import com.mycompany.knstore.service.PagoService;
import com.mycompany.knstore.service.dto.PagoCallbackRequestDTO;
import com.mycompany.knstore.service.dto.PagoDTO;
import com.mycompany.knstore.service.dto.PagoIniciarRequestDTO;
import com.mycompany.knstore.service.mapper.PagoMapper;
import com.mycompany.knstore.service.payment.PaymentGateway;
import com.mycompany.knstore.service.payment.PaymentGatewayInitResult;
import com.mycompany.knstore.service.payment.PaymentGatewayRefundResult;
import com.mycompany.knstore.service.payment.PaymentGatewayStatusResult;
import com.mycompany.knstore.service.util.MoneyUtils;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
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

    private final PagoMapper pagoMapper;

    private final HistorialEstadoService historialEstadoService;

    private final PaymentGateway paymentGateway;

    public PagoServiceImpl(
        PagoRepository pagoRepository,
        PedidoRepository pedidoRepository,
        CuentaRepository cuentaRepository,
        PagoMapper pagoMapper,
        HistorialEstadoService historialEstadoService,
        PaymentGateway paymentGateway
    ) {
        this.pagoRepository = pagoRepository;
        this.pedidoRepository = pedidoRepository;
        this.cuentaRepository = cuentaRepository;
        this.pagoMapper = pagoMapper;
        this.historialEstadoService = historialEstadoService;
        this.paymentGateway = paymentGateway;
    }

    @Override
    public PagoDTO save(PagoDTO pagoDTO) {
        LOG.debug("Request to save Pago : {}", pagoDTO);
        Pago pago = pagoMapper.toEntity(pagoDTO);
        normalizeMonetaryFields(pago);
        pago = pagoRepository.save(pago);
        registrarTransicionEstadoPago(pago.getId(), null, pago.getEstado());
        return pagoMapper.toDto(pago);
    }

    @Override
    public PagoDTO update(PagoDTO pagoDTO) {
        LOG.debug("Request to update Pago : {}", pagoDTO);
        Pago pago = pagoMapper.toEntity(pagoDTO);
        normalizeMonetaryFields(pago);
        EstadoPago estadoAnterior = pagoRepository.findById(pago.getId()).map(Pago::getEstado).orElse(null);
        pago = pagoRepository.save(pago);
        registrarTransicionEstadoPago(pago.getId(), estadoAnterior, pago.getEstado());
        return pagoMapper.toDto(pago);
    }

    @Override
    public Optional<PagoDTO> partialUpdate(PagoDTO pagoDTO) {
        LOG.debug("Request to partially update Pago : {}", pagoDTO);

        return pagoRepository
            .findById(pagoDTO.getId())
            .map(existingPago -> {
                EstadoPago estadoAnterior = existingPago.getEstado();
                pagoMapper.partialUpdate(existingPago, pagoDTO);
                normalizeMonetaryFields(existingPago);
                return new PagoEstadoWrapper(existingPago, estadoAnterior);
            })
            .map(wrapper -> {
                Pago pagoGuardado = pagoRepository.save(wrapper.pago());
                registrarTransicionEstadoPago(pagoGuardado.getId(), wrapper.estadoAnterior(), pagoGuardado.getEstado());
                return pagoGuardado;
            })
            .map(pagoMapper::toDto);
    }

    @Override
    public PagoDTO iniciarPago(PagoIniciarRequestDTO iniciarRequest) {
        LOG.debug("Request to iniciar pago: {}", iniciarRequest);
        Pedido pedido = pedidoRepository
            .findById(iniciarRequest.getPedidoId())
            .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        Optional<Pago> existingPending = pagoRepository
            .findByPedidoId(pedido.getId(), Pageable.unpaged())
            .getContent()
            .stream()
            .filter(pago -> EstadoPago.PENDING.equals(pago.getEstado()))
            .max(Comparator.comparing(Pago::getCreatedDate, Comparator.nullsLast(Comparator.naturalOrder())));

        if (existingPending.isPresent()) {
            return pagoMapper.toDto(existingPending.get());
        }

        PaymentGatewayInitResult initResult = paymentGateway.iniciarPago(pedido, iniciarRequest.getMetodoPago());

        Pago pago = new Pago();
        pago.setMetodoPago(iniciarRequest.getMetodoPago());
        pago.setEstado(EstadoPago.PENDING);
        pago.setMonto(MoneyUtils.normalizeOrZero(pedido.getTotal()));
        pago.setReferenciaPasarela(initResult.referenciaPasarela());
        pago.setDescripcionRespuesta(initResult.descripcion());
        pago.setIntentos(0);
        pago.setPedido(pedido);

        pago = pagoRepository.save(pago);
        registrarTransicionEstadoPago(pago.getId(), null, pago.getEstado());

        if (!EstadoPedido.PENDING.equals(pedido.getEstado())) {
            EstadoPedido estadoAnterior = pedido.getEstado();
            pedido.setEstado(EstadoPedido.PENDING);
            pedidoRepository.save(pedido);
            registrarTransicionEstadoPedido(pedido.getId(), estadoAnterior, pedido.getEstado());
        }

        return pagoMapper.toDto(pago);
    }

    @Override
    public PagoDTO procesarCallback(PagoCallbackRequestDTO callbackRequest) {
        LOG.debug("Request to procesar callback de pago: {}", callbackRequest);
        PaymentGatewayStatusResult callbackResult = paymentGateway.procesarCallback(callbackRequest);

        Pago pago = pagoRepository
            .findByReferenciaPasarela(callbackResult.referenciaPasarela())
            .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado para la referencia"));

        if (!EstadoPago.PENDING.equals(pago.getEstado())) {
            return pagoMapper.toDto(pago);
        }

        Pedido pedido = pago.getPedido();
        if (pedido == null) {
            throw new IllegalArgumentException("El pago no tiene pedido asociado");
        }

        BigDecimal montoCallback = MoneyUtils.normalizeOrZero(callbackResult.monto());
        BigDecimal montoPedido = MoneyUtils.normalizeOrZero(pedido.getTotal());

        EstadoPago estadoAnteriorPago = pago.getEstado();
        EstadoPedido estadoAnteriorPedido = pedido.getEstado();

        if (montoCallback.compareTo(montoPedido) != 0) {
            pago.setEstado(EstadoPago.REJECTED);
            pago.setDescripcionRespuesta("Monto incoherente entre callback y total del pedido");
            pago.setIntentos((pago.getIntentos() == null ? 0 : pago.getIntentos()) + 1);
            pedido.setEstado(EstadoPedido.CANCELLED);
        } else {
            EstadoPago estadoCallback = callbackResult.estado();
            if (EstadoPago.APPROVED.equals(estadoCallback)) {
                pago.setEstado(EstadoPago.APPROVED);
                pago.setFechaPago(Instant.now());
                pago.setCodigoAutorizacion(callbackResult.codigoAutorizacion());
                pago.setDescripcionRespuesta(callbackResult.descripcion());
                pago.setIntentos((pago.getIntentos() == null ? 0 : pago.getIntentos()) + 1);
                pedido.setEstado(EstadoPedido.CONFIRMED);
            } else {
                pago.setEstado(EstadoPago.REJECTED);
                pago.setDescripcionRespuesta(callbackResult.descripcion() != null ? callbackResult.descripcion() : "Pago rechazado");
                pago.setIntentos((pago.getIntentos() == null ? 0 : pago.getIntentos()) + 1);
                pedido.setEstado(EstadoPedido.CANCELLED);
            }
        }

        pedidoRepository.save(pedido);
        registrarTransicionEstadoPedido(pedido.getId(), estadoAnteriorPedido, pedido.getEstado());

        pago = pagoRepository.save(pago);
        registrarTransicionEstadoPago(pago.getId(), estadoAnteriorPago, pago.getEstado());

        return pagoMapper.toDto(pago);
    }

    @Override
    public PagoDTO reembolsar(String pagoId, String motivo) {
        LOG.debug("Request to refund Pago: {}", pagoId);
        Pago pago = pagoRepository.findById(pagoId).orElseThrow(() -> new IllegalArgumentException("Pago no encontrado"));

        if (EstadoPago.REFUNDED.equals(pago.getEstado())) {
            throw new IllegalArgumentException("El pago ya fue reembolsado");
        }
        if (!EstadoPago.APPROVED.equals(pago.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden reembolsar pagos APPROVED");
        }

        PaymentGatewayRefundResult refundResult = paymentGateway.reembolsar(pago, motivo);

        EstadoPago estadoAnterior = pago.getEstado();
        pago.setEstado(EstadoPago.REFUNDED);
        pago.setMotivoReembolso(motivo);
        pago.setFechaReembolso(Instant.now());
        pago.setDescripcionRespuesta(refundResult.descripcion());

        pago = pagoRepository.save(pago);
        registrarTransicionEstadoPago(pago.getId(), estadoAnterior, pago.getEstado());

        return pagoMapper.toDto(pago);
    }

    private void registrarTransicionEstadoPago(String pagoId, EstadoPago estadoAnterior, EstadoPago estadoNuevo) {
        if (pagoId == null || Objects.equals(estadoAnterior, estadoNuevo)) {
            return;
        }
        historialEstadoService.registrarCambioEstado(
            "Pago",
            pagoId,
            "estado",
            estadoAnterior != null ? estadoAnterior.name() : null,
            estadoNuevo != null ? estadoNuevo.name() : null
        );
    }

    private record PagoEstadoWrapper(Pago pago, EstadoPago estadoAnterior) {}

    private void registrarTransicionEstadoPedido(String pedidoId, EstadoPedido estadoAnterior, EstadoPedido estadoNuevo) {
        if (pedidoId == null || Objects.equals(estadoAnterior, estadoNuevo)) {
            return;
        }
        historialEstadoService.registrarCambioEstado(
            "Pedido",
            pedidoId,
            "estado",
            estadoAnterior != null ? estadoAnterior.name() : null,
            estadoNuevo != null ? estadoNuevo.name() : null
        );
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

    private Optional<String> getCurrentAccountId() {
        return SecurityUtils.getCurrentUserId()
            .flatMap(cuentaRepository::findOneByUserId)
            .map(cuenta -> cuenta.getId());
    }

    private void normalizeMonetaryFields(Pago pago) {
        pago.setMonto(MoneyUtils.normalize(pago.getMonto()));
    }
}
