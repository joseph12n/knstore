import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, TextFormat, byteSize, getPaginationState, openFile } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './cuenta.reducer';

export const Cuenta = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const cuentaList = useAppSelector(state => state.cuenta.entities);
  const loading = useAppSelector(state => state.cuenta.loading);
  const totalItems = useAppSelector(state => state.cuenta.totalItems);

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
      <h2 id="cuenta-heading" data-cy="CuentaHeading">
        Cuentas
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refrescar lista
          </Button>
          <Link to="/cuenta/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Crear nuevo Cuenta
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {cuentaList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('numDocumento')}>
                  Num Documento <FontAwesomeIcon icon={getSortIconByFieldName('numDocumento')} />
                </th>
                <th className="hand" onClick={sort('tipoPersona')}>
                  Tipo Persona <FontAwesomeIcon icon={getSortIconByFieldName('tipoPersona')} />
                </th>
                <th className="hand" onClick={sort('primerNombre')}>
                  Primer Nombre <FontAwesomeIcon icon={getSortIconByFieldName('primerNombre')} />
                </th>
                <th className="hand" onClick={sort('segundoNombre')}>
                  Segundo Nombre <FontAwesomeIcon icon={getSortIconByFieldName('segundoNombre')} />
                </th>
                <th className="hand" onClick={sort('primerApellido')}>
                  Primer Apellido <FontAwesomeIcon icon={getSortIconByFieldName('primerApellido')} />
                </th>
                <th className="hand" onClick={sort('segundoApellido')}>
                  Segundo Apellido <FontAwesomeIcon icon={getSortIconByFieldName('segundoApellido')} />
                </th>
                <th className="hand" onClick={sort('celular')}>
                  Celular <FontAwesomeIcon icon={getSortIconByFieldName('celular')} />
                </th>
                <th className="hand" onClick={sort('telefono')}>
                  Telefono <FontAwesomeIcon icon={getSortIconByFieldName('telefono')} />
                </th>
                <th className="hand" onClick={sort('fechaNacimiento')}>
                  Fecha Nacimiento <FontAwesomeIcon icon={getSortIconByFieldName('fechaNacimiento')} />
                </th>
                <th className="hand" onClick={sort('genero')}>
                  Genero <FontAwesomeIcon icon={getSortIconByFieldName('genero')} />
                </th>
                <th className="hand" onClick={sort('fotoPerfil')}>
                  Foto Perfil <FontAwesomeIcon icon={getSortIconByFieldName('fotoPerfil')} />
                </th>
                <th className="hand" onClick={sort('activo')}>
                  Activo <FontAwesomeIcon icon={getSortIconByFieldName('activo')} />
                </th>
                <th>
                  User <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Tipo Documento <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {cuentaList.map(cuenta => (
                <tr key={`entity-${cuenta.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/cuenta/${cuenta.id}`} variant="link" size="sm">
                      {cuenta.id}
                    </Button>
                  </td>
                  <td>{cuenta.numDocumento}</td>
                  <td>{cuenta.tipoPersona}</td>
                  <td>{cuenta.primerNombre}</td>
                  <td>{cuenta.segundoNombre}</td>
                  <td>{cuenta.primerApellido}</td>
                  <td>{cuenta.segundoApellido}</td>
                  <td>{cuenta.celular}</td>
                  <td>{cuenta.telefono}</td>
                  <td>
                    {cuenta.fechaNacimiento ? (
                      <TextFormat type="date" value={cuenta.fechaNacimiento} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{cuenta.genero}</td>
                  <td>
                    {cuenta.fotoPerfil ? (
                      <div>
                        {cuenta.fotoPerfilContentType ? (
                          <a onClick={openFile(cuenta.fotoPerfilContentType, cuenta.fotoPerfil)}>
                            <img src={`data:${cuenta.fotoPerfilContentType};base64,${cuenta.fotoPerfil}`} style={{ maxHeight: '30px' }} />
                            &nbsp;
                          </a>
                        ) : null}
                        <span>
                          {cuenta.fotoPerfilContentType}, {byteSize(cuenta.fotoPerfil)}
                        </span>
                      </div>
                    ) : null}
                  </td>
                  <td>{cuenta.activo ? 'true' : 'false'}</td>
                  <td>{cuenta.user ? cuenta.user.login : ''}</td>
                  <td>
                    {cuenta.tipoDocumento ? (
                      <Link to={`/tipo-documento/${cuenta.tipoDocumento.id}`}>{cuenta.tipoDocumento.sigla}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/cuenta/${cuenta.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">Vista</span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/cuenta/${cuenta.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        variant="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
                      </Button>
                      <Button
                        onClick={() =>
                          (globalThis.location.href = `/cuenta/${cuenta.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
          !loading && <div className="alert alert-warning">Ningún Cuentas encontrado</div>
        )}
      </div>
      {totalItems ? (
        <div className={cuentaList && cuentaList.length > 0 ? '' : 'd-none'}>
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

export default Cuenta;
