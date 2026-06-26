import React, { useEffect, useMemo, useState } from 'react';
import { useParams } from 'react-router';
import { Badge, Breadcrumb, Button, Col, Container, Row, Tab, Tabs } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router';
import { toast } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getProductos } from 'app/entities/producto/producto.reducer';
import { IProductoStorefront } from 'app/landing/model/storefront.model';
import { buildImageUrl, formatCOP } from 'app/landing/utils/format';
import LoadingSpinner from 'app/landing/components/LoadingSpinner';
import ErrorAlert from 'app/landing/components/ErrorAlert';
import EmptyState from 'app/landing/components/EmptyState';
import QuantitySelector from 'app/landing/components/QuantitySelector';
import useCart from 'app/landing/hooks/useCart';

export const ProductDetailPage = () => {
  const { addItem: onAddToCart } = useCart();
  const { slug } = useParams<{ slug: string }>();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const [quantity, setQuantity] = useState(1);
  const [selectedImage, setSelectedImage] = useState(0);

  const productos = useAppSelector(state => state.producto.entities) ?? [];
  const loading = useAppSelector(state => state.producto.loading);
  const errorMessage = useAppSelector(state => state.producto.errorMessage);

  useEffect(() => {
    if (productos.length === 0) {
      dispatch(getProductos({ page: 0, size: 100, sort: 'nombre,asc' }));
    }
  }, [dispatch, productos.length]);

  const producto = useMemo(() => productos.find(p => p.slug === slug), [productos, slug]) as IProductoStorefront | undefined;

  const productoImagenes = useMemo(() => producto?.imagenes ?? [], [producto]);

  useEffect(() => {
    if (producto) {
      setSelectedImage(0);
    }
  }, [producto]);

  if (loading && productos.length === 0) {
    return <LoadingSpinner fullScreen />;
  }

  if (errorMessage) {
    return (
      <Container className="py-5">
        <ErrorAlert message="No pudimos cargar el producto. Inténtalo de nuevo." />
      </Container>
    );
  }

  if (!producto) {
    return (
      <Container className="py-5">
        <EmptyState
          title="Producto no encontrado"
          description="El producto que buscas no existe o no está disponible."
          action={
            <Link to="/" className="btn btn-primary">
              Volver al inicio
            </Link>
          }
        />
      </Container>
    );
  }

  const stock = producto.inventario?.stock || 0;
  const precioVenta = producto.precio?.precioVenta || 0;
  const hasStock = stock > 0;

  const handleAddToCart = () => {
    if (!hasStock) {
      toast.error('Producto sin stock disponible.');
      return;
    }
    if (quantity > stock) {
      toast.error(`Solo hay ${stock} unidades disponibles.`);
      return;
    }
    onAddToCart(producto, quantity);
    toast.success('Producto añadido al carrito');
  };

  const handleBuyNow = () => {
    handleAddToCart();
    navigate('/carrito');
  };

  return (
    <Container className="py-4 kn-fade-in">
      <Breadcrumb className="mb-4 flex-wrap">
        <Breadcrumb.Item linkAs={Link as any} linkProps={{ to: '/' }}>
          Inicio
        </Breadcrumb.Item>
        {producto.categoria && (
          <Breadcrumb.Item linkAs={Link as any} linkProps={{ to: `/categorias/${producto.categoria.slug}` }}>
            {producto.categoria.nombre}
          </Breadcrumb.Item>
        )}
        <Breadcrumb.Item active className="text-truncate" style={{ maxWidth: '200px' }}>
          {producto.nombre}
        </Breadcrumb.Item>
      </Breadcrumb>

      <Row className="g-5">
        {/* Galería */}
        <Col lg={7}>
          <Row className="g-3">
            <Col xs={12}>
              <div className="rounded overflow-hidden position-relative" style={{ aspectRatio: '1/1', backgroundColor: '#f8f9fa' }}>
                {productoImagenes.length > 0 ? (
                  <img
                    src={buildImageUrl(productoImagenes[selectedImage]?.imagenContentType, productoImagenes[selectedImage]?.imagen)}
                    alt={producto.nombre}
                    className="w-100 h-100 object-fit-cover"
                  />
                ) : (
                  <img src="/content/images/product-placeholder.svg" alt={producto.nombre} className="w-100 h-100 object-fit-cover" />
                )}
                {producto.destacado && (
                  <Badge bg="dark" className="position-absolute top-0 start-0 m-3 text-uppercase">
                    Destacado
                  </Badge>
                )}
              </div>
            </Col>
            {productoImagenes.length > 1 && (
              <Col xs={12}>
                <div className="product-gallery-thumbs d-flex gap-2 overflow-auto pb-2">
                  {productoImagenes.map((img, idx) => (
                    <button
                      key={img.id}
                      type="button"
                      className={`btn p-0 border flex-shrink-0 ${selectedImage === idx ? 'border-primary border-2' : ''}`}
                      onClick={() => setSelectedImage(idx)}
                      aria-label={`Ver imagen ${idx + 1}`}
                      style={{ width: '72px', height: '72px' }}
                    >
                      <img
                        src={buildImageUrl(img.imagenContentType, img.imagen)}
                        alt={`${producto.nombre} ${idx + 1}`}
                        className="w-100 h-100 object-fit-cover"
                      />
                    </button>
                  ))}
                </div>
              </Col>
            )}
          </Row>
        </Col>

        {/* Info */}
        <Col lg={5}>
          <div className="text-muted small text-uppercase mb-2">{producto.marca?.nombre || 'Knstore'}</div>
          <h1 className="h2 fw-bold mb-3">{producto.nombre}</h1>
          <div className="h3 fw-bold mb-3">{formatCOP(precioVenta)}</div>
          <p className="text-muted mb-4">{producto.descripcion || 'Producto de alta calidad disponible en Knstore.'}</p>

          <div className="d-flex flex-wrap gap-2 mb-4">
            <Badge bg={hasStock ? 'success' : 'danger'}>{hasStock ? `En stock (${stock})` : 'Sin stock'}</Badge>
            {producto.sku && <Badge bg="secondary">SKU: {producto.sku}</Badge>}
            {producto.color && (
              <Badge bg="light" text="dark">
                Color: {producto.color}
              </Badge>
            )}
            {producto.talla && (
              <Badge bg="light" text="dark">
                Talla: {producto.talla}
              </Badge>
            )}
          </div>

          <div className="mb-4">
            <label className="form-label fw-semibold">Cantidad</label>
            <QuantitySelector value={quantity} min={1} max={Math.max(1, stock)} onChange={setQuantity} />
          </div>

          <div className="d-grid gap-2 mb-4">
            <Button variant="primary" size="lg" onClick={handleAddToCart} disabled={!hasStock} className="w-100">
              Añadir al carrito
            </Button>
            <Button variant="outline-primary" size="lg" onClick={handleBuyNow} disabled={!hasStock} className="w-100">
              Comprar ahora
            </Button>
          </div>

          <Tabs defaultActiveKey="detalles" className="mb-3">
            <Tab eventKey="detalles" title="Detalles">
              <ul className="list-unstyled text-muted small">
                <li className="mb-1">
                  <strong>Referencia:</strong> {producto.referencia || 'N/A'}
                </li>
                <li className="mb-1">
                  <strong>Código de barras:</strong> {producto.codigoBarras || 'N/A'}
                </li>
                <li className="mb-1">
                  <strong>Unidad de medida:</strong> {producto.unidadMedida || 'N/A'}
                </li>
                <li className="mb-1">
                  <strong>Categoría de IVA:</strong>{' '}
                  {producto.categoriaIva?.porcentaje !== undefined ? `${producto.categoriaIva.porcentaje}%` : 'N/A'}
                </li>
              </ul>
            </Tab>
            <Tab eventKey="envio" title="Envío">
              <p className="text-muted small">
                Envío gratis en compras superiores a $150.000. Entrega estimada de 3 a 5 días hábiles en ciudades principales.
              </p>
            </Tab>
          </Tabs>
        </Col>
      </Row>
    </Container>
  );
};

export default ProductDetailPage;
