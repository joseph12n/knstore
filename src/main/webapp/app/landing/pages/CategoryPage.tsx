import React, { useMemo, useState } from 'react';
import { useParams } from 'react-router';
import { Breadcrumb, Button, Col, Collapse, Container, Form, Row } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faFilter } from '@fortawesome/free-solid-svg-icons';
import { Link } from 'react-router';

import ProductCard from 'app/landing/components/ProductCard';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import ErrorAlert from 'app/landing/components/ErrorAlert';
import EmptyState from 'app/landing/components/EmptyState';
import { useCatalog } from 'app/landing/hooks/useCatalog';
import useCart from 'app/landing/hooks/useCart';

export const CategoryPage = () => {
  const { addItem: onAddToCart } = useCart();
  const { categoriaSlug, subcategoriaSlug } = useParams<{ categoriaSlug: string; subcategoriaSlug?: string }>();
  const {
    categorias: rawCategorias,
    subcategorias: rawSubcategorias,
    productos: rawProductos,
    loading,
    errorMessage,
  } = useCatalog({ page: 0, size: 48, sort: 'nombre,asc' });
  const categorias = rawCategorias ?? [];
  const subcategorias = rawSubcategorias ?? [];
  const productos = rawProductos ?? [];

  const [showFilters, setShowFilters] = useState(false);

  const categoria = useMemo(() => categorias.find(c => c.slug === categoriaSlug), [categorias, categoriaSlug]);
  const subcategoria = useMemo(
    () => subcategorias.find(s => s.slug === subcategoriaSlug && s.categoria?.slug === categoriaSlug),
    [subcategorias, subcategoriaSlug, categoriaSlug],
  );

  const productosFiltrados = useMemo(() => {
    if (!categoria) return [];
    return productos.filter(p => {
      const matchCategoria = p.categoria?.slug === categoriaSlug;
      const matchSubcategoria = subcategoriaSlug ? p.subcategoria?.slug === subcategoriaSlug : true;
      return matchCategoria && matchSubcategoria;
    });
  }, [productos, categoriaSlug, subcategoriaSlug, categoria]);

  const subcategoriasDeCategoria = useMemo(
    () => subcategorias.filter(s => s.categoria?.slug === categoriaSlug),
    [subcategorias, categoriaSlug],
  );

  if (loading && productos.length === 0) {
    return <LoadingSpinner fullScreen />;
  }

  if (!categoria) {
    return (
      <Container className="py-5">
        <EmptyState title="Categoría no encontrada" description="La categoría que buscas no existe o no está disponible." />
      </Container>
    );
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

      {errorMessage && <ErrorAlert message="No pudimos cargar los productos. Inténtalo de nuevo." />}

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
                  <Form.Select aria-label="Ordenar por">
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
          {productosFiltrados.length === 0 ? (
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
            <Row className="g-4">
              {productosFiltrados.map(producto => (
                <Col key={producto.id} xs={6} md={4} lg={4} xl={3}>
                  <ProductCard producto={producto} onAddToCart={onAddToCart} />
                </Col>
              ))}
            </Row>
          )}
        </Col>
      </Row>
    </Container>
  );
};

export default CategoryPage;
