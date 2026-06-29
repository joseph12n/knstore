import React from 'react';
import { Route, Routes } from 'react-router';

import StoreHome from 'app/landing/pages/StoreHome';
import CategoryPage from 'app/landing/pages/CategoryPage';
import ProductDetailPage from 'app/landing/pages/ProductDetailPage';
import SearchPage from 'app/landing/pages/SearchPage';
import CartPage from 'app/landing/pages/CartPage';
import CheckoutPage from 'app/landing/pages/CheckoutPage';

const StorefrontRoutes = () => (
  <Routes>
    <Route index element={<StoreHome />} />
    <Route path="categorias/:categoriaSlug/:subcategoriaSlug?" element={<CategoryPage />} />
    <Route path="productos/:slug" element={<ProductDetailPage />} />
    <Route path="buscar" element={<SearchPage />} />
    <Route path="carrito" element={<CartPage />} />
    <Route path="checkout" element={<CheckoutPage />} />
  </Routes>
);

export default StorefrontRoutes;
