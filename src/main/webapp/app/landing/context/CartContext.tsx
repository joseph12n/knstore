import React, { createContext, use, useCallback, useEffect, useMemo, useRef, useState } from 'react';
import axios from 'axios';
import { toast } from 'react-toastify';

import { CartItem, IProductoStorefront } from 'app/landing/model/storefront.model';
import { ICarrito } from 'app/shared/model/carrito.model';
import { IItemCarrito } from 'app/shared/model/item-carrito.model';
import { ICuenta } from 'app/shared/model/cuenta.model';
import { IProducto } from 'app/shared/model/producto.model';

const CART_STORAGE_KEY = 'knstore-cart';

export type AddItemResult = { ok: true } | { ok: false; reason: 'no-cuenta' | 'error' };

interface CartContextValue {
  items: CartItem[];
  total: number;
  count: number;
  loading: boolean;
  addItem: (producto: IProductoStorefront, cantidad?: number) => Promise<AddItemResult>;
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

const findCuentaByLogin = async (login: string): Promise<ICuenta | undefined> => {
  // TODO backend: confirmar que /api/cuentas soporta filtro por login; si no, solicitar endpoint.
  const response = await axios.get<ICuenta[]>(`api/cuentas?login=${encodeURIComponent(login)}&size=1`);
  return response.data[0];
};

const findOrCreateCarrito = async (cuentaId: string): Promise<ICarrito> => {
  // Buscar solo el carrito activo de la cuenta en lugar de traer todos.
  const carritosResponse = await axios.get<ICarrito[]>(`api/carritos?cuentaId=${cuentaId}&size=1`);
  const existing = carritosResponse.data[0];
  if (existing?.id) {
    return existing;
  }
  const createResponse = await axios.post<ICarrito>('api/carritos', { cuenta: { id: cuentaId } });
  return createResponse.data;
};

const fetchItemCarritos = async (carritoId: string): Promise<IItemCarrito[]> => {
  // TODO backend: si no soporta filtro por carritoId, usar size=1000 y filtrar en cliente.
  const response = await axios.get<IItemCarrito[]>(`api/item-carritos?carritoId=${carritoId}&size=1000`);
  return response.data.filter(item => item.carrito?.id === carritoId);
};

const fetchProductos = async (): Promise<IProducto[]> => {
  const response = await axios.get<IProducto[]>('api/productos?size=1000&eagerload=true');
  return response.data;
};

const handleCartError = (message: string, error: unknown) => {
  const axiosError = error as { response?: { data?: { detail?: string; message?: string } }; message?: string } | undefined;
  const detail = axiosError?.response?.data?.detail || axiosError?.response?.data?.message || axiosError?.message || 'Error desconocido';
  toast.error(`${message}: ${detail}`);
};

const toStorefrontProducto = (producto: IProducto): IProductoStorefront => ({
  ...producto,
  imagenes: producto.imagenes ?? [],
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

  const serverItemsRef = useRef<CartItem[]>([]);
  const initializedRef = useRef(false);
  const initPromiseRef = useRef<Promise<void> | null>(null);
  const carritoIdRef = useRef<string | undefined>(undefined);
  const carritoPromiseRef = useRef<{ cuentaId: string; promise: Promise<ICarrito> } | null>(null);
  const mergingRef = useRef(false);
  const prevAuthenticatedRef = useRef(isAuthenticated);

  const applyServerItems = useCallback((next: CartItem[]) => {
    serverItemsRef.current = next;
    setServerItems(next);
  }, []);

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

  // On logout, clear anonymous leftovers so the cart does not "resurrect"
  useEffect(() => {
    const wasAuthenticated = prevAuthenticatedRef.current;
    prevAuthenticatedRef.current = isAuthenticated;
    if (wasAuthenticated && !isAuthenticated) {
      clearLocalCart();
      setLocalItems([]);
      applyServerItems([]);
      initializedRef.current = false;
      carritoIdRef.current = undefined;
      carritoPromiseRef.current = null;
    }
  }, [isAuthenticated, applyServerItems]);

  const findOrCreateCarritoInflight = useCallback((cuentaId: string): Promise<ICarrito> => {
    if (carritoPromiseRef.current?.cuentaId === cuentaId) {
      return carritoPromiseRef.current.promise;
    }
    const promise = findOrCreateCarrito(cuentaId).finally(() => {
      if (carritoPromiseRef.current?.promise === promise) {
        carritoPromiseRef.current = null;
      }
    });
    carritoPromiseRef.current = { cuentaId, promise };
    return promise;
  }, []);

  const loadServerCart = useCallback(async () => {
    if (!isAuthenticated || !login || initializedRef.current || mergingRef.current) {
      return;
    }

    setLoading(true);
    const promise = (async () => {
      try {
        const [cuenta, productos] = await Promise.all([findCuentaByLogin(login), fetchProductos()]);
        if (!cuenta?.id) {
          return;
        }

        const carrito = await findOrCreateCarritoInflight(cuenta.id);
        if (!carrito.id) {
          return;
        }
        carritoIdRef.current = carrito.id;
        const itemsBelongingToCart = await fetchItemCarritos(carrito.id);

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
        if (local.length > 0) {
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
          applyServerItems(merged);
        } else {
          applyServerItems(loadedItems);
        }
      } catch (error) {
        handleCartError('No se pudo cargar el carrito', error);
        applyServerItems([]);
      } finally {
        setLoading(false);
        initializedRef.current = true;
        mergingRef.current = false;
      }
    })();
    initPromiseRef.current = promise;
    try {
      await promise;
    } finally {
      if (initPromiseRef.current === promise) {
        initPromiseRef.current = null;
      }
    }
  }, [isAuthenticated, login, findOrCreateCarritoInflight, applyServerItems]);

  useEffect(() => {
    void loadServerCart();
  }, [loadServerCart]);

  const refresh = useCallback(async () => {
    if (!isAuthenticated || !login) {
      setLocalItems(loadLocalCart());
      return;
    }
    mergingRef.current = false;
    initializedRef.current = false;
    await loadServerCart();
  }, [isAuthenticated, login, loadServerCart]);

  const items = useMemo(() => (isAuthenticated ? serverItems : localItems), [isAuthenticated, serverItems, localItems]);

  const addItem = useCallback(
    async (producto: IProductoStorefront, cantidad = 1): Promise<AddItemResult> => {
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
        return { ok: true };
      }

      // Wait for the initial server cart load before mutating so the load does not overwrite the new item
      if (!initializedRef.current) {
        if (initPromiseRef.current === null) {
          void loadServerCart();
        }
        await initPromiseRef.current;
      }

      const existing = serverItemsRef.current.find(item => item.producto.id === producto.id);
      if (existing?.id) {
        const newCantidad = existing.cantidad + cantidad;
        const previousCantidad = existing.cantidad;
        applyServerItems(serverItemsRef.current.map(item => (item.id === existing.id ? { ...item, cantidad: newCantidad } : item)));
        try {
          await axios.put(`api/item-carritos/${existing.id}`, {
            id: existing.id,
            cantidad: newCantidad,
            precioUnitario: existing.precioUnitario,
            carrito: { id: carritoIdRef.current },
            producto: { id: producto.id },
          });
          return { ok: true };
        } catch (error) {
          applyServerItems(serverItemsRef.current.map(item => (item.id === existing.id ? { ...item, cantidad: previousCantidad } : item)));
          handleCartError('No se pudo actualizar la cantidad en el carrito', error);
          return { ok: false, reason: 'error' };
        }
      }

      if (!login) {
        return { ok: false, reason: 'no-cuenta' };
      }
      try {
        const cuenta = await findCuentaByLogin(login);
        if (!cuenta?.id) {
          return { ok: false, reason: 'no-cuenta' };
        }
        const carrito = await findOrCreateCarritoInflight(cuenta.id);
        carritoIdRef.current = carrito.id;
        const response = await axios.post<IItemCarrito>('api/item-carritos', {
          cantidad,
          precioUnitario,
          carrito: { id: carrito.id },
          producto: { id: producto.id },
        });
        applyServerItems([...serverItemsRef.current, { id: response.data.id, producto, cantidad, precioUnitario }]);
        return { ok: true };
      } catch (error) {
        handleCartError('No se pudo agregar el producto al carrito', error);
        return { ok: false, reason: 'error' };
      }
    },
    [isAuthenticated, login, loadServerCart, findOrCreateCarritoInflight, applyServerItems],
  );

  const updateQuantity = useCallback(
    async (itemId: string, cantidad: number) => {
      const normalizedCantidad = Math.max(1, cantidad);

      if (!isAuthenticated) {
        setLocalItems(prev => prev.map(item => (item.id === itemId ? { ...item, cantidad: normalizedCantidad } : item)));
        return;
      }

      const item = serverItemsRef.current.find(i => i.id === itemId);
      if (!item?.id) return;
      const previousCantidad = item.cantidad;

      applyServerItems(serverItemsRef.current.map(i => (i.id === itemId ? { ...i, cantidad: normalizedCantidad } : i)));
      try {
        await axios.put(`api/item-carritos/${item.id}`, {
          id: item.id,
          cantidad: normalizedCantidad,
          precioUnitario: item.precioUnitario,
          carrito: { id: carritoIdRef.current },
          producto: { id: item.producto.id },
        });
      } catch (error) {
        applyServerItems(serverItemsRef.current.map(i => (i.id === itemId ? { ...i, cantidad: previousCantidad } : i)));
        handleCartError('No se pudo actualizar la cantidad', error);
      }
    },
    [isAuthenticated, applyServerItems],
  );

  const removeItem = useCallback(
    async (itemId: string) => {
      if (!isAuthenticated) {
        setLocalItems(prev => prev.filter(item => item.id !== itemId));
        return;
      }

      const removed = serverItemsRef.current.find(i => i.id === itemId);
      const removedIndex = serverItemsRef.current.findIndex(i => i.id === itemId);
      applyServerItems(serverItemsRef.current.filter(item => item.id !== itemId));
      try {
        await axios.delete(`api/item-carritos/${itemId}`);
      } catch (error) {
        if (removed && removedIndex >= 0) {
          applyServerItems([...serverItemsRef.current.slice(0, removedIndex), removed, ...serverItemsRef.current.slice(removedIndex)]);
        }
        handleCartError('No se pudo eliminar el producto del carrito', error);
      }
    },
    [isAuthenticated, applyServerItems],
  );

  const clearCart = useCallback(async () => {
    if (!isAuthenticated) {
      setLocalItems([]);
      return;
    }

    const carritoId = carritoIdRef.current;
    const itemsBeforeClear = [...serverItemsRef.current];
    applyServerItems([]);
    if (!carritoId) {
      return;
    }
    try {
      await axios.delete(`api/carritos/${carritoId}/items`);
    } catch (error) {
      const axiosError = error as { response?: { status?: number } } | undefined;
      if (axiosError?.response?.status === 403 || axiosError?.response?.status === 404) {
        // El carrito del servidor ya está vacío o el recurso no existe: el carrito local ya quedó limpio.
        return;
      }
      applyServerItems(itemsBeforeClear);
      toast.error('No se pudo vaciar el carrito. Inténtalo de nuevo.');
    }
  }, [isAuthenticated, applyServerItems]);

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
