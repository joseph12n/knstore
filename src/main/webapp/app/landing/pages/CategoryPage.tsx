import React, { useEffect, useMemo, useState } from 'react';
import { useParams } from 'react-router';
import { Breadcrumb, Button, Col, Collapse, Container, Form, Row } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faFilter } from '@fortawesome/free-solid-svg-icons';
import { Link } from 'react-router';
import axios from 'axios';

import ProductCard from 'app/landing/components/ProductCard';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import ErrorAlert from 'app/landing/components/ErrorAlert';
import EmptyState from 'app/landing/components/EmptyState';
import Pagination from 'app/landing/components/Pagination';
import { useCatalog } from 'app/landing/hooks/useCatalog';
import useCart from 'app/landing/hooks/useCart';
import { IProductoStorefront } from 'app/landing/model/storefront.model';
import { CATALOG_PAGE_SIZE } from 'app/landing/utils/constants';
import { getApiErrorMessage } from 'app/landing/utils/apiError';

const CATEGORY_PAGE_SIZE = CATALOG_PAGE_SIZE;

export const CategoryPage = () => {
  const { addItem: onAddToCart } = useCart();
  const { categoriaSlug, subcategoriaSlug } = useParams<{ categoriaSlug: string; subcategoriaSlug?: string }>();
  const {
    categorias: rawCategorias,
    subcategorias: rawSubcategorias,
    loading: catalogLoading,
    errorMessage: catalogErrorMessage,
    retry: retryCatalog,
  } = useCatalog({ page: 0, size: 100, sort: 'nombre,asc', loadOnMount: false });
  const categorias = rawCategorias ?? [];
  const subcategorias = rawSubcategorias ?? [];

  const [showFilters, setShowFilters] = useState(false);
  const [sortBy, setSortBy] = useState('nombre,asc');
  const [activePage, setActivePage] = useState(1);
  const [categoriaProductos, setCategoriaProductos] = useState<IProductoStorefront[]>([]);
  const [totalItems, setTotalItems] = useState(0);
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [retryKey, setRetryKey] = useState(0);

  const categoria = useMemo(() => categorias.find(c => c.slug === categoriaSlug), [categorias, categoriaSlug]);
  const subcategoria = useMemo(
    () => subcategorias.find(s => s.slug === subcategoriaSlug && s.categoria?.slug === categoriaSlug),
    [subcategorias, subcategoriaSlug, categoriaSlug],
  );

  const subcategoriasDeCategoria = useMemo(
    () => subcategorias.filter(s => s.categoria?.slug === categoriaSlug),
    [subcategorias, categoriaSlug],
  );

  useEffect(() => {
    setActivePage(1);
  }, [categoriaSlug, subcategoriaSlug]);

  const handleSortChange = (value: string) => {
    setSortBy(value);
    setActivePage(1);
  };

  useEffect(() => {
    if (!categoria?.id) {
      return;
    }
    const controller = new AbortController();

    const loadProductos = async () => {
      setLoading(true);
      setErrorMessage(null);
      try {
        const page = activePage - 1;
        // RF-072: orden y filtro por categoria/subcategoria se resuelven
        // server-side en GET /api/productos/search (es publico).
        const filter = subcategoria?.id
          ? `&subcategoriaId=${encodeURIComponent(subcategoria.id)}`
          : `&categoriaId=${encodeURIComponent(categoria.id)}`;
        const requestUrl = `api/productos/search?page=${page}&size=${CATEGORY_PAGE_SIZE}&sort=${encodeURIComponent(sortBy)}${filter}`;
        const response = await axios.get<IProductoStorefront[]>(requestUrl, { signal: controller.signal });
        setCategoriaProductos(response.data.map(p => ({ ...p, imagenes: p.imagenes ?? [] })));
        setTotalItems(parseInt(response.headers['x-total-count'] || `${response.data.length}`, 10));
      } catch (axiosError) {
        if (axios.isCancel(axiosError)) {
          return;
        }
        setErrorMessage(`No pudimos cargar los productos de esta categoría: ${getApiErrorMessage(axiosError)}`);
      } finally {
        if (!controller.signal.aborted) {
          setLoading(false);
        }
      }
    };

    void loadProductos();
    return () => controller.abort();
  }, [categoria?.id, subcategoria?.id, activePage, sortBy, retryKey]);

  const handleRetry = () => {
    if (errorMessage) {
      setRetryKey(key => key + 1);
    }
    if (catalogErrorMessage) {
      retryCatalog();
    }
  };

  if (catalogLoading && !categoria) {
    return <LoadingSpinner fullScreen />;
  }

  if (catalogErrorMessage && !categoria) {
    return (
      <Container className="py-5">
        <ErrorAlert message="No pudimos cargar la tienda. Inténtalo de nuevo." onRetry={retryCatalog} />
      </Container>
    );
  }

  if (!categoria) {
    return (
      <Container className="py-5">
        <EmptyState title="Categoría no encontrada" description="La categoría que buscas no existe o no está disponible." />
      </Container>
    );
  }

  if (errorMessage && categoriaProductos.length === 0) {
    return (
      <Container className="py-5">
        <ErrorAlert message={errorMessage} onRetry={handleRetry} />
      </Container>
    );
  }

  if (loading && categoriaProductos.length === 0) {
    return <LoadingSpinner fullScreen />;
  }

  return (
    <Container className="py-4 kn-fade-in">
      <Breadcrumb className="mb-3">
        <Breadcrumb.Item linkAs={Link as any} linkProps={{ to: '/' }}>
          Inicio
        </Breadcrumb.Item>
        <Breadcrumb.Item linkAs={Link as any} linkProps={{ to: `/categorias/${categoria.slug}` }}>
          {categoria.nombre}
        </Breadcrumb.Item>
        {subcategoria && <Breadcrumb.Item active>{subcategoria.nombre}</Breadcrumb.Item>}
      </Breadcrumb>

      <h1 className="h2 fw-bold mb-4">{subcategoria?.nombre || categoria.nombre}</h1>

      {subcategoriasDeCategoria.length > 0 && !subcategoriaSlug && (
        <div className="d-flex flex-wrap gap-2 mb-4">
          <span className="fw-semibold small me-2">Subcategorías:</span>
          {subcategoriasDeCategoria.map(sub => (
            <Link key={sub.id} to={`/categorias/${categoria.slug}/${sub.slug}`} className="btn btn-sm btn-outline-secondary rounded-pill">
              {sub.nombre}
            </Link>
          ))}
        </div>
      )}

      <Button
        variant="outline-secondary"
        className="d-lg-none mb-3 w-100"
        onClick={() => setShowFilters(!showFilters)}
        aria-controls="category-filters"
        aria-expanded={showFilters}
      >
        <FontAwesomeIcon icon={faFilter} className="me-2" />
        {showFilters ? 'Ocultar filtros' : 'Mostrar filtros'}
      </Button>

      <Row>
        <Col lg={3} className="mb-4">
          <Collapse in={showFilters} className="d-lg-block">
            <div id="category-filters">
              <div className="p-3 rounded border" style={{ backgroundColor: 'var(--kn-color-surface)' }}>
                <h5 className="fw-bold mb-3">Filtros</h5>
                <Form.Group className="mb-3">
                  <Form.Label className="small fw-semibold">Ordenar por</Form.Label>
                  <Form.Select aria-label="Ordenar por" value={sortBy} onChange={e => handleSortChange(e.target.value)}>
                    <option value="nombre,asc">Nombre A-Z</option>
                    <option value="nombre,desc">Nombre Z-A</option>
                    <option value="precioVenta,asc">Precio: menor a mayor</option>
                    <option value="precioVenta,desc">Precio: mayor a menor</option>
                  </Form.Select>
                </Form.Group>
                <p className="text-muted small mb-0">Más filtros próximamente.</p>
              </div>
            </div>
          </Collapse>
        </Col>
        <Col lg={9}>
          {loading ? (
            <LoadingSpinner />
          ) : errorMessage ? (
            <ErrorAlert message={errorMessage} onRetry={handleRetry} />
          ) : categoriaProductos.length === 0 ? (
            <EmptyState
              title="No hay productos en esta categoría"
              description="Prueba con otra categoría o vuelve más tarde."
              action={
                <Link to="/" className="btn btn-primary">
                  Volver al inicio
                </Link>
              }
            />
          ) : (
            <>
              <Row className="g-4">
                {categoriaProductos.map(producto => (
                  <Col key={producto.id} xs={6} md={4} lg={4} xl={3}>
                    <ProductCard producto={producto} onAddToCart={onAddToCart} />
                  </Col>
                ))}
              </Row>
              <Pagination activePage={activePage} itemsPerPage={CATEGORY_PAGE_SIZE} totalItems={totalItems} onPageChange={setActivePage} />
            </>
          )}
        </Col>
      </Row>
    </Container>
  );
};

export default CategoryPage;
