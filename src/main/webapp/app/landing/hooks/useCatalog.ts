import { useEffect, useMemo } from 'react';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCategorias } from 'app/entities/categoria/categoria.reducer';
import { getEntities as getSubcategorias } from 'app/entities/subcategoria/subcategoria.reducer';
import { getEntities as getMarcas } from 'app/entities/marca/marca.reducer';
import { getEntities as getProductos } from 'app/entities/producto/producto.reducer';
import { IProductoStorefront } from 'app/landing/model/storefront.model';
import { CATALOG_PAGE_SIZE } from 'app/landing/utils/constants';

export const useCatalog = (options?: { page?: number; size?: number; sort?: string; onlyActive?: boolean }) => {
  const dispatch = useAppDispatch();
  const { page = 0, size = CATALOG_PAGE_SIZE, sort = 'nombre,asc', onlyActive = true } = options || {};

  const categorias = useAppSelector(state => state.categoria.entities) ?? [];
  const subcategorias = useAppSelector(state => state.subcategoria.entities) ?? [];
  const marcas = useAppSelector(state => state.marca.entities) ?? [];
  const productos = useAppSelector(state => state.producto.entities) ?? [];
  const categoriaLoading = useAppSelector(state => state.categoria.loading);
  const subcategoriaLoading = useAppSelector(state => state.subcategoria.loading);
  const productoLoading = useAppSelector(state => state.producto.loading);
  const loading = categoriaLoading || subcategoriaLoading || productoLoading;
  const errorMessage = useAppSelector(state => state.producto.errorMessage);
  const totalItems = useAppSelector(state => state.producto.totalItems);

  useEffect(() => {
    dispatch(getCategorias({ page: 0, size: 100, sort: 'nombre,asc' }));
    dispatch(getSubcategorias({ page: 0, size: 200, sort: 'nombre,asc' }));
    dispatch(getMarcas({ page: 0, size: 100, sort: 'nombre,asc' }));
  }, [dispatch]);

  useEffect(() => {
    dispatch(getProductos({ page, size, sort }));
  }, [dispatch, page, size, sort]);

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
  };
};

export default useCatalog;
