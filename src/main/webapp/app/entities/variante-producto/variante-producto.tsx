import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './variante-producto.reducer';

export const VarianteProducto = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const varianteProductoList = useAppSelector(state => state.varianteProducto.entities);
  const loading = useAppSelector(state => state.varianteProducto.loading);
  const totalItems = useAppSelector(state => state.varianteProducto.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const { order } = paginationState;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="variante-producto-heading" data-cy="VarianteProductoHeading">
        Variante Productos
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refrescar lista
          </Button>
          <Link to="/variante-producto/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Crear nuevo Variante Producto
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {varianteProductoList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('sku')}>
                  Sku <FontAwesomeIcon icon={getSortIconByFieldName('sku')} />
                </th>
                <th className="hand" onClick={sort('color')}>
                  Color <FontAwesomeIcon icon={getSortIconByFieldName('color')} />
                </th>
                <th className="hand" onClick={sort('talla')}>
                  Talla <FontAwesomeIcon icon={getSortIconByFieldName('talla')} />
                </th>
                <th className="hand" onClick={sort('codigoBarras')}>
                  Codigo Barras <FontAwesomeIcon icon={getSortIconByFieldName('codigoBarras')} />
                </th>
                <th className="hand" onClick={sort('stock')}>
                  Stock <FontAwesomeIcon icon={getSortIconByFieldName('stock')} />
                </th>
                <th className="hand" onClick={sort('stockMinimo')}>
                  Stock Minimo <FontAwesomeIcon icon={getSortIconByFieldName('stockMinimo')} />
                </th>
                <th className="hand" onClick={sort('ubicacionBodega')}>
                  Ubicacion Bodega <FontAwesomeIcon icon={getSortIconByFieldName('ubicacionBodega')} />
                </th>
                <th className="hand" onClick={sort('precioAdicional')}>
                  Precio Adicional <FontAwesomeIcon icon={getSortIconByFieldName('precioAdicional')} />
                </th>
                <th className="hand" onClick={sort('activo')}>
                  Activo <FontAwesomeIcon icon={getSortIconByFieldName('activo')} />
                </th>
                <th>
                  Producto <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {varianteProductoList.map(varianteProducto => (
                <tr key={`entity-${varianteProducto.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/variante-producto/${varianteProducto.id}`} variant="link" size="sm">
                      {varianteProducto.id}
                    </Button>
                  </td>
                  <td>{varianteProducto.sku}</td>
                  <td>{varianteProducto.color}</td>
                  <td>{varianteProducto.talla}</td>
                  <td>{varianteProducto.codigoBarras}</td>
                  <td>{varianteProducto.stock}</td>
                  <td>{varianteProducto.stockMinimo}</td>
                  <td>{varianteProducto.ubicacionBodega}</td>
                  <td>{varianteProducto.precioAdicional}</td>
                  <td>{varianteProducto.activo ? 'true' : 'false'}</td>
                  <td>
                    {varianteProducto.producto ? (
                      <Link to={`/producto/${varianteProducto.producto.id}`}>{varianteProducto.producto.nombre}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/variante-producto/${varianteProducto.id}`}
                        variant="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">Vista</span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/variante-producto/${varianteProducto.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        variant="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
                      </Button>
                      <Button
                        onClick={() =>
                          (globalThis.location.href = `/variante-producto/${varianteProducto.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
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
          !loading && <div className="alert alert-warning">Ningún Variante Productos encontrado</div>
        )}
      </div>
      {totalItems ? (
        <div className={varianteProductoList && varianteProductoList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default VarianteProducto;
