package com.mycompany.knstore.service;

import com.mycompany.knstore.domain.Carrito;
import com.mycompany.knstore.domain.Envio;
import com.mycompany.knstore.domain.Factura;
import com.mycompany.knstore.domain.ItemCarrito;
import com.mycompany.knstore.domain.ItemPedido;
import com.mycompany.knstore.domain.Pago;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.repository.CarritoRepository;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.DireccionRepository;
import com.mycompany.knstore.repository.EnvioRepository;
import com.mycompany.knstore.repository.FacturaRepository;
import com.mycompany.knstore.repository.ItemCarritoRepository;
import com.mycompany.knstore.repository.ItemPedidoRepository;
import com.mycompany.knstore.repository.PagoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.dto.CarritoDTO;
import com.mycompany.knstore.service.dto.CuentaDTO;
import com.mycompany.knstore.service.dto.DireccionDTO;
import com.mycompany.knstore.service.dto.EnvioDTO;
import com.mycompany.knstore.service.dto.FacturaDTO;
import com.mycompany.knstore.service.dto.ItemCarritoDTO;
import com.mycompany.knstore.service.dto.ItemPedidoDTO;
import com.mycompany.knstore.service.dto.PagoDTO;
import com.mycompany.knstore.service.dto.PedidoDTO;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ResourceAccessService {

    private final CuentaRepository cuentaRepository;
    private final CarritoRepository carritoRepository;
    private final PedidoRepository pedidoRepository;
    private final DireccionRepository direccionRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final PagoRepository pagoRepository;
    private final EnvioRepository envioRepository;
    private final FacturaRepository facturaRepository;

    public ResourceAccessService(
        CuentaRepository cuentaRepository,
        CarritoRepository carritoRepository,
        PedidoRepository pedidoRepository,
        DireccionRepository direccionRepository,
        ItemCarritoRepository itemCarritoRepository,
        ItemPedidoRepository itemPedidoRepository,
        PagoRepository pagoRepository,
        EnvioRepository envioRepository,
        FacturaRepository facturaRepository
    ) {
        this.cuentaRepository = cuentaRepository;
        this.carritoRepository = carritoRepository;
        this.pedidoRepository = pedidoRepository;
        this.direccionRepository = direccionRepository;
        this.itemCarritoRepository = itemCarritoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.pagoRepository = pagoRepository;
        this.envioRepository = envioRepository;
        this.facturaRepository = facturaRepository;
    }

    /**
     * RF-073: el cliente puede editar su propia cuenta mandando solo el id en el
     * DTO (sin repetir el login del usuario); si no trae id, se valida el login
     * del usuario del DTO como antes. Las relaciones se preservan en el servicio.
     */
    public boolean canAccessCuentaDto(CuentaDTO cuentaDTO) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || cuentaDTO == null) {
            return false;
        }
        if (cuentaDTO.getId() != null) {
            Optional<String> userId = SecurityUtils.getCurrentUserId();
            if (userId.isPresent() && cuentaRepository.findByIdAndUserId(cuentaDTO.getId(), userId.orElseThrow()).isPresent()) {
                return true;
            }
        }
        if (cuentaDTO.getUser() != null && cuentaDTO.getUser().getLogin() != null) {
            return getCurrentUserLogin()
                .map(login -> login.equalsIgnoreCase(cuentaDTO.getUser().getLogin()))
                .orElse(false);
        }
        return false;
    }

    public boolean canAccessCuentaId(String id) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || id == null) {
            return false;
        }
        return SecurityUtils.getCurrentUserId()
            .flatMap(userId -> cuentaRepository.findByIdAndUserId(id, userId).map(cuenta -> true))
            .orElse(false);
    }

    public boolean canAccessCarritoDto(CarritoDTO carritoDTO) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || carritoDTO == null || carritoDTO.getCuenta() == null || carritoDTO.getCuenta().getId() == null) {
            return false;
        }
        return canAccessCuentaId(carritoDTO.getCuenta().getId());
    }

    public boolean canAccessCarritoId(String id) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || id == null) {
            return false;
        }
        return getCurrentAccountId()
            .flatMap(login -> carritoRepository.findByIdAndCuentaId(id, login).map(carrito -> true))
            .orElse(false);
    }

    public boolean canAccessPedidoDto(PedidoDTO pedidoDTO) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || pedidoDTO == null || pedidoDTO.getCuenta() == null || pedidoDTO.getCuenta().getId() == null) {
            return false;
        }
        return canAccessCuentaId(pedidoDTO.getCuenta().getId());
    }

    public boolean canAccessPedidoId(String id) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || id == null) {
            return false;
        }
        return getCurrentAccountId()
            .flatMap(login -> pedidoRepository.findByIdAndCuentaId(id, login).map(pedido -> true))
            .orElse(false);
    }

    public boolean canAccessItemCarritoDto(ItemCarritoDTO itemCarritoDTO) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || itemCarritoDTO == null || itemCarritoDTO.getCarrito() == null || itemCarritoDTO.getCarrito().getId() == null) {
            return false;
        }
        return canAccessCarritoId(itemCarritoDTO.getCarrito().getId());
    }

    /**
     * RNF-028: valida ownership en un numero constante de consultas, subiendo en
     * la cadena item -> carrito -> cuenta sin recorrer listas de la cuenta.
     */
    public boolean canAccessItemCarritoId(String id) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || id == null) {
            return false;
        }
        Optional<String> cuentaId = getCurrentAccountId();
        if (cuentaId.isEmpty()) {
            return false;
        }
        return itemCarritoRepository
            .findById(id)
            .map(ItemCarrito::getCarrito)
            .map(Carrito::getId)
            .filter(Objects::nonNull)
            .map(carritoId -> carritoRepository.findByIdAndCuentaId(carritoId, cuentaId.orElseThrow()).isPresent())
            .orElse(false);
    }

    public boolean canAccessItemPedidoDto(ItemPedidoDTO itemPedidoDTO) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || itemPedidoDTO == null || itemPedidoDTO.getPedido() == null || itemPedidoDTO.getPedido().getId() == null) {
            return false;
        }
        return canAccessPedidoId(itemPedidoDTO.getPedido().getId());
    }

    /**
     * RNF-028: item -> pedido -> cuenta con consultas por id (sin N+1).
     */
    public boolean canAccessItemPedidoId(String id) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || id == null) {
            return false;
        }
        Optional<String> cuentaId = getCurrentAccountId();
        if (cuentaId.isEmpty()) {
            return false;
        }
        return itemPedidoRepository
            .findById(id)
            .map(ItemPedido::getPedido)
            .map(Pedido::getId)
            .filter(Objects::nonNull)
            .map(pedidoId -> pedidoRepository.findByIdAndCuentaId(pedidoId, cuentaId.orElseThrow()).isPresent())
            .orElse(false);
    }

    public boolean canAccessPagoDto(PagoDTO pagoDTO) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || pagoDTO == null || pagoDTO.getPedido() == null || pagoDTO.getPedido().getId() == null) {
            return false;
        }
        return canAccessPedidoId(pagoDTO.getPedido().getId());
    }

    /**
     * RNF-028: pago -> pedido -> cuenta con consultas por id (sin N+1).
     */
    public boolean canAccessPagoId(String id) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || id == null) {
            return false;
        }
        Optional<String> cuentaId = getCurrentAccountId();
        if (cuentaId.isEmpty()) {
            return false;
        }
        return pagoRepository
            .findById(id)
            .map(Pago::getPedido)
            .map(Pedido::getId)
            .filter(Objects::nonNull)
            .map(pedidoId -> pedidoRepository.findByIdAndCuentaId(pedidoId, cuentaId.orElseThrow()).isPresent())
            .orElse(false);
    }

    public boolean canAccessEnvioDto(EnvioDTO envioDTO) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || envioDTO == null || envioDTO.getPedido() == null || envioDTO.getPedido().getId() == null) {
            return false;
        }
        return canAccessPedidoId(envioDTO.getPedido().getId());
    }

    /**
     * RNF-028: envio -> pedido -> cuenta con consultas por id (sin N+1).
     */
    public boolean canAccessEnvioId(String id) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || id == null) {
            return false;
        }
        Optional<String> cuentaId = getCurrentAccountId();
        if (cuentaId.isEmpty()) {
            return false;
        }
        return envioRepository
            .findById(id)
            .map(Envio::getPedido)
            .map(Pedido::getId)
            .filter(Objects::nonNull)
            .map(pedidoId -> pedidoRepository.findByIdAndCuentaId(pedidoId, cuentaId.orElseThrow()).isPresent())
            .orElse(false);
    }

    public boolean canAccessFacturaDto(FacturaDTO facturaDTO) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || facturaDTO == null || facturaDTO.getPago() == null || facturaDTO.getPago().getId() == null) {
            return false;
        }
        return canAccessPagoId(facturaDTO.getPago().getId());
    }

    /**
     * RNF-028: factura -> pago -> pedido -> cuenta con consultas por id (sin N+1).
     */
    public boolean canAccessFacturaId(String id) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || id == null) {
            return false;
        }
        Optional<String> cuentaId = getCurrentAccountId();
        if (cuentaId.isEmpty()) {
            return false;
        }
        return facturaRepository
            .findById(id)
            .map(Factura::getPago)
            .map(Pago::getId)
            .filter(Objects::nonNull)
            .map(pagoId ->
                pagoRepository
                    .findById(pagoId)
                    .map(Pago::getPedido)
                    .map(Pedido::getId)
                    .filter(Objects::nonNull)
                    .map(pedidoId -> pedidoRepository.findByIdAndCuentaId(pedidoId, cuentaId.orElseThrow()).isPresent())
                    .orElse(false)
            )
            .orElse(false);
    }

    public boolean canAccessDireccionDto(DireccionDTO direccionDTO) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || direccionDTO == null || direccionDTO.getCuenta() == null || direccionDTO.getCuenta().getId() == null) {
            return false;
        }
        return canAccessCuentaId(direccionDTO.getCuenta().getId());
    }

    public boolean canAccessDireccionId(String id) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || id == null) {
            return false;
        }
        return getCurrentAccountId()
            .flatMap(cuentaId -> direccionRepository.findByIdAndCuentaId(id, cuentaId).map(direccion -> true))
            .orElse(false);
    }

    private boolean isAdminOrManager() {
        return (
            SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN) ||
            SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.MANAGER)
        );
    }

    private boolean isCliente() {
        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE);
    }

    private Optional<String> getCurrentUserLogin() {
        return SecurityUtils.getCurrentUserLogin();
    }

    private Optional<String> getCurrentAccountId() {
        return SecurityUtils.getCurrentUserId()
            .flatMap(cuentaRepository::findOneByUserId)
            .map(cuenta -> cuenta.getId());
    }
}
