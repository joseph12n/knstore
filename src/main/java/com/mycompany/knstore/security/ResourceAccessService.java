package com.mycompany.knstore.security;

import com.mycompany.knstore.repository.CarritoRepository;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.DireccionRepository;
import com.mycompany.knstore.repository.EnvioRepository;
import com.mycompany.knstore.repository.FacturaRepository;
import com.mycompany.knstore.repository.ItemCarritoRepository;
import com.mycompany.knstore.repository.ItemPedidoRepository;
import com.mycompany.knstore.repository.PagoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.service.dto.CarritoDTO;
import com.mycompany.knstore.service.dto.CuentaDTO;
import com.mycompany.knstore.service.dto.DireccionDTO;
import com.mycompany.knstore.service.dto.EnvioDTO;
import com.mycompany.knstore.service.dto.FacturaDTO;
import com.mycompany.knstore.service.dto.ItemCarritoDTO;
import com.mycompany.knstore.service.dto.ItemPedidoDTO;
import com.mycompany.knstore.service.dto.PagoDTO;
import com.mycompany.knstore.service.dto.PedidoDTO;
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

    public boolean canAccessCuentaDto(CuentaDTO cuentaDTO) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente()) {
            return false;
        }
        if (cuentaDTO == null || cuentaDTO.getUser() == null || cuentaDTO.getUser().getLogin() == null) {
            return false;
        }
        return getCurrentLogin()
            .map(login -> login.equalsIgnoreCase(cuentaDTO.getUser().getLogin()))
            .orElse(false);
    }

    public boolean canAccessCuentaId(String id) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || id == null) {
            return false;
        }
        return getCurrentLogin()
            .flatMap(login -> cuentaRepository.findByIdAndUserLogin(id, login).map(cuenta -> true))
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
        return getCurrentLogin()
            .flatMap(login -> carritoRepository.findByIdAndCuentaUserLogin(id, login).map(carrito -> true))
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
        return getCurrentLogin()
            .flatMap(login -> pedidoRepository.findByIdAndCuentaUserLogin(id, login).map(pedido -> true))
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

    public boolean canAccessItemCarritoId(String id) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || id == null) {
            return false;
        }
        return getCurrentLogin()
            .flatMap(login -> itemCarritoRepository.findByIdAndCarritoCuentaUserLogin(id, login).map(itemCarrito -> true))
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

    public boolean canAccessItemPedidoId(String id) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || id == null) {
            return false;
        }
        return getCurrentLogin()
            .flatMap(login -> itemPedidoRepository.findByIdAndPedidoCuentaUserLogin(id, login).map(itemPedido -> true))
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

    public boolean canAccessPagoId(String id) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || id == null) {
            return false;
        }
        return getCurrentLogin()
            .flatMap(login -> pagoRepository.findByIdAndPedidoCuentaUserLogin(id, login).map(pago -> true))
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

    public boolean canAccessEnvioId(String id) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || id == null) {
            return false;
        }
        return getCurrentLogin()
            .flatMap(login -> envioRepository.findByIdAndPedidoCuentaUserLogin(id, login).map(envio -> true))
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

    public boolean canAccessFacturaId(String id) {
        if (isAdminOrManager()) {
            return true;
        }
        if (!isCliente() || id == null) {
            return false;
        }
        return getCurrentLogin()
            .flatMap(login -> facturaRepository.findByIdAndPagoPedidoCuentaUserLogin(id, login).map(factura -> true))
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
        return getCurrentLogin()
            .flatMap(login -> direccionRepository.findByIdAndCuentaUserLogin(id, login).map(direccion -> true))
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

    private Optional<String> getCurrentLogin() {
        return SecurityUtils.getCurrentUserLogin();
    }
}
