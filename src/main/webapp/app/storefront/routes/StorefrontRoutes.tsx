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

const CLIENT_AUTHORITIES = [Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE];

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
        <PrivateRoute hasAnyAuthorities={CLIENT_AUTHORITIES}>
          <CheckoutPage cartItems={cartItems} onCheckoutComplete={onClearCart} />
        </PrivateRoute>
      }
    />
    <Route
      path="cuenta"
      element={
        <PrivateRoute hasAnyAuthorities={CLIENT_AUTHORITIES}>
          <AccountPage />
        </PrivateRoute>
      }
    />
    <Route
      path="cuenta/direcciones"
      element={
        <PrivateRoute hasAnyAuthorities={CLIENT_AUTHORITIES}>
          <AddressesPage />
        </PrivateRoute>
      }
    />
    <Route
      path="cuenta/pedidos"
      element={
        <PrivateRoute hasAnyAuthorities={CLIENT_AUTHORITIES}>
          <OrdersPage />
        </PrivateRoute>
      }
    />
    <Route
      path="cuenta/pedidos/:id"
      element={
        <PrivateRoute hasAnyAuthorities={CLIENT_AUTHORITIES}>
          <OrderDetailPage />
        </PrivateRoute>
      }
    />
  </Routes>
);

export default StorefrontRoutes;
