import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, byteSize, getPaginationState, openFile } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './producto.reducer';

export const Producto = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const productoList = useAppSelector(state => state.producto.entities);
  const loading = useAppSelector(state => state.producto.loading);
  const totalItems = useAppSelector(state => state.producto.totalItems);

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
      <h2 id="producto-heading" data-cy="ProductoHeading">
        Productos
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refrescar lista
          </Button>
          <Link to="/producto/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Crear nuevo Producto
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {productoList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('nombre')}>
                  Nombre <FontAwesomeIcon icon={getSortIconByFieldName('nombre')} />
                </th>
                <th className="hand" onClick={sort('slug')}>
                  Slug <FontAwesomeIcon icon={getSortIconByFieldName('slug')} />
                </th>
                <th className="hand" onClick={sort('descripcion')}>
                  Descripcion <FontAwesomeIcon icon={getSortIconByFieldName('descripcion')} />
                </th>
                <th className="hand" onClick={sort('imagen')}>
                  Imagen <FontAwesomeIcon icon={getSortIconByFieldName('imagen')} />
                </th>
                <th className="hand" onClick={sort('imagenAlt')}>
                  Imagen Alt <FontAwesomeIcon icon={getSortIconByFieldName('imagenAlt')} />
                </th>
                <th className="hand" onClick={sort('marca')}>
                  Marca <FontAwesomeIcon icon={getSortIconByFieldName('marca')} />
                </th>
                <th className="hand" onClick={sort('referencia')}>
                  Referencia <FontAwesomeIcon icon={getSortIconByFieldName('referencia')} />
                </th>
                <th className="hand" onClick={sort('codigoBarras')}>
                  Codigo Barras <FontAwesomeIcon icon={getSortIconByFieldName('codigoBarras')} />
                </th>
                <th className="hand" onClick={sort('unidadMedida')}>
                  Unidad Medida <FontAwesomeIcon icon={getSortIconByFieldName('unidadMedida')} />
                </th>
                <th className="hand" onClick={sort('pesoKg')}>
                  Peso Kg <FontAwesomeIcon icon={getSortIconByFieldName('pesoKg')} />
                </th>
                <th className="hand" onClick={sort('largoCm')}>
                  Largo Cm <FontAwesomeIcon icon={getSortIconByFieldName('largoCm')} />
                </th>
                <th className="hand" onClick={sort('anchoCm')}>
                  Ancho Cm <FontAwesomeIcon icon={getSortIconByFieldName('anchoCm')} />
                </th>
                <th className="hand" onClick={sort('altoCm')}>
                  Alto Cm <FontAwesomeIcon icon={getSortIconByFieldName('altoCm')} />
                </th>
                <th className="hand" onClick={sort('categoriaIva')}>
                  Categoria Iva <FontAwesomeIcon icon={getSortIconByFieldName('categoriaIva')} />
                </th>
                <th className="hand" onClick={sort('precioCompra')}>
                  Precio Compra <FontAwesomeIcon icon={getSortIconByFieldName('precioCompra')} />
                </th>
                <th className="hand" onClick={sort('precioVenta')}>
                  Precio Venta <FontAwesomeIcon icon={getSortIconByFieldName('precioVenta')} />
                </th>
                <th className="hand" onClick={sort('ganancia')}>
                  Ganancia <FontAwesomeIcon icon={getSortIconByFieldName('ganancia')} />
                </th>
                <th className="hand" onClick={sort('margen')}>
                  Margen <FontAwesomeIcon icon={getSortIconByFieldName('margen')} />
                </th>
                <th className="hand" onClick={sort('garantiaMeses')}>
                  Garantia Meses <FontAwesomeIcon icon={getSortIconByFieldName('garantiaMeses')} />
                </th>
                <th className="hand" onClick={sort('destacado')}>
                  Destacado <FontAwesomeIcon icon={getSortIconByFieldName('destacado')} />
                </th>
                <th className="hand" onClick={sort('activo')}>
                  Activo <FontAwesomeIcon icon={getSortIconByFieldName('activo')} />
                </th>
                <th>
                  Subcategoria <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {productoList.map(producto => (
                <tr key={`entity-${producto.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/producto/${producto.id}`} variant="link" size="sm">
                      {producto.id}
                    </Button>
                  </td>
                  <td>{producto.nombre}</td>
                  <td>{producto.slug}</td>
                  <td>{producto.descripcion}</td>
                  <td>
                    {producto.imagen ? (
                      <div>
                        {producto.imagenContentType ? (
                          <a onClick={openFile(producto.imagenContentType, producto.imagen)}>
                            <img src={`data:${producto.imagenContentType};base64,${producto.imagen}`} style={{ maxHeight: '30px' }} />
                            &nbsp;
                          </a>
                        ) : null}
                        <span>
                          {producto.imagenContentType}, {byteSize(producto.imagen)}
                        </span>
                      </div>
                    ) : null}
                  </td>
                  <td>{producto.imagenAlt}</td>
                  <td>{producto.marca}</td>
                  <td>{producto.referencia}</td>
                  <td>{producto.codigoBarras}</td>
                  <td>{producto.unidadMedida}</td>
                  <td>{producto.pesoKg}</td>
                  <td>{producto.largoCm}</td>
                  <td>{producto.anchoCm}</td>
                  <td>{producto.altoCm}</td>
                  <td>{producto.categoriaIva}</td>
                  <td>{producto.precioCompra}</td>
                  <td>{producto.precioVenta}</td>
                  <td>{producto.ganancia}</td>
                  <td>{producto.margen}</td>
                  <td>{producto.garantiaMeses}</td>
                  <td>{producto.destacado ? 'true' : 'false'}</td>
                  <td>{producto.activo ? 'true' : 'false'}</td>
                  <td>
                    {producto.subcategoria ? (
                      <Link to={`/subcategoria/${producto.subcategoria.id}`}>{producto.subcategoria.nombre}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/producto/${producto.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">Vista</span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/producto/${producto.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        variant="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
                      </Button>
                      <Button
                        onClick={() =>
                          (globalThis.location.href = `/producto/${producto.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
          !loading && <div className="alert alert-warning">Ningún Productos encontrado</div>
        )}
      </div>
      {totalItems ? (
        <div className={productoList && productoList.length > 0 ? '' : 'd-none'}>
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

export default Producto;
