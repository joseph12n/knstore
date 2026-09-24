import { act, renderHook } from '@testing-library/react';
import { afterEach, describe, expect, it, vi } from 'vitest';

import { BREAKPOINTS } from 'app/landing/utils/constants';

import { MOBILE_VIEW_QUERY, useIsMobileView } from './useIsMobileView';

type ChangeListener = (event: { matches: boolean }) => void;

/** Mock de window.matchMedia con estado controlable y listeners capturados. */
const mockMatchMedia = (initialMatches: boolean) => {
  const state = { matches: initialMatches };
  const listeners = new Set<ChangeListener>();

  const addEventListener = vi.fn((type: string, listener: ChangeListener) => {
    if (type === 'change') {
      listeners.add(listener);
    }
  });
  const removeEventListener = vi.fn((type: string, listener: ChangeListener) => {
    if (type === 'change') {
      listeners.delete(listener);
    }
  });

  const mql = {
    get matches() {
      return state.matches;
    },
    media: MOBILE_VIEW_QUERY,
    onchange: null,
    addEventListener,
    removeEventListener,
    addListener: vi.fn((listener: ChangeListener) => addEventListener('change', listener)),
    removeListener: vi.fn((listener: ChangeListener) => removeEventListener('change', listener)),
    dispatchEvent: vi.fn(() => true),
  };

  const matchMedia = vi.fn(() => mql);
  vi.stubGlobal('matchMedia', matchMedia);

  return {
    matchMedia,
    addEventListener,
    removeEventListener,
    /** Dispara el evento `change` del MediaQueryList mockeado. */
    fireChange(matches: boolean) {
      state.matches = matches;
      act(() => {
        listeners.forEach(listener => listener({ matches }));
      });
    },
  };
};

describe('useIsMobileView', () => {
  afterEach(() => {
    vi.unstubAllGlobals();
    vi.restoreAllMocks();
  });

  it('inicializa en true cuando matchMedia indica viewport móvil', () => {
    mockMatchMedia(true);

    const { result } = renderHook(() => useIsMobileView());

    expect(result.current).toBe(true);
  });

  it('inicializa en false en desktop con el matchMedia real de happy-dom (viewport 1024)', () => {
    const { result } = renderHook(() => useIsMobileView());

    expect(result.current).toBe(false);
    expect(BREAKPOINTS.lg).toBe(992);
    expect(MOBILE_VIEW_QUERY).toBe('(max-width: 991.98px)');
  });

  it('cae al fallback de innerWidth cuando no hay matchMedia', () => {
    vi.stubGlobal('matchMedia', undefined);

    const { result } = renderHook(() => useIsMobileView());

    // happy-dom nace con 1024px (desktop): el fallback devuelve false sin lanzar.
    expect(result.current).toBe(false);
  });

  it('cambia a true cuando el listener `change` dispara con matches: true', () => {
    const media = mockMatchMedia(false);

    const { result } = renderHook(() => useIsMobileView());
    expect(result.current).toBe(false);
    expect(media.addEventListener).toHaveBeenCalledWith('change', expect.any(Function));

    media.fireChange(true);

    expect(result.current).toBe(true);
  });

  it('limpia el listener `change` al desmontar', () => {
    const media = mockMatchMedia(false);

    const { unmount } = renderHook(() => useIsMobileView());
    expect(media.removeEventListener).not.toHaveBeenCalled();

    unmount();

    expect(media.removeEventListener).toHaveBeenCalledWith('change', expect.any(Function));

    // Tras desmontar no quedan listeners: el disparo posterior no actualiza nada.
    media.fireChange(true);
    expect(media.removeEventListener).toHaveBeenCalledTimes(1);
  });
});
