import React from 'react';

import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { Authority } from 'app/shared/jhipster/constants';
import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  const authorities = useAppSelector(state => state.authentication.account.authorities);
  const isCatalogRole = hasAnyAuthority(authorities, [Authority.ADMIN, Authority.MANAGER]);
  const isClientPanelRole = hasAnyAuthority(authorities, [Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE]);

  return (
    <>
      {/* prettier-ignore */}
      {isCatalogRole && (
        <MenuItem icon="asterisk" to="/tipo-documento">
          Tipo Documento
        </MenuItem>
      )}
      {isClientPanelRole && (
        <MenuItem icon="asterisk" to="/cuenta">
          Cuenta
        </MenuItem>
      )}
      {isCatalogRole && (
        <MenuItem icon="asterisk" to="/categoria">
          Categoria
        </MenuItem>
      )}
      {isCatalogRole && (
        <MenuItem icon="asterisk" to="/subcategoria">
          Subcategoria
        </MenuItem>
      )}
      {isCatalogRole && (
        <MenuItem icon="asterisk" to="/marca">
          Marca
        </MenuItem>
      )}
      {isCatalogRole && (
        <MenuItem icon="asterisk" to="/categoria-iva">
          Categoria IVA
        </MenuItem>
      )}
      {isCatalogRole && (
        <MenuItem icon="asterisk" to="/producto">
          Producto
        </MenuItem>
      )}
      {isCatalogRole && (
        <MenuItem icon="asterisk" to="/producto-precio">
          Producto Precio
        </MenuItem>
      )}
      {isCatalogRole && (
        <MenuItem icon="asterisk" to="/producto-inventario">
          Producto Inventario
        </MenuItem>
      )}
      {isCatalogRole && (
        <MenuItem icon="asterisk" to="/producto-imagen">
          Producto Imagen
        </MenuItem>
      )}
      {isCatalogRole && (
        <MenuItem icon="asterisk" to="/etiqueta-producto">
          Etiqueta Producto
        </MenuItem>
      )}
      {isClientPanelRole && (
        <MenuItem icon="asterisk" to="/direccion">
          Direccion
        </MenuItem>
      )}
      {isClientPanelRole && (
        <MenuItem icon="asterisk" to="/carrito">
          Carrito
        </MenuItem>
      )}
      {isClientPanelRole && (
        <MenuItem icon="asterisk" to="/pedido">
          Pedido
        </MenuItem>
      )}
      {isClientPanelRole && (
        <MenuItem icon="asterisk" to="/item-pedido">
          Item Pedido
        </MenuItem>
      )}
      {isClientPanelRole && (
        <MenuItem icon="asterisk" to="/item-carrito">
          Item Carrito
        </MenuItem>
      )}
      {isClientPanelRole && (
        <MenuItem icon="asterisk" to="/pago">
          Pago
        </MenuItem>
      )}
      {isClientPanelRole && (
        <MenuItem icon="asterisk" to="/envio">
          Envio
        </MenuItem>
      )}
      {isClientPanelRole && (
        <MenuItem icon="asterisk" to="/factura">
          Factura
        </MenuItem>
      )}
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
