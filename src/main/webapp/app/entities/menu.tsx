import React from 'react';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/tipo-documento">
        Tipo Documento
      </MenuItem>
      <MenuItem icon="asterisk" to="/cuenta">
        Cuenta
      </MenuItem>
      <MenuItem icon="asterisk" to="/categoria">
        Categoria
      </MenuItem>
      <MenuItem icon="asterisk" to="/subcategoria">
        Subcategoria
      </MenuItem>
      <MenuItem icon="asterisk" to="/producto">
        Producto
      </MenuItem>
      <MenuItem icon="asterisk" to="/variante-producto">
        Variante Producto
      </MenuItem>
      <MenuItem icon="asterisk" to="/etiqueta-producto">
        Etiqueta Producto
      </MenuItem>
      <MenuItem icon="asterisk" to="/direccion">
        Direccion
      </MenuItem>
      <MenuItem icon="asterisk" to="/carrito">
        Carrito
      </MenuItem>
      <MenuItem icon="asterisk" to="/item-carrito">
        Item Carrito
      </MenuItem>
      <MenuItem icon="asterisk" to="/pedido">
        Pedido
      </MenuItem>
      <MenuItem icon="asterisk" to="/item-pedido">
        Item Pedido
      </MenuItem>
      <MenuItem icon="asterisk" to="/pago">
        Pago
      </MenuItem>
      <MenuItem icon="asterisk" to="/envio">
        Envio
      </MenuItem>
      <MenuItem icon="asterisk" to="/factura">
        Factura
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
