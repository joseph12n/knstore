import React, { useMemo, useState } from 'react';
import { useSearchParams } from 'react-router';
import { Col, Container, Form, Row } from 'react-bootstrap';

import ProductCard from 'app/storefront/components/ProductCard';
import LoadingSpinner from 'app/storefront/components/LoadingSpinner';
import ErrorAlert from 'app/storefront/components/ErrorAlert';
import EmptyState from 'app/storefront/components/EmptyState';
import SearchBox from 'app/storefront/components/SearchBox';
import { useCatalog } from 'app/storefront/hooks/useCatalog';
import { IProductoStorefront } from 'app/storefront/model/storefront.model';

interface SearchPageProps {
  onAddToCart: (producto: IProductoStorefront, cantidad?: number) => void;
}

export const SearchPage = ({ onAddToCart }: SearchPageProps) => {
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

      <Row>
        <Col lg={3} className="mb-4">
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
