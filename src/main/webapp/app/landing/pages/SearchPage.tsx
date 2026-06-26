import React, { useMemo, useState } from 'react';
import { useSearchParams } from 'react-router';
import { Button, Col, Collapse, Container, Form, Row } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faFilter } from '@fortawesome/free-solid-svg-icons';

import ProductCard from 'app/landing/components/ProductCard';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import ErrorAlert from 'app/landing/components/ErrorAlert';
import EmptyState from 'app/landing/components/EmptyState';
import SearchBox from 'app/landing/components/SearchBox';
import { useCatalog } from 'app/landing/hooks/useCatalog';
import useCart from 'app/landing/hooks/useCart';

export const SearchPage = () => {
  const { addItem: onAddToCart } = useCart();
  const [searchParams] = useSearchParams();
  const query = searchParams.get('q') || '';
  const {
    categorias: rawCategorias,
    marcas: rawMarcas,
    productos: rawProductos,
    loading,
    errorMessage,
  } = useCatalog({ page: 0, size: 100, sort: 'nombre,asc' });
  const categorias = rawCategorias ?? [];
  const marcas = rawMarcas ?? [];
  const productos = rawProductos ?? [];

  const [selectedCategory, setSelectedCategory] = useState('');
  const [selectedBrand, setSelectedBrand] = useState('');
  const [sortBy, setSortBy] = useState('relevance');
  const [showFilters, setShowFilters] = useState(false);

  const resultados = useMemo(() => {
    let list = productos;

    if (query.trim()) {
      // TODO backend: reemplazar por endpoint de búsqueda full-text paginado (RF-024).
      const lowerQuery = query.toLowerCase();
      list = list.filter(
        p =>
          p.nombre?.toLowerCase().includes(lowerQuery) ||
          p.descripcion?.toLowerCase().includes(lowerQuery) ||
          p.marca?.nombre?.toLowerCase().includes(lowerQuery) ||
          p.sku?.toLowerCase().includes(lowerQuery),
      );
    }

    if (selectedCategory) {
      list = list.filter(p => p.categoria?.id === selectedCategory || p.subcategoria?.id === selectedCategory);
    }

    if (selectedBrand) {
      list = list.filter(p => p.marca?.id === selectedBrand);
    }

    if (sortBy === 'priceAsc') {
      list = [...list].sort((a, b) => (a.precio?.precioVenta || 0) - (b.precio?.precioVenta || 0));
    } else if (sortBy === 'priceDesc') {
      list = [...list].sort((a, b) => (b.precio?.precioVenta || 0) - (a.precio?.precioVenta || 0));
    } else if (sortBy === 'nameAsc') {
      list = [...list].sort((a, b) => (a.nombre || '').localeCompare(b.nombre || ''));
    }

    return list;
  }, [productos, query, selectedCategory, selectedBrand, sortBy]);

  return (
    <Container className="py-4 kn-fade-in">
      <h1 className="h2 fw-bold mb-4">Resultados de búsqueda</h1>

      <div className="mb-4" style={{ maxWidth: '600px' }}>
        <SearchBox initialValue={query} />
      </div>

      {query && (
        <p className="text-muted mb-4">
          {resultados.length} {resultados.length === 1 ? 'resultado' : 'resultados'} para "{query}"
        </p>
      )}

      <Button
        variant="outline-secondary"
        className="d-lg-none mb-3 w-100"
        onClick={() => setShowFilters(!showFilters)}
        aria-controls="search-filters"
        aria-expanded={showFilters}
      >
        <FontAwesomeIcon icon={faFilter} className="me-2" />
        {showFilters ? 'Ocultar filtros' : 'Mostrar filtros'}
      </Button>

      <Row>
        <Col lg={3} className="mb-4">
          <Collapse in={showFilters} className="d-lg-block">
            <div id="search-filters">
              <div className="p-3 rounded border" style={{ backgroundColor: 'var(--kn-color-surface)' }}>
                <h5 className="fw-bold mb-3">Filtros</h5>
                <Form.Group className="mb-3">
                  <Form.Label className="small fw-semibold">Categoría</Form.Label>
                  <Form.Select value={selectedCategory} onChange={e => setSelectedCategory(e.target.value)}>
                    <option value="">Todas</option>
                    {categorias.map(c => (
                      <option key={c.id} value={c.id}>
                        {c.nombre}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
                <Form.Group className="mb-3">
                  <Form.Label className="small fw-semibold">Marca</Form.Label>
                  <Form.Select value={selectedBrand} onChange={e => setSelectedBrand(e.target.value)}>
                    <option value="">Todas</option>
                    {marcas.map(m => (
                      <option key={m.id} value={m.id}>
                        {m.nombre}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
                <Form.Group>
                  <Form.Label className="small fw-semibold">Ordenar</Form.Label>
                  <Form.Select value={sortBy} onChange={e => setSortBy(e.target.value)}>
                    <option value="relevance">Relevancia</option>
                    <option value="priceAsc">Precio: menor a mayor</option>
                    <option value="priceDesc">Precio: mayor a menor</option>
                    <option value="nameAsc">Nombre A-Z</option>
                  </Form.Select>
                </Form.Group>
              </div>
            </div>
          </Collapse>
        </Col>
        <Col lg={9}>
          {loading ? (
            <LoadingSpinner />
          ) : errorMessage ? (
            <ErrorAlert message="No pudimos cargar los productos. Inténtalo de nuevo." />
          ) : resultados.length === 0 ? (
            <EmptyState title="No encontramos resultados" description="Intenta con otros términos o ajusta los filtros." />
          ) : (
            <Row className="g-4">
              {resultados.map(producto => (
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

export default SearchPage;
