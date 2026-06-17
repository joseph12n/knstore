import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './item-pedido.reducer';

export const ItemPedido = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const itemPedidoList = useAppSelector(state => state.itemPedido.entities);
  const loading = useAppSelector(state => state.itemPedido.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const { order } = sortState;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="item-pedido-heading" data-cy="ItemPedidoHeading">
        Item Pedidos
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refrescar lista
          </Button>
          <Link to="/item-pedido/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Crear nuevo Item Pedido
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {itemPedidoList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('nombreProducto')}>
                  Nombre Producto <FontAwesomeIcon icon={getSortIconByFieldName('nombreProducto')} />
                </th>
                <th className="hand" onClick={sort('slugProducto')}>
                  Slug Producto <FontAwesomeIcon icon={getSortIconByFieldName('slugProducto')} />
                </th>
                <th className="hand" onClick={sort('marcaProducto')}>
                  Marca Producto <FontAwesomeIcon icon={getSortIconByFieldName('marcaProducto')} />
                </th>
                <th className="hand" onClick={sort('skuProducto')}>
                  Sku Producto <FontAwesomeIcon icon={getSortIconByFieldName('skuProducto')} />
                </th>
                <th className="hand" onClick={sort('colorProducto')}>
                  Color Producto <FontAwesomeIcon icon={getSortIconByFieldName('colorProducto')} />
                </th>
                <th className="hand" onClick={sort('tallaProducto')}>
                  Talla Producto <FontAwesomeIcon icon={getSortIconByFieldName('tallaProducto')} />
                </th>
                <th className="hand" onClick={sort('cantidad')}>
                  Cantidad <FontAwesomeIcon icon={getSortIconByFieldName('cantidad')} />
                </th>
                <th className="hand" onClick={sort('precioUnitario')}>
                  Precio Unitario <FontAwesomeIcon icon={getSortIconByFieldName('precioUnitario')} />
                </th>
                <th className="hand" onClick={sort('porcentajeIva')}>
                  Porcentaje Iva <FontAwesomeIcon icon={getSortIconByFieldName('porcentajeIva')} />
                </th>
                <th className="hand" onClick={sort('valorIva')}>
                  Valor Iva <FontAwesomeIcon icon={getSortIconByFieldName('valorIva')} />
                </th>
                <th className="hand" onClick={sort('descuento')}>
                  Descuento <FontAwesomeIcon icon={getSortIconByFieldName('descuento')} />
                </th>
                <th className="hand" onClick={sort('subtotal')}>
                  Subtotal <FontAwesomeIcon icon={getSortIconByFieldName('subtotal')} />
                </th>
                <th>
                  Pedido <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Producto <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {itemPedidoList.map(itemPedido => (
                <tr key={`entity-${itemPedido.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/item-pedido/${itemPedido.id}`} variant="link" size="sm">
                      {itemPedido.id}
                    </Button>
                  </td>
                  <td>{itemPedido.nombreProducto}</td>
                  <td>{itemPedido.slugProducto}</td>
                  <td>{itemPedido.marcaProducto}</td>
                  <td>{itemPedido.skuProducto}</td>
                  <td>{itemPedido.colorProducto}</td>
                  <td>{itemPedido.tallaProducto}</td>
                  <td>{itemPedido.cantidad}</td>
                  <td>{itemPedido.precioUnitario}</td>
                  <td>{itemPedido.porcentajeIva}</td>
                  <td>{itemPedido.valorIva}</td>
                  <td>{itemPedido.descuento}</td>
                  <td>{itemPedido.subtotal}</td>
                  <td>{itemPedido.pedido ? <Link to={`/pedido/${itemPedido.pedido.id}`}>{itemPedido.pedido.id}</Link> : ''}</td>
                  <td>{itemPedido.producto ? <Link to={`/producto/${itemPedido.producto.id}`}>{itemPedido.producto.nombre}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/item-pedido/${itemPedido.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">Vista</span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/item-pedido/${itemPedido.id}/edit`}
                        variant="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
                      </Button>
                      <Button
                        onClick={() => (globalThis.location.href = `/item-pedido/${itemPedido.id}/delete`)}
                        variant="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Eliminar</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">Ningún Item Pedidos encontrado</div>
        )}
      </div>
    </div>
  );
};

export default ItemPedido;
