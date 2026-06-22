import { useCallback, useEffect, useState } from 'react';

import { CartItem, IProductoStorefront } from 'app/storefront/model/storefront.model';

const CART_STORAGE_KEY = 'knstore-cart';

export const useCart = () => {
  const [items, setItems] = useState<CartItem[]>(() => {
    try {
      const stored = localStorage.getItem(CART_STORAGE_KEY);
      return stored ? JSON.parse(stored) : [];
    } catch {
      return [];
    }
  });

  useEffect(() => {
    localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(items));
  }, [items]);

  const addItem = useCallback((producto: IProductoStorefront, cantidad = 1) => {
    setItems(prev => {
      const existing = prev.find(item => item.producto.id === producto.id);
      if (existing) {
        return prev.map(item => (item.producto.id === producto.id ? { ...item, cantidad: item.cantidad + cantidad } : item));
      }
      return [
        ...prev,
        {
          id: `${Date.now()}-${Math.random().toString(36).slice(2, 9)}`,
          producto,
          cantidad,
          precioUnitario: producto.precio?.precioVenta || 0,
        },
      ];
    });
  }, []);

  const updateQuantity = useCallback((itemId: string, cantidad: number) => {
    setItems(prev => prev.map(item => (item.id === itemId ? { ...item, cantidad: Math.max(1, cantidad) } : item)));
  }, []);

  const removeItem = useCallback((itemId: string) => {
    setItems(prev => prev.filter(item => item.id !== itemId));
  }, []);

  const clearCart = useCallback(() => {
    setItems([]);
  }, []);

  const total = items.reduce((sum, item) => sum + item.precioUnitario * item.cantidad, 0);
  const count = items.reduce((sum, item) => sum + item.cantidad, 0);

  return {
    items,
    total,
    count,
    addItem,
    updateQuantity,
    removeItem,
    clearCart,
  };
};

export default useCart;
