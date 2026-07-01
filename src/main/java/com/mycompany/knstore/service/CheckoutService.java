package com.mycompany.knstore.service;

import com.mycompany.knstore.domain.*;
import com.mycompany.knstore.domain.enumeration.*;
import com.mycompany.knstore.repository.*;
import com.mycompany.knstore.service.dto.*;
import com.mycompany.knstore.service.mapper.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de checkout atómico. Crea pedido, ítems, pago (simbólico), envío y factura
 * en una sola operación, además de decrementar stock y vaciar el carrito del cliente.
 */
@Service
@Transactional
public class CheckoutService {

    private static final Logger LOG = LoggerFactory.getLogger(CheckoutService.class);

    private static final String PEDIDO_SEQUENCE_COLLECTION = "pedido_sequence";

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final PagoRepository pagoRepository;
    private final EnvioRepository envioRepository;
    private final FacturaRepository facturaRepository;
    private final ProductoRepository productoRepository;
    private final ProductoInventarioRepository productoInventarioRepository;
    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final DireccionRepository direccionRepository;
    private final MongoTemplate mongoTemplate;

    private final PedidoMapper pedidoMapper;
    private final ItemPedidoMapper itemPedidoMapper;

    public CheckoutService(
        PedidoRepository pedidoRepository,
        ItemPedidoRepository itemPedidoRepository,
        PagoRepository pagoRepository,
        EnvioRepository envioRepository,
        FacturaRepository facturaRepository,
        ProductoRepository productoRepository,
        ProductoInventarioRepository productoInventarioRepository,
        CarritoRepository carritoRepository,
        ItemCarritoRepository itemCarritoRepository,
        DireccionRepository direccionRepository,
        MongoTemplate mongoTemplate,
        PedidoMapper pedidoMapper,
        ItemPedidoMapper itemPedidoMapper
    ) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.pagoRepository = pagoRepository;
        this.envioRepository = envioRepository;
        this.facturaRepository = facturaRepository;
        this.productoRepository = productoRepository;
        this.productoInventarioRepository = productoInventarioRepository;
        this.carritoRepository = carritoRepository;
        this.itemCarritoRepository = itemCarritoRepository;
        this.direccionRepository = direccionRepository;
        this.mongoTemplate = mongoTemplate;
        this.pedidoMapper = pedidoMapper;
        this.itemPedidoMapper = itemPedidoMapper;
    }

    public CheckoutResultDTO checkout(Cuenta cuenta, CheckoutRequestDTO request) {
        LOG.debug("Request to checkout for cuenta {}: {}", cuenta.getId(), request);

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new CheckoutException("El carrito está vacío");
        }

        Direccion direccion = direccionRepository
            .findById(request.getDireccionId())
            .orElseThrow(() -> new CheckoutException("Dirección no encontrada"));

        if (direccion.getCuenta() == null || !direccion.getCuenta().getId().equals(cuenta.getId())) {
            throw new CheckoutException("La dirección no pertenece a la cuenta");
        }

        // Cargar productos, validar stock y precios
        Map<String, Producto> productosMap = new HashMap<>();
        Map<String, Integer> cantidadPorProducto = new HashMap<>();
        for (CheckoutItemDTO item : request.getItems()) {
            cantidadPorProducto.merge(item.getProductoId(), item.getCantidad(), Integer::sum);
        }

        for (String productoId : cantidadPorProducto.keySet()) {
            Producto producto = productoRepository
                .findById(productoId)
                .orElseThrow(() -> new CheckoutException("Producto no encontrado: " + productoId));
            productosMap.put(productoId, producto);

            Integer stock = producto.getInventario() != null ? producto.getInventario().getStock() : 0;
            Integer requerido = cantidadPorProducto.get(productoId);
            if (stock == null || stock < requerido) {
                throw new CheckoutException(
                    "Stock insuficiente para " + producto.getNombre() + " (disponible: " + (stock == null ? 0 : stock) + ")"
                );
            }

            // Validar precio contra el precio de venta real del producto
            CheckoutItemDTO itemRequest = request
                .getItems()
                .stream()
                .filter(i -> i.getProductoId().equals(productoId))
                .findFirst()
                .orElseThrow();
            BigDecimal precioEsperado =
                producto.getPrecio() != null && producto.getPrecio().getPrecioVenta() != null
                    ? producto.getPrecio().getPrecioVenta()
                    : BigDecimal.ZERO;
            if (precioEsperado.compareTo(BigDecimal.ZERO) > 0 && itemRequest.getPrecioUnitario().compareTo(precioEsperado) != 0) {
                throw new CheckoutException("Precio incorrecto para " + producto.getNombre());
            }
        }

        // Calcular totales
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal ivaTotal = BigDecimal.ZERO;
        for (CheckoutItemDTO item : request.getItems()) {
            Producto producto = productosMap.get(item.getProductoId());
            BigDecimal precio = item.getPrecioUnitario() != null ? item.getPrecioUnitario() : BigDecimal.ZERO;
            BigDecimal cantidad = BigDecimal.valueOf(item.getCantidad());
            BigDecimal itemSubtotal = precio.multiply(cantidad);
            subtotal = subtotal.add(itemSubtotal);

            BigDecimal porcentajeIva =
                producto.getCategoriaIva() != null && producto.getCategoriaIva().getPorcentaje() != null
                    ? producto.getCategoriaIva().getPorcentaje()
                    : BigDecimal.ZERO;
            BigDecimal valorIva = itemSubtotal.multiply(porcentajeIva).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            ivaTotal = ivaTotal.add(valorIva);
        }

        BigDecimal costoEnvio = calcularCostoEnvio(request.getTipoServicioEnvio());
        BigDecimal descuento = BigDecimal.ZERO;
        BigDecimal total = subtotal.add(ivaTotal).add(costoEnvio).subtract(descuento);

        // Crear pedido
        Pedido pedido = new Pedido();
        pedido.setNumeroPedido(generarNumeroPedido());
        pedido.setEstado(EstadoPedido.CONFIRMED);
        pedido.setSubtotal(subtotal);
        pedido.setIvaTotal(ivaTotal);
        pedido.setCostoEnvio(costoEnvio);
        pedido.setDescuento(descuento);
        pedido.setTotal(total);
        pedido.setNotasCliente(request.getNotasCliente());
        pedido.setDireccion(direccion);
        pedido.setCuenta(cuenta);
        pedido = pedidoRepository.save(pedido);

        // Crear ítems del pedido y decrementar stock de forma atómica
        for (CheckoutItemDTO item : request.getItems()) {
            Producto producto = productosMap.get(item.getProductoId());

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setNombreProducto(producto.getNombre());
            itemPedido.setSlugProducto(producto.getSlug());
            itemPedido.setMarcaProducto(producto.getMarca() != null ? producto.getMarca().getNombre() : null);
            itemPedido.setSkuProducto(producto.getSku());
            itemPedido.setColorProducto(producto.getColor());
            itemPedido.setTallaProducto(producto.getTalla());
            itemPedido.setCantidad(item.getCantidad());
            itemPedido.setPrecioUnitario(item.getPrecioUnitario());
            itemPedido.setSubtotal(item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())));

            BigDecimal porcentajeIva =
                producto.getCategoriaIva() != null && producto.getCategoriaIva().getPorcentaje() != null
                    ? producto.getCategoriaIva().getPorcentaje()
                    : BigDecimal.ZERO;
            itemPedido.setPorcentajeIva(porcentajeIva);
            BigDecimal valorIva = itemPedido.getSubtotal().multiply(porcentajeIva).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            itemPedido.setValorIva(valorIva);
            itemPedido.setDescuento(BigDecimal.ZERO);
            itemPedido.setPedido(pedido);
            itemPedido.setProducto(producto);
            itemPedidoRepository.save(itemPedido);

            // Decrementar stock de forma atómica
            if (producto.getInventario() != null) {
                decrementarStockAtomico(producto.getInventario().getId(), item.getCantidad(), producto.getNombre());
            }
        }

        // Crear pago simbólico aprobado
        Pago pago = new Pago();
        pago.setMetodoPago(request.getMetodoPago());
        pago.setEstado(EstadoPago.APPROVED);
        pago.setMonto(total);
        pago.setReferenciaPasarela("PAGO-SIMBOLICO-" + pedido.getNumeroPedido());
        pago.setCodigoAutorizacion("AUT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        pago.setDescripcionRespuesta("Pago aprobado de forma simbólica");
        pago.setIntentos(1);
        pago.setFechaPago(Instant.now());
        pago.setPedido(pedido);
        pago = pagoRepository.save(pago);

        // Crear envío y asociarlo al pedido
        Envio envio = new Envio();
        envio.setTipoServicio(request.getTipoServicioEnvio());
        envio.setEstado(EstadoEnvio.PENDING);
        envio.setCostoEnvio(costoEnvio);
        envio.setPedido(pedido);
        envio.setObservaciones("Envío registrado automáticamente desde el checkout");
        envio = envioRepository.save(envio);
        pedido.setEnvio(envio);
        pedidoRepository.save(pedido);

        // Crear factura simbólica
        Factura factura = new Factura();
        factura.setPrefijo("FE");
        factura.setSubtotal(subtotal);
        factura.setDescuentos(descuento);
        factura.setBaseGravableIva(subtotal);
        factura.setValorIva(ivaTotal);
        factura.setTotal(total);
        factura.setEnviada(false);
        factura.setFechaEmision(Instant.now());
        factura.setFechaVencimiento(LocalDate.now().plusDays(30));
        factura.setPago(pago);
        facturaRepository.save(factura);

        // Vaciar carrito del usuario
        vaciarCarrito(cuenta);

        CheckoutResultDTO result = new CheckoutResultDTO();
        result.setPedido(pedidoMapper.toDto(pedido));
        return result;
    }

    private void decrementarStockAtomico(String inventarioId, int cantidad, String nombreProducto) {
        Query query = new Query(Criteria.where("id").is(inventarioId).and("stock").gte(cantidad));
        Update update = new Update().inc("stock", -cantidad);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        ProductoInventario actualizado = mongoTemplate.findAndModify(query, update, options, ProductoInventario.class);
        if (actualizado == null) {
            throw new CheckoutException("Stock insuficiente para " + nombreProducto + " (posible concurrencia)");
        }
    }

    private void vaciarCarrito(Cuenta cuenta) {
        carritoRepository.findByCuentaId(cuenta.getId()).forEach(carrito -> {
            List<ItemCarrito> items = itemCarritoRepository.findByCarritoId(carrito.getId());
            itemCarritoRepository.deleteAll(items);
        });
    }

    private BigDecimal calcularCostoEnvio(TipoServicioEnvio tipoServicio) {
        if (tipoServicio == null) {
            return BigDecimal.valueOf(9900);
        }
        return switch (tipoServicio) {
            case EXPRESS -> BigDecimal.valueOf(19900);
            case MISMO_DIA -> BigDecimal.valueOf(29900);
            case PROGRAMADO -> BigDecimal.valueOf(14900);
            case PUNTO_PICKUP -> BigDecimal.ZERO;
            default -> BigDecimal.valueOf(9900);
        };
    }

    private String generarNumeroPedido() {
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sequenceKey = "PED-" + fecha;

        Query query = new Query(Criteria.where("_id").is(sequenceKey));
        Update update = new Update().inc("seq", 1);
        FindAndModifyOptions options = new FindAndModifyOptions().upsert(true).returnNew(true);
        Document sequence = mongoTemplate.findAndModify(query, update, options, Document.class, PEDIDO_SEQUENCE_COLLECTION);

        long seq = sequence != null ? ((Number) sequence.get("seq")).longValue() : 1L;
        return String.format("PED-%s-%06d", fecha, seq);
    }
}
