import React, { createContext, use, useCallback, useEffect, useMemo, useRef, useState } from 'react';
import axios from 'axios';
import { toast } from 'react-toastify';

import { CartItem, IProductoStorefront } from 'app/landing/model/storefront.model';
import { ICarrito } from 'app/shared/model/carrito.model';
import { IItemCarrito } from 'app/shared/model/item-carrito.model';
import { ICuenta } from 'app/shared/model/cuenta.model';
import { IProducto } from 'app/shared/model/producto.model';

const CART_STORAGE_KEY = 'knstore-cart';

interface CartContextValue {
  items: CartItem[];
  total: number;
  count: number;
  loading: boolean;
  addItem: (producto: IProductoStorefront, cantidad?: number) => Promise<void>;
  updateQuantity: (itemId: string, cantidad: number) => Promise<void>;
  removeItem: (itemId: string) => Promise<void>;
  clearCart: () => Promise<void>;
  refresh: () => Promise<void>;
}

const CartContext = createContext<CartContextValue | undefined>(undefined);

const loadLocalCart = (): CartItem[] => {
  try {
    const stored = localStorage.getItem(CART_STORAGE_KEY);
    return stored ? JSON.parse(stored) : [];
  } catch {
    return [];
  }
};

const saveLocalCart = (items: CartItem[]) => {
  try {
    localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(items));
  } catch {
    // ignore
  }
};

const clearLocalCart = () => {
  try {
    localStorage.removeItem(CART_STORAGE_KEY);
  } catch {
    // ignore
  }
};

const findCuenta = async (login: string): Promise<ICuenta | undefined> => {
  const response = await axios.get<ICuenta[]>('api/cuentas');
  return response.data.find(c => c.user?.login === login);
};

const findOrCreateCarrito = async (cuentaId: string): Promise<ICarrito> => {
  const carritosResponse = await axios.get<ICarrito[]>('api/carritos');
  const existing = carritosResponse.data.find(c => c.cuenta?.id === cuentaId);
  if (existing?.id) {
    return existing;
  }
  const createResponse = await axios.post<ICarrito>('api/carritos', { cuenta: { id: cuentaId } });
  return createResponse.data;
};

const fetchItemCarritos = async (): Promise<IItemCarrito[]> => {
  const response = await axios.get<IItemCarrito[]>('api/item-carritos');
  return response.data;
};

const fetchProductos = async (): Promise<IProducto[]> => {
  const response = await axios.get<IProducto[]>('api/productos?size=1000&eagerload=true');
  return response.data;
};

const handleCartError = (message: string, error: unknown) => {
  const axiosError = error as any;
  const detail = axiosError?.response?.data?.detail || axiosError?.response?.data?.message || axiosError?.message || 'Error desconocido';
  toast.error(`${message}: ${detail}`);
};

const toStorefrontProducto = (producto: IProducto): IProductoStorefront => ({
  ...producto,
  imagenes: (producto as any).imagenes ?? [],
});

interface CartProviderProps {
  children: React.ReactNode;
  isAuthenticated: boolean;
  login?: string;
}

