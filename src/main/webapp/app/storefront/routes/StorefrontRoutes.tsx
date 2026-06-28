import React from 'react';
import { Route, Routes } from 'react-router';

import PrivateRoute from 'app/shared/auth/private-route';
import { Authority } from 'app/shared/jhipster/constants';

import StoreHome from 'app/landing/pages/StoreHome';
import CategoryPage from 'app/landing/pages/CategoryPage';
import ProductDetailPage from 'app/landing/pages/ProductDetailPage';
import SearchPage from 'app/landing/pages/SearchPage';
import CartPage from 'app/landing/pages/CartPage';
import CheckoutPage from 'app/landing/pages/CheckoutPage';
import AccountPage from 'app/landing/pages/AccountPage';
import AddressesPage from 'app/landing/pages/AddressesPage';
import OrdersPage from 'app/landing/pages/OrdersPage';
import OrderDetailPage from 'app/landing/pages/OrderDetailPage';

const StorefrontRoutes = () => (
  <Routes>
    <Route index element={<StoreHome />} />
    <Route path="categorias/:categoriaSlug/:subcategoriaSlug?" element={<CategoryPage />} />
    <Route path="productos/:slug" element={<ProductDetailPage />} />
    <Route path="buscar" element={<SearchPage />} />
    <Route path="carrito" element={<CartPage />} />
    <Route
      path="checkout"
      element={
        <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE, Authority.USER]}>
          <CheckoutPage />
        </PrivateRoute>
      }
    />
    <Route
      path="cuenta"
      element={
        <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE, Authority.USER]}>
          <AccountPage />
        </PrivateRoute>
      }
    />
    <Route
      index
      element={
        <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE, Authority.USER]}>
          <AccountPage />
        </PrivateRoute>
      }
    />
    <Route
      path="cuenta/direcciones"
      element={
        <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE]}>
          <AddressesPage />
        </PrivateRoute>
      }
    />
    <Route
      path="direcciones"
      element={
        <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE]}>
          <AddressesPage />
        </PrivateRoute>
      }
    />
    <Route
      path="cuenta/pedidos"
      element={
        <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE, Authority.USER]}>
          <OrdersPage />
        </PrivateRoute>
      }
    />
    <Route
      path="pedidos"
      element={
        <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE, Authority.USER]}>
          <OrdersPage />
        </PrivateRoute>
      }
    />
    <Route
      path="cuenta/pedidos/:id"
      element={
        <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE, Authority.USER]}>
          <OrderDetailPage />
        </PrivateRoute>
      }
    />
    <Route
      path="pedidos/:id"
      element={
        <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE, Authority.USER]}>
          <OrderDetailPage />
        </PrivateRoute>
      }
    />
  </Routes>
);

export default StorefrontRoutes;
