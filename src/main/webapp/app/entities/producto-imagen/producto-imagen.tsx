import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { byteSize, getSortState, openFile } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './producto-imagen.reducer';

export const ProductoImagen = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const productoImagenList = useAppSelector(state => state.productoImagen.entities);
  const loading = useAppSelector(state => state.productoImagen.loading);

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
      <h2 id="producto-imagen-heading" data-cy="ProductoImagenHeading">
        Producto Imagens
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refrescar lista
          </Button>
          <Link to="/producto-imagen/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Crear nuevo Producto Imagen
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {productoImagenList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('imagen')}>
                  Imagen <FontAwesomeIcon icon={getSortIconByFieldName('imagen')} />
                </th>
                <th className="hand" onClick={sort('imagenAlt')}>
                  Imagen Alt <FontAwesomeIcon icon={getSortIconByFieldName('imagenAlt')} />
                </th>
                <th className="hand" onClick={sort('esPrincipal')}>
                  Es Principal <FontAwesomeIcon icon={getSortIconByFieldName('esPrincipal')} />
                </th>
                <th>
                  Producto <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {productoImagenList.map(productoImagen => (
                <tr key={`entity-${productoImagen.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/producto-imagen/${productoImagen.id}`} variant="link" size="sm">
                      {productoImagen.id}
                    </Button>
                  </td>
                  <td>
                    {productoImagen.imagen ? (
                      <div>
                        {productoImagen.imagenContentType ? (
                          <a onClick={openFile(productoImagen.imagenContentType, productoImagen.imagen)}>
                            <img
                              src={`data:${productoImagen.imagenContentType};base64,${productoImagen.imagen}`}
                              style={{ maxHeight: '30px' }}
                            />
                            &nbsp;
                          </a>
                        ) : null}
                        <span>
                          {productoImagen.imagenContentType}, {byteSize(productoImagen.imagen)}
                        </span>
                      </div>
                    ) : null}
                  </td>
                  <td>{productoImagen.imagenAlt}</td>
                  <td>{productoImagen.esPrincipal ? 'true' : 'false'}</td>
                  <td>
                    {productoImagen.producto ? (
                      <Link to={`/producto/${productoImagen.producto.id}`}>{productoImagen.producto.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/producto-imagen/${productoImagen.id}`}
                        variant="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">Vista</span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/producto-imagen/${productoImagen.id}/edit`}
                        variant="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
                      </Button>
                      <Button
                        onClick={() => (globalThis.location.href = `/producto-imagen/${productoImagen.id}/delete`)}
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
          !loading && <div className="alert alert-warning">Ningún Producto Imagens encontrado</div>
        )}
      </div>
    </div>
  );
};

export default ProductoImagen;
