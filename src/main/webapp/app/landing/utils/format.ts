/**
 * Utilidades de formateo para el storefront.
 * RNF-026: los montos siempre se muestran formateados a 2 decimales en COP.
 */

export const formatCOP = (value?: number | null): string => {
  if (value === undefined || value === null || Number.isNaN(value)) {
    return '$ 0';
  }
  // Usamos toLocaleString para separadores de miles y redondeo seguro.
  return value.toLocaleString('es-CO', {
    style: 'currency',
    currency: 'COP',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  });
};

export const formatNumber = (value?: number | null): string => {
  if (value === undefined || value === null || Number.isNaN(value)) {
    return '0';
  }
  return value.toLocaleString('es-CO', { maximumFractionDigits: 2 });
};

export const calculateDiscountPercent = (original?: number | null, current?: number | null): number | null => {
  if (!original || !current || original <= 0 || current >= original) {
    return null;
  }
  return Math.round(((original - current) / original) * 100);
};

export const truncateText = (text?: string | null, maxLength = 80): string => {
  if (!text) {
    return '';
  }
  return text.length > maxLength ? `${text.slice(0, maxLength).trim()}…` : text;
};

export const buildImageUrl = (
  contentType?: string | null,
  base64?: string | null,
  fallback = '/content/images/product-placeholder.png',
): string => {
  if (!base64) {
    return fallback;
  }
  if (base64.startsWith('data:')) {
    return base64;
  }
  return `data:${contentType || 'image/jpeg'};base64,${base64}`;
};
