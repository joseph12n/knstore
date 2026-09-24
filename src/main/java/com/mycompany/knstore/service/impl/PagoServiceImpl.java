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
import com.mycompany.knstore.service.HistorialEstadoService;
import com.mycompany.knstore.service.MailService;
import com.mycompany.knstore.service.PagoService;
import com.mycompany.knstore.service.dto.PagoDTO;
import com.mycompany.knstore.service.invoice.FacturaPdfService;
import com.mycompany.knstore.service.mapper.PagoMapper;
import com.mycompany.knstore.service.payment.PaymentGateway;
import com.mycompany.knstore.service.util.MoneyUtils;
import com.mycompany.knstore.service.util.MongoIdUtils;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
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

    private final HistorialEstadoService historialEstadoService;

    private final PaymentGateway paymentGateway;

    private final FacturaPdfService facturaPdfService;

    private final MailService mailService;

    public PagoServiceImpl(
        PagoRepository pagoRepository,
        PedidoRepository pedidoRepository,
        CuentaRepository cuentaRepository,
        FacturaRepository facturaRepository,
        PagoMapper pagoMapper,
        HistorialEstadoService historialEstadoService,
        PaymentGateway paymentGateway,
        FacturaPdfService facturaPdfService,
        MailService mailService
    ) {
        this.pagoRepository = pagoRepository;
        this.pedidoRepository = pedidoRepository;
        this.cuentaRepository = cuentaRepository;
        this.facturaRepository = facturaRepository;
        this.pagoMapper = pagoMapper;
        this.historialEstadoService = historialEstadoService;
        this.paymentGateway = paymentGateway;
        this.facturaPdfService = facturaPdfService;
        this.mailService = mailService;
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
            // RNF-028: una consulta para los pedidos de la cuenta y una consulta
            // en lote para sus pagos (sin N+1 ni paginacion en memoria).
            return getCurrentAccountId()
                .map(cuentaId -> {
                    List<String> pedidoIds = pedidoRepository
                        .findByCuentaId(cuentaId, Pageable.unpaged())
                        .getContent()
                        .stream()
                        .map(Pedido::getId)
                        .toList();
                    return pagoRepository.findByPedidoIdIn(MongoIdUtils.toObjectIds(pedidoIds), withSort(pageable)).map(pagoMapper::toDto);
                })
                .orElse(Page.empty(pageable));
        }
        return pagoRepository.findAll(pageable).map(pagoMapper::toDto);
    }

    @Override
    public Optional<PagoDTO> findOne(String id) {
        LOG.debug("Request to get Pago : {}", id);
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            // RNF-028: el pago se resuelve por id y el ownership con una sola
            // consulta del pedido (2 consultas constantes, sin recorrer listas).
            return getCurrentAccountId()
                .flatMap(cuentaId -> pagoRepository.findById(id).filter(pago -> pedidoPerteneceACuenta(pago.getPedido(), cuentaId)))
                .map(pagoMapper::toDto);
        }
        return pagoRepository.findById(id).map(pagoMapper::toDto);
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
        LOG.debug("Request to delete Pago : {}", id);
        pagoRepository.deleteById(id);
    }

    @Override
    @Transactional
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
            .orElse(null);

        // Idempotencia: si el pago ya quedo resuelto (APPROVED u otro estado final), no se reprocesa.
        if (pago != null && esEstadoFinal(pago.getEstado())) {
            return pagoMapper.toDto(pago);
        }

        if (pago == null) {
            pago = new Pago();
            pago.setPedido(pedido);
            pago.setMonto(MoneyUtils.normalizar(pedido.getTotal()));
            pago.setMetodoPago(com.mycompany.knstore.domain.enumeration.MetodoPago.NEQUI);
            pago.setEstado(EstadoPago.PENDING);
            pago.setIntentos(0);
        }

        pago.setIntentos((pago.getIntentos() == null ? 0 : pago.getIntentos()) + 1);
        EstadoPago estadoAnterior = pago.getEstado();
        pago.setReferenciaPasarela(paymentGateway.iniciarPago(pago.getMonto()));
        pago.setEstado(EstadoPago.PENDING);
        pago.setDescripcionRespuesta("Pago iniciado. Esperando confirmacion de la pasarela");
        pago.setCodigoAutorizacion(null);
        pago.setFechaPago(null);
        pago = pagoRepository.save(pago);

        historialEstadoService.registrar(
            "PAGO",
            pago.getId(),
            "estado",
            estadoAnterior != null ? estadoAnterior.name() : null,
            EstadoPago.PENDING.name()
        );

        // Pasarela simbolica: el pago se aprueba automaticamente y la factura se genera en el mismo flujo.
        return procesarCallback(pago.getReferenciaPasarela(), "APPROVED", pago.getMonto(), null);
    }

    @Override
    @Transactional
    public PagoDTO procesarCallback(String referencia, String estado, BigDecimal monto, String codigoAutorizacion) {
        LOG.debug("Request to process payment callback for referencia : {}", referencia);
        Pago pago = pagoRepository
            .findByReferenciaPasarela(referencia)
            .orElseThrow(() -> new IllegalArgumentException("Referencia de pago no encontrada"));

        // Idempotencia: un pago ya resuelto no se reprocesa ni se revierte con un callback externo.
        if (esEstadoFinal(pago.getEstado())) {
            return pagoMapper.toDto(pago);
        }

        EstadoPago estadoAnterior = pago.getEstado();
        PaymentGateway.ResultadoCallback resultado = paymentGateway.procesarCallback(
            new PaymentGateway.CallbackPayload(referencia, estado, monto, codigoAutorizacion)
        );

        boolean montoCoherente = monto == null || monto.compareTo(pago.getMonto()) == 0;
        Pedido pedido = pago.getPedido();
        boolean pedidoAprobable =
            pedido == null ||
            pedido.getEstado() == null ||
            EstadoPedido.CONFIRMED.equals(pedido.getEstado()) ||
            pedido.getEstado().puedeTransicionarA(EstadoPedido.CONFIRMED);
        boolean aprobado = "APPROVED".equals(resultado.estado()) && montoCoherente && pedidoAprobable;

        if (aprobado) {
            pago.setEstado(EstadoPago.APPROVED);
            pago.setCodigoAutorizacion(resultado.codigoAutorizacion());
            pago.setDescripcionRespuesta(resultado.descripcion());
            pago.setFechaPago(Instant.now());
            pago = pagoRepository.save(pago);

            if (pedido != null && !EstadoPedido.CONFIRMED.equals(pedido.getEstado())) {
                EstadoPedido estadoPedidoAnterior = pedido.getEstado();
                pedido.setEstado(EstadoPedido.CONFIRMED);
                pedidoRepository.save(pedido);
                historialEstadoService.registrar(
                    "PEDIDO",
                    pedido.getId(),
                    "estado",
                    estadoPedidoAnterior != null ? estadoPedidoAnterior.name() : null,
                    EstadoPedido.CONFIRMED.name()
                );
            }
            crearFacturaSiNoExiste(pago, pedido);
        } else {
            pago.setEstado(EstadoPago.REJECTED);
            if (!pedidoAprobable) {
                pago.setDescripcionRespuesta("Pago rechazado: el pedido fue cancelado o devuelto y ya no puede aprobarse");
            } else {
                pago.setDescripcionRespuesta(
                    montoCoherente ? resultado.descripcion() : "Pago rechazado: el monto no coincide con el total del pedido"
                );
            }
            pago = pagoRepository.save(pago);
        }

        historialEstadoService.registrar(
            "PAGO",
            pago.getId(),
            "estado",
            estadoAnterior != null ? estadoAnterior.name() : null,
            pago.getEstado().name()
        );
        return pagoMapper.toDto(pago);
    }

    @Override
    public Optional<PagoDTO> consultarEstado(String referencia) {
        LOG.debug("Request to consultar estado de pago por referencia : {}", referencia);
        return pagoRepository.findByReferenciaPasarela(referencia).map(pagoMapper::toDto);
    }

    @Override
    @Transactional
    public PagoDTO reembolsar(String id, String motivo) {
        LOG.debug("Request to reembolsar Pago : {} - {}", id, motivo);
        Pago pago = pagoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Pago no encontrado"));

        if (EstadoPago.REFUNDED.equals(pago.getEstado())) {
            throw new IllegalStateException("Este pago ya fue reembolsado");
        }
        if (!EstadoPago.APPROVED.equals(pago.getEstado())) {
            throw new IllegalStateException("Solo se pueden reembolsar pagos aprobados");
        }

        paymentGateway.reembolsar(pago.getReferenciaPasarela(), pago.getMonto(), motivo);

        EstadoPago estadoAnterior = pago.getEstado();
        pago.setEstado(EstadoPago.REFUNDED);
        pago.setFechaReembolso(Instant.now());
        pago.setMotivoReembolso(motivo);
        pago = pagoRepository.save(pago);

        historialEstadoService.registrar(
            "PAGO",
            pago.getId(),
            "estado",
            estadoAnterior != null ? estadoAnterior.name() : null,
            EstadoPago.REFUNDED.name()
        );

        // Trazabilidad cruzada cuando el pedido ya estaba enviado o entregado.
        if (pago.getPedido() != null) {
            Pedido pedido = pago.getPedido();
            if (EstadoPedido.SHIPPED.equals(pedido.getEstado()) || EstadoPedido.DELIVERED.equals(pedido.getEstado())) {
                historialEstadoService.registrar("PEDIDO", pedido.getId(), "reembolso", null, motivo);
            }
        }
        return pagoMapper.toDto(pago);
    }

    private void crearFacturaSiNoExiste(Pago pago, Pedido pedido) {
        if (pedido == null) {
            return;
        }
        boolean existeFactura = facturaRepository
            .findByPagoId(pago.getId(), org.springframework.data.domain.Pageable.unpaged())
            .hasContent();
        if (!existeFactura) {
            Factura factura = new Factura();
            factura.setPrefijo("FE");
            factura.setNumero(facturaPdfService.generarConsecutivo("FE"));
            factura.setSubtotal(MoneyUtils.normalizar(pedido.getSubtotal()));
            factura.setDescuentos(MoneyUtils.normalizar(pedido.getDescuento() == null ? BigDecimal.ZERO : pedido.getDescuento()));
            factura.setBaseGravableIva(MoneyUtils.normalizar(pedido.getSubtotal()));
            factura.setValorIva(MoneyUtils.normalizar(pedido.getIvaTotal()));
            factura.setTotal(MoneyUtils.normalizar(pedido.getTotal()));
            factura.setEnviada(true);
            factura.setFechaEmision(Instant.now());
            factura.setFechaVencimiento(LocalDate.now().plusDays(30));
            factura.setFechaEnvioEmail(Instant.now());
            factura.setPago(pago);
            facturaRepository.save(factura);

            enviarFacturaPorCorreo(factura, pedido);
        }
    }

    private void enviarFacturaPorCorreo(Factura factura, Pedido pedido) {
        try {
            String destinatario = null;
            if (pedido.getCuenta() != null && pedido.getCuenta().getUser() != null) {
                destinatario = pedido.getCuenta().getUser().getEmail();
            }
            if (destinatario == null || destinatario.isBlank()) {
                LOG.warn("Sin correo del cliente; la factura {} no se envio", factura.getNumero());
                return;
            }
            byte[] pdf = facturaPdfService.generarPdf(factura, pedido);
            String nombreArchivo = factura.getNumero() + ".pdf";
            mailService.sendEmailWithAttachment(
                destinatario,
                "Tu factura KN-Store " + factura.getNumero(),
                "<p>Gracias por tu compra. Adjuntamos tu factura <strong>" + factura.getNumero() + "</strong>.</p>",
                nombreArchivo,
                pdf
            );
        } catch (Exception e) {
            LOG.warn("No se pudo generar o enviar la factura {}: {}", factura.getNumero(), e.getMessage());
        }
    }

    /**
     * Un pago es final solo cuando quedo APPROVED o REFUNDED. REJECTED no es
     * terminal: el cliente puede reintentarlo con {@link #iniciarPago(String)}.
     */
    private boolean esEstadoFinal(EstadoPago estado) {
        return estado == EstadoPago.APPROVED || estado == EstadoPago.REFUNDED;
    }

    private Optional<String> getCurrentAccountId() {
        return SecurityUtils.getCurrentUserId()
            .flatMap(cuentaRepository::findOneByUserId)
            .map(cuenta -> cuenta.getId());
    }
}
