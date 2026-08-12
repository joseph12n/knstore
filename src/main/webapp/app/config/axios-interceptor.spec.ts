import { describe, expect, it, vi, beforeEach } from 'vitest';

import { Storage } from 'react-jhipster';

import axios from 'axios';

import { AUTHENTICATION_TOKEN_KEY } from 'app/shared/jhipster/constants';
import setupAxiosInterceptors, { isTokenExpired } from './axios-interceptor';

const b64url = (value: string) =>
  btoa(String.fromCharCode(...new TextEncoder().encode(value)))
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=+$/, '');

const buildToken = (exp: number) => {
  const header = b64url(JSON.stringify({ alg: 'HS256', typ: 'JWT' }));
  const payload = b64url(JSON.stringify({ sub: 'user', exp }));
  return `${header}.${payload}.signature`;
};

describe('Axios Interceptor', () => {
  describe('setupAxiosInterceptors', () => {
    const client = axios;
    const onUnauthenticated = vi.fn();
    setupAxiosInterceptors(onUnauthenticated);

    it('onRequestSuccess is called on fulfilled request', () => {
      expect((client.interceptors.request as any).handlers[0].fulfilled({ data: 'foo', url: '/test' })).toMatchObject({
        data: 'foo',
      });
    });
    it('onResponseSuccess is called on fulfilled response', () => {
      expect((client.interceptors.response as any).handlers[0].fulfilled({ data: 'foo' })).toEqual({ data: 'foo' });
    });
    it('onResponseError is called on rejected response', async () => {
      const rejectError = {
        response: {
          statusText: 'NotFound',
          status: 401,
          data: { message: 'Page not found' },
        },
      };
      await expect((client.interceptors.response as any).handlers[0].rejected(rejectError)).rejects.toEqual(rejectError);
      expect(onUnauthenticated).toHaveBeenCalledTimes(1);
    });
  });

  describe('isTokenExpired', () => {
    it('returns true for a token with a past expiration', () => {
      expect(isTokenExpired(buildToken(Math.floor(Date.now() / 1000) - 60))).toBe(true);
    });

    it('returns false for a token with a future expiration', () => {
      expect(isTokenExpired(buildToken(Math.floor(Date.now() / 1000) + 600))).toBe(false);
    });

    it('returns false for a malformed token', () => {
      expect(isTokenExpired('not-a-jwt')).toBe(false);
    });
  });

  describe('request Authorization header', () => {
    beforeEach(() => {
      Storage.local.remove(AUTHENTICATION_TOKEN_KEY);
      Storage.session.remove(AUTHENTICATION_TOKEN_KEY);
    });

    it('does not attach the Authorization header for an expired token', () => {
      Storage.local.set(AUTHENTICATION_TOKEN_KEY, buildToken(Math.floor(Date.now() / 1000) - 60));
      const config = (axios.interceptors.request as any).handlers[0].fulfilled({ headers: {}, url: '/api/categorias' });
      expect(config.headers.Authorization).toBeUndefined();
    });

    it('attaches the Authorization header for a valid token', () => {
      const token = buildToken(Math.floor(Date.now() / 1000) + 600);
      Storage.local.set(AUTHENTICATION_TOKEN_KEY, token);
      const config = (axios.interceptors.request as any).handlers[0].fulfilled({ headers: {}, url: '/api/categorias' });
      expect(config.headers.Authorization).toBe(`Bearer ${token}`);
    });
  });
});
