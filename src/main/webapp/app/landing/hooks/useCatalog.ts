import { useCallback, useEffect, useMemo } from 'react';
import { useStore } from 'react-redux';

import { useAppDispatch, useAppSelector, type IRootState } from 'app/config/store';
import { getEntities as getCategorias } from 'app/entities/categoria/categoria.reducer';
import { getEntities as getSubcategorias } from 'app/entities/subcategoria/subcategoria.reducer';
import { getEntities as getMarcas } from 'app/entities/marca/marca.reducer';
import { getEntities as getProductos } from 'app/entities/producto/producto.reducer';
import { IProductoStorefront } from 'app/landing/model/storefront.model';
import { CATALOG_PAGE_SIZE } from 'app/landing/utils/constants';

export const CATALOG_FETCH_SIZE = 100;

export interface CatalogOptions {
  page?: number;
  size?: number;
  sort?: string;
  onlyActive?: boolean;
  loadOnMount?: boolean;
}

export const useCatalog = (options?: CatalogOptions) => {
  const dispatch = useAppDispatch();
  const store = useStore<IRootState>();
  const { page = 0, size = CATALOG_PAGE_SIZE, sort = 'nombre,asc', onlyActive = true, loadOnMount = true } = options || {};

  const categorias = useAppSelector(state => state.categoria.entities) ?? [];
  const subcategorias = useAppSelector(state => state.subcategoria.entities) ?? [];
  const marcas = useAppSelector(state => state.marca.entities) ?? [];
  const productos = useAppSelector(state => state.producto.entities) ?? [];
  const categoriaLoading = useAppSelector(state => state.categoria.loading);
  const subcategoriaLoading = useAppSelector(state => state.subcategoria.loading);
  const productoLoading = useAppSelector(state => state.producto.loading);
  const loading = categoriaLoading || subcategoriaLoading || (productoLoading && productos.length === 0);
  const rawError = useAppSelector(
    state => state.categoria.errorMessage || state.subcategoria.errorMessage || state.marca.errorMessage || state.producto.errorMessage,
  );
  const errorMessage = productos.length === 0 && categorias.length === 0 ? rawError : null;
  const totalItems = useAppSelector(state => state.producto.totalItems);

  const retry = useCallback(() => {
    dispatch(getCategorias({ page: 0, size: CATALOG_FETCH_SIZE, sort: 'nombre,asc' }));
    dispatch(getSubcategorias({ page: 0, size: CATALOG_FETCH_SIZE * 2, sort: 'nombre,asc' }));
    dispatch(getMarcas({ page: 0, size: CATALOG_FETCH_SIZE, sort: 'nombre,asc' }));
    dispatch(getProductos({ page, size, sort }));
  }, [dispatch, page, size, sort]);

  useEffect(() => {
    if (!loadOnMount) {
      return;
    }
    const state = store.getState();
    if (state.categoria.entities.length === 0 && !state.categoria.loading) {
      dispatch(getCategorias({ page: 0, size: CATALOG_FETCH_SIZE, sort: 'nombre,asc' }));
    }
    if (state.subcategoria.entities.length === 0 && !state.subcategoria.loading) {
      dispatch(getSubcategorias({ page: 0, size: CATALOG_FETCH_SIZE * 2, sort: 'nombre,asc' }));
    }
    if (state.marca.entities.length === 0 && !state.marca.loading) {
      dispatch(getMarcas({ page: 0, size: CATALOG_FETCH_SIZE, sort: 'nombre,asc' }));
    }
    if (state.producto.entities.length === 0 && !state.producto.loading) {
      dispatch(getProductos({ page, size, sort }));
    }
  }, [dispatch, store, loadOnMount, page, size, sort]);

  const productosStorefront: IProductoStorefront[] = useMemo(() => {
    let list = productos.map(p => ({
      ...p,
      imagenes: p.imagenes ?? [],
    }));
    if (onlyActive) {
      list = list.filter(p => p.activo);
    }
    return list;
  }, [productos, onlyActive]);

  const categoriasActivas = useMemo(() => categorias.filter(c => c.activo), [categorias]);
  const subcategoriasActivas = useMemo(
    () => subcategorias.filter(s => s.activo && categoriasActivas.some(c => c.id === s.categoria?.id)),
    [subcategorias, categoriasActivas],
  );

  return {
    categorias: categoriasActivas,
    subcategorias: subcategoriasActivas,
    marcas,
    productos: productosStorefront,
    loading,
    errorMessage,
    totalItems,
    retry,
  };
};

export default useCatalog;
