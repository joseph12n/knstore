import React from 'react';
import { Route, Routes } from 'react-router';

import PrivateRoute from 'app/shared/auth/private-route';
import { Authority } from 'app/shared/jhipster/constants';

import StoreHome from 'app/storefront/pages/StoreHome';
import CategoryPage from 'app/storefront/pages/CategoryPage';
import ProductDetailPage from 'app/storefront/pages/ProductDetailPage';
import SearchPage from 'app/storefront/pages/SearchPage';
import CartPage from 'app/storefront/pages/CartPage';
import CheckoutPage from 'app/storefront/pages/CheckoutPage';
import AccountPage from 'app/storefront/pages/AccountPage';
import AddressesPage from 'app/storefront/pages/AddressesPage';
import OrdersPage from 'app/storefront/pages/OrdersPage';
import OrderDetailPage from 'app/storefront/pages/OrderDetailPage';
import { CartItem } from 'app/storefront/model/storefront.model';

interface StorefrontRoutesProps {
  cartItems: CartItem[];
  onAddToCart: (producto: any, cantidad?: number) => void;
  onUpdateCartQuantity: (itemId: string, quantity: number) => void;
  onRemoveCartItem: (itemId: string) => void;
  onClearCart: () => void;
}

const StorefrontRoutes = ({ cartItems, onAddToCart, onUpdateCartQuantity, onRemoveCartItem, onClearCart }: StorefrontRoutesProps) => (
  <Routes>
    <Route index element={<StoreHome onAddToCart={onAddToCart} />} />
    <Route path="categorias/:categoriaSlug/:subcategoriaSlug?" element={<CategoryPage onAddToCart={onAddToCart} />} />
    <Route path="productos/:slug" element={<ProductDetailPage onAddToCart={onAddToCart} />} />
    <Route path="buscar" element={<SearchPage onAddToCart={onAddToCart} />} />
    <Route
      path="carrito"
      element={
        <CartPage cartItems={cartItems} onUpdateQuantity={onUpdateCartQuantity} onRemoveItem={onRemoveCartItem} onClearCart={onClearCart} />
      }
    />
    <Route
      path="checkout"
      element={
        <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE, Authority.USER]}>
          <CheckoutPage cartItems={cartItems} onCheckoutComplete={onClearCart} />
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
