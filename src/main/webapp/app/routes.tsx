import React, { Suspense, useEffect, useMemo } from 'react';
import { Route } from 'react-router';

import EntitiesRoutes from 'app/entities/routes';
import Activate from 'app/modules/account/activate/activate';
import PasswordResetFinish from 'app/modules/account/password-reset/finish/password-reset-finish';
import PasswordResetInit from 'app/modules/account/password-reset/init/password-reset-init';
import Register from 'app/modules/account/register/register';
import Login from 'app/modules/login/login';
import Logout from 'app/modules/login/logout';
import PrivateRoute from 'app/shared/auth/private-route';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import PageNotFound from 'app/shared/error/page-not-found';
import { Authority } from 'app/shared/jhipster/constants';
import StorefrontLayout from 'app/storefront/components/StorefrontLayout';
import StorefrontRoutes from 'app/storefront/routes/StorefrontRoutes';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCategorias } from 'app/entities/categoria/categoria.reducer';
import { getEntities as getSubcategorias } from 'app/entities/subcategoria/subcategoria.reducer';
import { IProductoStorefront } from 'app/storefront/model/storefront.model';
import useCart from 'app/storefront/hooks/useCart';

const loading = <div>loading ...</div>;

const Account = React.lazy(() => import(/* webpackChunkName: "account" */ 'app/modules/account'));
const Admin = React.lazy(() => import(/* webpackChunkName: "administration" */ 'app/modules/administration'));

const ADMIN_AUTHORITIES = [Authority.ADMIN, Authority.MANAGER];
const STORE_AUTHORITIES = [Authority.ADMIN, Authority.MANAGER, Authority.CLIENTE, Authority.USER];

