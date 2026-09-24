import { useEffect, useState } from 'react';

import { BREAKPOINTS } from 'app/landing/utils/constants';

/** Query móvil: ancho <= BREAKPOINTS.lg - 0.02 (991.98px), igual al corte de admin.scss. */
export const MOBILE_VIEW_QUERY = `(max-width: ${BREAKPOINTS.lg - 0.02}px)`;

/**
 * Indica si el viewport se considera móvil (<= 991.98px).
 * Resincroniza al montar y escucha el evento `change` de matchMedia;
 * sin matchMedia cae al ancho de ventana (default defensivo).
 */
export const useIsMobileView = (): boolean => {
  const [isMobile, setIsMobile] = useState(() => {
    if (typeof window === 'undefined') {
      return false;
    }
    if (typeof window.matchMedia === 'function') {
      return window.matchMedia(MOBILE_VIEW_QUERY).matches;
    }
    return window.innerWidth < BREAKPOINTS.lg;
  });

  useEffect(() => {
    if (typeof window.matchMedia !== 'function') {
      return;
    }
    const mql = window.matchMedia(MOBILE_VIEW_QUERY);
    const onChange = (event: MediaQueryListEvent) => setIsMobile(event.matches);
    setIsMobile(mql.matches);
    mql.addEventListener('change', onChange);
    return () => mql.removeEventListener('change', onChange);
  }, []);

  return isMobile;
};

export default useIsMobileView;
