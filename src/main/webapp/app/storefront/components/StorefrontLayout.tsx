import React from 'react';

import StoreHeader from './StoreHeader';
import StoreFooter from './StoreFooter';
import { CartItem } from 'app/storefront/model/storefront.model';
import { ICategoria } from 'app/shared/model/categoria.model';
import { ISubcategoria } from 'app/shared/model/subcategoria.model';

interface StorefrontLayoutProps {
  children: React.ReactNode;
  categorias: ICategoria[];
  subcategorias: ISubcategoria[];
  cartItems: CartItem[];
  onUpdateCartQuantity: (itemId: string, quantity: number) => void;
  onRemoveCartItem: (itemId: string) => void;
  onClearCart: () => void;
}

export const StorefrontLayout = ({
  children,
  categorias,
  subcategorias,
  cartItems,
  onUpdateCartQuantity,
  onRemoveCartItem,
  onClearCart,
}: StorefrontLayoutProps) => (
  <div className="storefront d-flex flex-column min-vh-100">
    <StoreHeader
      categorias={categorias}
      subcategorias={subcategorias}
      cartItems={cartItems}
      onUpdateCartQuantity={onUpdateCartQuantity}
      onRemoveCartItem={onRemoveCartItem}
      onClearCart={onClearCart}
    />
    <main className="flex-grow-1">{children}</main>
    <StoreFooter />
  </div>
);

export default StorefrontLayout;
