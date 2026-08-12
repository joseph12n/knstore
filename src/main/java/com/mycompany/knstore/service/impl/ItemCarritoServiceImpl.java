package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Carrito;
import com.mycompany.knstore.domain.ItemCarrito;
import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.repository.CarritoRepository;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.ItemCarritoRepository;
import com.mycompany.knstore.repository.ProductoRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.ItemCarritoService;
import com.mycompany.knstore.service.dto.ItemCarritoDTO;
import com.mycompany.knstore.service.mapper.ItemCarritoMapper;
import com.mycompany.knstore.service.util.MoneyUtils;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.ItemCarrito}.
 */
@Service
public class ItemCarritoServiceImpl implements ItemCarritoService {

    private static final Logger LOG = LoggerFactory.getLogger(ItemCarritoServiceImpl.class);

    private final ItemCarritoRepository itemCarritoRepository;

    private final CarritoRepository carritoRepository;

    private final CuentaRepository cuentaRepository;

    private final ProductoRepository productoRepository;

    private final ItemCarritoMapper itemCarritoMapper;

    public ItemCarritoServiceImpl(
        ItemCarritoRepository itemCarritoRepository,
        CarritoRepository carritoRepository,
        CuentaRepository cuentaRepository,
        ProductoRepository productoRepository,
        ItemCarritoMapper itemCarritoMapper
    ) {
        this.itemCarritoRepository = itemCarritoRepository;
        this.carritoRepository = carritoRepository;
        this.cuentaRepository = cuentaRepository;
        this.productoRepository = productoRepository;
        this.itemCarritoMapper = itemCarritoMapper;
    }

    @Override
    public ItemCarritoDTO save(ItemCarritoDTO itemCarritoDTO) {
        LOG.debug("Request to save ItemCarrito : {}", itemCarritoDTO);
        ItemCarrito itemCarrito = itemCarritoMapper.toEntity(itemCarritoDTO);
        itemCarrito = guardarConTotales(itemCarrito);
        return itemCarritoMapper.toDto(itemCarrito);
    }

    @Override
    public ItemCarritoDTO update(ItemCarritoDTO itemCarritoDTO) {
        LOG.debug("Request to update ItemCarrito : {}", itemCarritoDTO);
        ItemCarrito itemCarrito = itemCarritoMapper.toEntity(itemCarritoDTO);
        itemCarrito = guardarConTotales(itemCarrito);
        return itemCarritoMapper.toDto(itemCarrito);
    }

    @Override
    public Optional<ItemCarritoDTO> partialUpdate(ItemCarritoDTO itemCarritoDTO) {
        LOG.debug("Request to partially update ItemCarrito : {}", itemCarritoDTO);

        return itemCarritoRepository
            .findById(itemCarritoDTO.getId())
            .map(existingItemCarrito -> {
                itemCarritoMapper.partialUpdate(existingItemCarrito, itemCarritoDTO);
                return guardarConTotales(existingItemCarrito);
            })
            .map(itemCarritoMapper::toDto);
    }

    @Override
    public List<ItemCarritoDTO> findAll() {
        LOG.debug("Request to get all ItemCarritos");
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return getCurrentAccountId()
                .map(cuentaId ->
                    carritoRepository
                        .findByCuentaId(cuentaId)
                        .stream()
                        .flatMap(carrito -> itemCarritoRepository.findByCarritoId(carrito.getId()).stream())
                        .map(itemCarritoMapper::toDto)
                        .collect(Collectors.toCollection(LinkedList::new))
                )
                .orElseGet(LinkedList::new);
        }
        return itemCarritoRepository.findAll().stream().map(itemCarritoMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<ItemCarritoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return itemCarritoRepository.findAllWithEagerRelationships(pageable).map(itemCarritoMapper::toDto);
    }

    @Override
    public Optional<ItemCarritoDTO> findOne(String id) {
        LOG.debug("Request to get ItemCarrito : {}", id);
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return getCurrentAccountId()
                .flatMap(cuentaId ->
                    carritoRepository
                        .findByCuentaId(cuentaId)
                        .stream()
                        .map(carrito -> itemCarritoRepository.findByIdAndCarritoId(id, carrito.getId()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst()
                )
                .map(itemCarritoMapper::toDto);
        }
        return itemCarritoRepository.findOneWithEagerRelationships(id).map(itemCarritoMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete ItemCarrito : {}", id);
        Optional<String> carritoId = itemCarritoRepository.findById(id).map(ItemCarrito::getCarrito).map(Carrito::getId);
        itemCarritoRepository.deleteById(id);
        carritoId.ifPresent(this::recalcularSubtotalCarrito);
    }

    private ItemCarrito guardarConTotales(ItemCarrito itemCarrito) {
        validarCantidad(itemCarrito);
        resolverPrecioDesdeProducto(itemCarrito);
        itemCarrito.setSubtotal(calcularSubtotal(itemCarrito));
        itemCarrito = itemCarritoRepository.save(itemCarrito);
        if (itemCarrito.getCarrito() != null) {
            recalcularSubtotalCarrito(itemCarrito.getCarrito().getId());
        }
        return itemCarrito;
    }

    private void validarCantidad(ItemCarrito itemCarrito) {
        if (itemCarrito.getCantidad() == null || itemCarrito.getCantidad() < 1) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
    }

    /**
     * El precio unitario nunca se toma del cliente: se resuelve siempre desde el
     * producto en base de datos para impedir la manipulacion de precios.
     */
    private void resolverPrecioDesdeProducto(ItemCarrito itemCarrito) {
        if (itemCarrito.getProducto() == null || itemCarrito.getProducto().getId() == null) {
            throw new IllegalArgumentException("El producto es obligatorio");
        }
        Producto producto = productoRepository
            .findById(itemCarrito.getProducto().getId())
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        if (producto.getPrecio() == null || producto.getPrecio().getPrecioVenta() == null) {
            throw new IllegalArgumentException("El producto no tiene precio de venta configurado");
        }
        itemCarrito.setPrecioUnitario(producto.getPrecio().getPrecioVenta());
    }

    private BigDecimal calcularSubtotal(ItemCarrito itemCarrito) {
        BigDecimal cantidad = BigDecimal.valueOf(itemCarrito.getCantidad() == null ? 0 : itemCarrito.getCantidad());
        return MoneyUtils.multiplicar(cantidad, itemCarrito.getPrecioUnitario());
    }

    private void recalcularSubtotalCarrito(String carritoId) {
        carritoRepository.findById(carritoId).ifPresent(carrito -> {
            BigDecimal subtotal = itemCarritoRepository
                .findByCarritoId(carritoId)
                .stream()
                .map(ItemCarrito::getSubtotal)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            carrito.setSubtotal(subtotal);
            carrito.setFechaActualizacion(Instant.now());
            carritoRepository.save(carrito);
        });
    }

    private Optional<String> getCurrentAccountId() {
        return SecurityUtils.getCurrentUserId()
            .flatMap(cuentaRepository::findOneByUserId)
            .map(cuenta -> cuenta.getId());
    }
}
