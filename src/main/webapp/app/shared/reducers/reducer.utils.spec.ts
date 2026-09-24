import { describe, expect, it } from 'vitest';

import { EntityState, createEntitySlice } from './reducer.utils';

interface ITestEntity {
  id: string;
  nombre: string;
}

const initialState: EntityState<ITestEntity> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: {} as ITestEntity,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

const slice = createEntitySlice<ITestEntity>({ name: 'test', initialState });

describe('createEntitySlice rejection matcher', () => {
  it('preserves the rejected action error message', () => {
    const state = slice.reducer(undefined, {
      type: 'test/fetch/rejected',
      payload: 'some payload',
      error: { message: 'error message' },
    });

    expect(state).toMatchObject({
      loading: false,
      updating: false,
      updateSuccess: false,
      errorMessage: 'error message',
    });
  });

  it('falls back to the payload message when error is not present', () => {
    const state = slice.reducer(undefined, {
      type: 'test/fetch/rejected',
      payload: { message: 'payload message' },
    });

    expect(state.errorMessage).toBe('payload message');
  });

  it('sets errorMessage to null when no message is available', () => {
    const state = slice.reducer(undefined, { type: 'test/fetch/rejected', payload: 'some payload' });

    expect(state.errorMessage).toBeNull();
  });

  it('does not write errorMessage when the rejected action belongs to another slice', () => {
    const state = slice.reducer(undefined, {
      type: 'authentication/get_account/rejected',
      error: { message: 'Request failed with status code 401' },
    });

    expect(state.errorMessage).toBeNull();
  });

  it('writes errorMessage for a rejected action of the slice itself', () => {
    const state = slice.reducer(undefined, {
      type: 'test/fetch/rejected',
      error: { message: 'own slice error' },
    });

    expect(state.errorMessage).toBe('own slice error');
  });

  it('clears errorMessage on a fulfilled action of the slice itself', () => {
    const state = slice.reducer(
      { ...initialState, errorMessage: 'previous error' },
      { type: 'test/fetch/fulfilled', payload: { data: [] } },
    );

    expect(state.errorMessage).toBeNull();
  });

  it('keeps errorMessage when the fulfilled action belongs to another slice', () => {
    const state = slice.reducer(
      { ...initialState, errorMessage: 'previous error' },
      { type: 'authentication/get_account/fulfilled', payload: {} },
    );

    expect(state.errorMessage).toBe('previous error');
  });
});
