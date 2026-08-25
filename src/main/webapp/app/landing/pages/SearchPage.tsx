import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router';
import { Button, Col, Collapse, Container, Form, Row } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faFilter } from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';

import ProductCard from 'app/landing/components/ProductCard';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import ErrorAlert from 'app/landing/components/ErrorAlert';
import EmptyState from 'app/landing/components/EmptyState';
import SearchBox from 'app/landing/components/SearchBox';
import Pagination from 'app/landing/components/Pagination';
import { useCatalog } from 'app/landing/hooks/useCatalog';
import useDebounce from 'app/landing/hooks/useDebounce';
import useCart from 'app/landing/hooks/useCart';
import { IProductoStorefront } from 'app/landing/model/storefront.model';
import { CATALOG_PAGE_SIZE } from 'app/landing/utils/constants';
import { getApiErrorMessage } from 'app/landing/utils/apiError';

const SEARCH_PAGE_SIZE = CATALOG_PAGE_SIZE;

// RF-072: la ordenacion es server-side; cada opcion del dropdown mapea al
// parametro sort que soporta GET /api/productos/search.
const SEARCH_SORTS: Record<string, string> = {
  relevance: 'nombre,asc',
  priceAsc: 'precioVenta,asc',
  priceDesc: 'precioVenta,desc',
  nameAsc: 'nombre,asc',
};

export const SearchPage = () => {
  const { addItem: onAddToCart } = useCart();
  const [searchParams] = useSearchParams();
  const query = searchParams.get('q') || '';
  const debouncedQuery = useDebounce(query, 300);

  const [activePage, setActivePage] = useState(1);
  const [searchResults, setSearchResults] = useState<IProductoStorefront[]>([]);
  const [totalItems, setTotalItems] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [retryKey, setRetryKey] = useState(0);

  const [selectedCategory, setSelectedCategory] = useState('');
  const [selectedBrand, setSelectedBrand] = useState('');
  const [sortBy, setSortBy] = useState('relevance');
  const [showFilters, setShowFilters] = useState(false);

  const handleCategoryChange = (value: string) => {
    setSelectedCategory(value);
    setActivePage(1);
  };

  const handleBrandChange = (value: string) => {
    setSelectedBrand(value);
    setActivePage(1);
  };

  const handleSortChange = (value: string) => {
    setSortBy(value);
    setActivePage(1);
  };

  const {
    categorias: rawCategorias,
    marcas: rawMarcas,
    loading: catalogLoading,
    errorMessage: catalogErrorMessage,
    retry: retryCatalog,
  } = useCatalog({ page: 0, size: 100, sort: 'nombre,asc', loadOnMount: false });
  const categorias = rawCategorias ?? [];
  const marcas = rawMarcas ?? [];

  useEffect(() => {
    setActivePage(1);
  }, [debouncedQuery]);

  useEffect(() => {
    const controller = new AbortController();
    const loadSearchResults = async () => {
      setLoading(true);
      setError(null);
      try {
        const page = activePage - 1;
        const filters = [
          selectedCategory ? `&categoriaId=${encodeURIComponent(selectedCategory)}` : '',
          selectedBrand ? `&marcaId=${encodeURIComponent(selectedBrand)}` : '',
        ].join('');
        const requestUrl = `api/productos/search?q=${encodeURIComponent(debouncedQuery.trim())}&page=${page}&size=${SEARCH_PAGE_SIZE}&sort=${SEARCH_SORTS[sortBy]}${filters}`;
        const response = await axios.get<IProductoStorefront[]>(requestUrl, { signal: controller.signal });
        setSearchResults(response.data.map(p => ({ ...p, imagenes: p.imagenes ?? [] })));
        setTotalItems(parseInt(response.headers['x-total-count'] || `${response.data.length}`, 10));
      } catch (axiosError) {
        if (axios.isCancel(axiosError)) {
          return;
        }
        setError(`No pudimos realizar la búsqueda: ${getApiErrorMessage(axiosError)}`);
      } finally {
        if (!controller.signal.aborted) {
          setLoading(false);
        }
      }
    };

    loadSearchResults();
    return () => controller.abort();
  }, [debouncedQuery, activePage, retryKey, selectedCategory, selectedBrand, sortBy]);

  const resultados = searchResults;

  const isLoading = loading || catalogLoading;
  const hasError = (error || catalogErrorMessage) && searchResults.length === 0;

  const handleRetry = () => {
    if (error) {
      setActivePage(1);
      setRetryKey(key => key + 1);
    }
    if (catalogErrorMessage) {
      retryCatalog();
    }
  };

  return (
    <Container className="py-4 kn-fade-in">
      <h1 className="h2 fw-bold mb-4">Resultados de búsqueda</h1>

      <div className="mb-4" style={{ maxWidth: '600px' }}>
        <SearchBox initialValue={query} />
      </div>

      {query && (
        <p className="text-muted mb-4">
          {totalItems} {totalItems === 1 ? 'resultado' : 'resultados'} para "{query}"
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
                  <Form.Select value={selectedCategory} onChange={e => handleCategoryChange(e.target.value)}>
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
                  <Form.Select value={selectedBrand} onChange={e => handleBrandChange(e.target.value)}>
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
                  <Form.Select value={sortBy} onChange={e => handleSortChange(e.target.value)}>
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
          {isLoading ? (
            <LoadingSpinner />
          ) : hasError ? (
            <ErrorAlert
              message={error || catalogErrorMessage || 'No pudimos cargar los productos. Inténtalo de nuevo.'}
              onRetry={handleRetry}
            />
          ) : resultados.length === 0 ? (
            <EmptyState title="No encontramos resultados" description="Intenta con otros términos o ajusta los filtros." />
          ) : (
            <>
              <Row className="g-4">
                {resultados.map(producto => (
                  <Col key={producto.id} xs={6} md={4} lg={4} xl={3}>
                    <ProductCard producto={producto} onAddToCart={onAddToCart} />
                  </Col>
                ))}
              </Row>
              <Pagination activePage={activePage} itemsPerPage={SEARCH_PAGE_SIZE} totalItems={totalItems} onPageChange={setActivePage} />
            </>
          )}
        </Col>
      </Row>
    </Container>
  );
};

export default SearchPage;
