import { describe, expect, it } from 'vitest';
import { buildImageUrl } from './format';

describe('buildImageUrl', () => {
  const FALLBACK = '/content/images/product-placeholder.png';

  it('returns imagenUrl as-is when provided (takes priority over base64)', () => {
    const url = 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&q=80';
    expect(buildImageUrl('image/jpeg', 'aGVsbG8=', FALLBACK, url)).toBe(url);
  });

  it('returns a data URI base64 as-is', () => {
    const dataUri = 'data:image/png;base64,iVBORw0KGgo=';
    expect(buildImageUrl('image/png', dataUri, FALLBACK)).toBe(dataUri);
  });

  it('builds a data URI from plain base64 using contentType', () => {
    expect(buildImageUrl('image/png', 'aGVsbG8=', FALLBACK)).toBe('data:image/png;base64,aGVsbG8=');
  });

  it('falls back to image/jpeg when contentType is missing', () => {
    expect(buildImageUrl(undefined, 'aGVsbG8=', FALLBACK)).toBe('data:image/jpeg;base64,aGVsbG8=');
  });

  it('returns an http base64 as-is (defensive)', () => {
    const url = 'https://example.com/imagen.png';
    expect(buildImageUrl('image/png', url, FALLBACK)).toBe(url);
  });

  it('returns the fallback when nothing is provided', () => {
    expect(buildImageUrl(null, null, FALLBACK)).toBe(FALLBACK);
    expect(buildImageUrl(undefined, undefined, FALLBACK)).toBe(FALLBACK);
  });

  it('uses the default fallback when omitted', () => {
    expect(buildImageUrl(null, null)).toBe('/content/images/product-placeholder.png');
  });
});