const AppRoutes = () => {
  const dispatch = useAppDispatch();
  const { items: cartItems, addItem, updateQuantity, removeItem, clearCart } = useCart();

  useEffect(() => {
    dispatch(getCategorias({ page: 0, size: 100, sort: 'nombre,asc' }));
    dispatch(getSubcategorias({ page: 0, size: 200, sort: 'nombre,asc' }));
  }, [dispatch]);

  const categorias = (useAppSelector(state => state.categoria.entities) ?? []).filter(c => c.activo);
  const subcategorias = (useAppSelector(state => state.subcategoria.entities) ?? []).filter(s => s.activo);

  const handleAddToCart = (producto: IProductoStorefront, cantidad = 1) => {
    addItem(producto, cantidad);
  };

  const activeSubcategorias = useMemo(
    () => subcategorias.filter(s => categorias.some(c => c.id === s.categoria?.id)),
    [subcategorias, categorias],
  );

  return (
    <div className="view-routes">
      <Suspense fallback={loading}>
        <ErrorBoundaryRoutes>
          {/* Storefront de cliente */}
          <Route
            index
            element={
              <StorefrontLayout
                categorias={categorias}
                subcategorias={activeSubcategorias}
                cartItems={cartItems}
                onUpdateCartQuantity={updateQuantity}
                onRemoveCartItem={removeItem}
                onClearCart={clearCart}
              >
                <StorefrontRoutes
                  cartItems={cartItems}
                  onAddToCart={handleAddToCart}
                  onUpdateCartQuantity={updateQuantity}
                  onRemoveCartItem={removeItem}
                  onClearCart={clearCart}
                />
              </StorefrontLayout>
            }
          />
          <Route
            path="categorias/:categoriaSlug/:subcategoriaSlug?"
            element={
              <StorefrontLayout
                categorias={categorias}
                subcategorias={activeSubcategorias}
                cartItems={cartItems}
                onUpdateCartQuantity={updateQuantity}
                onRemoveCartItem={removeItem}
                onClearCart={clearCart}
              >
                <StorefrontRoutes
                  cartItems={cartItems}
                  onAddToCart={handleAddToCart}
                  onUpdateCartQuantity={updateQuantity}
                  onRemoveCartItem={removeItem}
                  onClearCart={clearCart}
                />
              </StorefrontLayout>
            }
          />
          <Route
            path="productos/:slug"
            element={
              <StorefrontLayout
                categorias={categorias}
                subcategorias={activeSubcategorias}
                cartItems={cartItems}
                onUpdateCartQuantity={updateQuantity}
                onRemoveCartItem={removeItem}
                onClearCart={clearCart}
              >
                <StorefrontRoutes
                  cartItems={cartItems}
                  onAddToCart={handleAddToCart}
                  onUpdateCartQuantity={updateQuantity}
                  onRemoveCartItem={removeItem}
                  onClearCart={clearCart}
                />
              </StorefrontLayout>
            }
          />
          <Route
            path="buscar"
            element={
              <StorefrontLayout
                categorias={categorias}
                subcategorias={activeSubcategorias}
                cartItems={cartItems}
                onUpdateCartQuantity={updateQuantity}
                onRemoveCartItem={removeItem}
                onClearCart={clearCart}
              >
                <StorefrontRoutes
                  cartItems={cartItems}
                  onAddToCart={handleAddToCart}
                  onUpdateCartQuantity={updateQuantity}
                  onRemoveCartItem={removeItem}
                  onClearCart={clearCart}
                />
              </StorefrontLayout>
            }
          />
          <Route
            path="carrito"
            element={
              <StorefrontLayout
                categorias={categorias}
                subcategorias={activeSubcategorias}
                cartItems={cartItems}
                onUpdateCartQuantity={updateQuantity}
                onRemoveCartItem={removeItem}
                onClearCart={clearCart}
              >
                <StorefrontRoutes
                  cartItems={cartItems}
                  onAddToCart={handleAddToCart}
                  onUpdateCartQuantity={updateQuantity}
                  onRemoveCartItem={removeItem}
                  onClearCart={clearCart}
                />
              </StorefrontLayout>
            }
          />
          <Route
            path="checkout"
            element={
              <PrivateRoute hasAnyAuthorities={STORE_AUTHORITIES}>
                <StorefrontLayout
                  categorias={categorias}
                  subcategorias={activeSubcategorias}
                  cartItems={cartItems}
                  onUpdateCartQuantity={updateQuantity}
                  onRemoveCartItem={removeItem}
                  onClearCart={clearCart}
                >
                  <StorefrontRoutes
                    cartItems={cartItems}
                    onAddToCart={handleAddToCart}
                    onUpdateCartQuantity={updateQuantity}
                    onRemoveCartItem={removeItem}
                    onClearCart={clearCart}
                  />
                </StorefrontLayout>
              </PrivateRoute>
            }
          />
          <Route
            path="cuenta/*"
            element={
              <PrivateRoute hasAnyAuthorities={STORE_AUTHORITIES}>
                <StorefrontLayout
                  categorias={categorias}
                  subcategorias={activeSubcategorias}
                  cartItems={cartItems}
                  onUpdateCartQuantity={updateQuantity}
                  onRemoveCartItem={removeItem}
                  onClearCart={clearCart}
                >
                  <StorefrontRoutes
                    cartItems={cartItems}
                    onAddToCart={handleAddToCart}
                    onUpdateCartQuantity={updateQuantity}
                    onRemoveCartItem={removeItem}
                    onClearCart={clearCart}
                  />
                </StorefrontLayout>
              </PrivateRoute>
            }
          />

          {/* Autenticación */}
          <Route path="login" element={<Login />} />
          <Route path="logout" element={<Logout />} />
          <Route path="account">
            <Route
              path="*"
              element={
                <PrivateRoute hasAnyAuthorities={STORE_AUTHORITIES}>
                  <Account />
                </PrivateRoute>
              }
            />
            <Route path="register" element={<Register />} />
            <Route path="activate" element={<Activate />} />
            <Route path="reset">
              <Route path="request" element={<PasswordResetInit />} />
              <Route path="finish" element={<PasswordResetFinish />} />
            </Route>
          </Route>

          {/* Dashboard administrativo JHipster - solo ADMIN/MANAGER */}
          <Route
            path="admin/*"
            element={
              <PrivateRoute hasAnyAuthorities={ADMIN_AUTHORITIES}>
                <Admin />
              </PrivateRoute>
            }
          />

          {/* CRUD de entidades JHipster - cada ruta interna define sus propios permisos */}
          <Route path="*" element={<EntitiesRoutes />} />
          <Route path="*" element={<PageNotFound />} />
        </ErrorBoundaryRoutes>
      </Suspense>
    </div>
  );
};

export default AppRoutes;