export const CartProvider: React.FC<CartProviderProps> = ({ children, isAuthenticated, login }) => {
  const [localItems, setLocalItems] = useState<CartItem[]>(() => loadLocalCart());
  const [serverItems, setServerItems] = useState<CartItem[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [initialized, setInitialized] = useState<boolean>(false);

  const carritoIdRef = useRef<string | undefined>(undefined);
  const mergingRef = useRef(false);

  // Persist local cart changes for anonymous users
  useEffect(() => {
    if (!isAuthenticated) {
      saveLocalCart(localItems);
    }
  }, [localItems, isAuthenticated]);

  // Sync local cart across browser tabs
  useEffect(() => {
    const handleStorage = (e: StorageEvent) => {
      if (e.key === CART_STORAGE_KEY && !isAuthenticated) {
        setLocalItems(loadLocalCart());
      }
    };
    window.addEventListener('storage', handleStorage);
    return () => window.removeEventListener('storage', handleStorage);
  }, [isAuthenticated]);

  // Reset server cart state when auth changes
  useEffect(() => {
    if (!isAuthenticated) {
      setServerItems([]);
      setInitialized(false);
      carritoIdRef.current = undefined;
    }
  }, [isAuthenticated]);

  const loadServerCart = useCallback(async () => {
    if (!isAuthenticated || !login || initialized || mergingRef.current) {
      return;
    }

    let cancelled = false;
    setLoading(true);

    try {
      const [cuenta, productos] = await Promise.all([findCuenta(login), fetchProductos()]);
      if (!cuenta?.id || cancelled) {
        setLoading(false);
        return;
      }

      const carrito = await findOrCreateCarrito(cuenta.id);
      carritoIdRef.current = carrito.id;
      const itemCarritos = await fetchItemCarritos();
      const itemsBelongingToCart = itemCarritos.filter(item => item.carrito?.id === carrito.id);

      const productosMap = new Map(productos.map(p => [p.id, p]));
      const loadedItems = itemsBelongingToCart
        .map(item => {
          const producto = productosMap.get(item.producto?.id ?? '');
          if (!producto) return undefined;
          return {
            id: item.id,
            producto: toStorefrontProducto(producto),
            cantidad: item.cantidad ?? 1,
            precioUnitario: item.precioUnitario ?? producto.precio?.precioVenta ?? 0,
          };
        })
        .filter((item): item is NonNullable<typeof item> => item !== undefined);

      // Merge localStorage cart into server cart
      const local = loadLocalCart();
      if (local.length > 0 && !mergingRef.current) {
        mergingRef.current = true;
        const merged = [...loadedItems];
        for (const localItem of local) {
          const existing = merged.find(item => item.producto.id === localItem.producto.id);
          if (existing?.id) {
            existing.cantidad += localItem.cantidad;
            await axios.put(`api/item-carritos/${existing.id}`, {
              id: existing.id,
              cantidad: existing.cantidad,
              precioUnitario: existing.precioUnitario,
              carrito: { id: carrito.id },
              producto: { id: existing.producto.id },
            });
          } else {
            const response = await axios.post<IItemCarrito>('api/item-carritos', {
              cantidad: localItem.cantidad,
              precioUnitario: localItem.precioUnitario,
              carrito: { id: carrito.id },
              producto: { id: localItem.producto.id },
            });
            merged.push({
              id: response.data.id,
              producto: localItem.producto,
              cantidad: localItem.cantidad,
              precioUnitario: localItem.precioUnitario,
            });
          }
        }
        clearLocalCart();
        setServerItems(merged);
      } else {
        setServerItems(loadedItems);
      }
    } catch (error) {
      handleCartError('No se pudo cargar el carrito', error);
      setServerItems([]);
    } finally {
      if (!cancelled) {
        setLoading(false);
        setInitialized(true);
      }
    }

    return () => {
      cancelled = true;
    };
  }, [isAuthenticated, login, initialized]);

  useEffect(() => {
    loadServerCart();
  }, [loadServerCart]);

  const refresh = useCallback(async () => {
    if (!isAuthenticated) {
      setLocalItems(loadLocalCart());
      return;
    }
    setInitialized(false);
    await loadServerCart();
  }, [isAuthenticated, loadServerCart]);

  const items = useMemo(() => (isAuthenticated ? serverItems : localItems), [isAuthenticated, serverItems, localItems]);

  const addItem = useCallback(
    async (producto: IProductoStorefront, cantidad = 1) => {
      const precioUnitario = producto.precio?.precioVenta || 0;

      if (!isAuthenticated) {
        setLocalItems(prev => {
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
              precioUnitario,
            },
          ];
        });
        return;
      }

      const existing = serverItems.find(item => item.producto.id === producto.id);
      if (existing?.id) {
        const newCantidad = existing.cantidad + cantidad;
        setServerItems(prev => prev.map(item => (item.id === existing.id ? { ...item, cantidad: newCantidad } : item)));
        try {
          await axios.put(`api/item-carritos/${existing.id}`, {
            id: existing.id,
            cantidad: newCantidad,
            precioUnitario: existing.precioUnitario,
            carrito: { id: carritoIdRef.current },
            producto: { id: producto.id },
          });
        } catch (error) {
          handleCartError('No se pudo actualizar la cantidad en el carrito', error);
        }
      } else {
        if (!login) return;
        try {
          const cuenta = await findCuenta(login);
          if (!cuenta?.id) return;
          const carrito = await findOrCreateCarrito(cuenta.id);
          carritoIdRef.current = carrito.id;
          const response = await axios.post<IItemCarrito>('api/item-carritos', {
            cantidad,
            precioUnitario,
            carrito: { id: carrito.id },
            producto: { id: producto.id },
          });
          setServerItems(prev => [
            ...prev,
            {
              id: response.data.id,
              producto,
              cantidad,
              precioUnitario,
            },
          ]);
        } catch (error) {
          handleCartError('No se pudo agregar el producto al carrito', error);
        }
      }
    },
    [isAuthenticated, serverItems, login],
  );

  const updateQuantity = useCallback(
    async (itemId: string, cantidad: number) => {
      const normalizedCantidad = Math.max(1, cantidad);

      if (!isAuthenticated) {
        setLocalItems(prev => prev.map(item => (item.id === itemId ? { ...item, cantidad: normalizedCantidad } : item)));
        return;
      }

      const item = serverItems.find(i => i.id === itemId);
      if (!item?.id) return;

      setServerItems(prev => prev.map(i => (i.id === itemId ? { ...i, cantidad: normalizedCantidad } : i)));
      try {
        await axios.put(`api/item-carritos/${item.id}`, {
          id: item.id,
          cantidad: normalizedCantidad,
          precioUnitario: item.precioUnitario,
          carrito: { id: carritoIdRef.current },
          producto: { id: item.producto.id },
        });
      } catch (error) {
        handleCartError('No se pudo actualizar la cantidad', error);
      }
    },
    [isAuthenticated, serverItems],
  );

  const removeItem = useCallback(
    async (itemId: string) => {
      if (!isAuthenticated) {
        setLocalItems(prev => prev.filter(item => item.id !== itemId));
        return;
      }

      setServerItems(prev => prev.filter(item => item.id !== itemId));
      try {
        await axios.delete(`api/item-carritos/${itemId}`);
      } catch (error) {
        handleCartError('No se pudo eliminar el producto del carrito', error);
      }
    },
    [isAuthenticated],
  );

  const clearCart = useCallback(async () => {
    if (!isAuthenticated) {
      setLocalItems([]);
      return;
    }

    const itemsToDelete = [...serverItems];
    setServerItems([]);
    try {
      await Promise.all(itemsToDelete.map(item => (item.id ? axios.delete(`api/item-carritos/${item.id}`) : Promise.resolve())));
    } catch (error) {
      handleCartError('No se pudo vaciar el carrito', error);
    }
  }, [isAuthenticated, serverItems]);

  const total = useMemo(() => items.reduce((sum, item) => sum + item.precioUnitario * item.cantidad, 0), [items]);
  const count = useMemo(() => items.reduce((sum, item) => sum + item.cantidad, 0), [items]);

  const value = useMemo(
    () => ({
      items,
      total,
      count,
      loading,
      addItem,
      updateQuantity,
      removeItem,
      clearCart,
      refresh,
    }),
    [items, total, count, loading, addItem, updateQuantity, removeItem, clearCart, refresh],
  );

  return <CartContext value={value}>{children}</CartContext>;
};

export const useCartContext = (): CartContextValue => {
  const context = use(CartContext);
  if (!context) {
    throw new Error('useCartContext must be used within a CartProvider');
  }
  return context;
};

export default CartContext;
