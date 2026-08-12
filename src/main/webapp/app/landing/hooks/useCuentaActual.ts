import { useEffect } from 'react';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import { getCuentaByLogin, reset as resetCuenta } from 'app/entities/cuenta/cuenta.reducer';

/**
 * Hook que centraliza el patron repetido en el panel de cliente:
 * cargar la sesion, resolver la Cuenta del usuario por login y limpiarla
 * al desmontar.
 */
export const useCuentaActual = () => {
  const dispatch = useAppDispatch();
  const account = useAppSelector(state => state.authentication.account);
  const cuenta = useAppSelector(state => state.cuenta.entity);
  const loading = useAppSelector(state => state.cuenta.loading);

  useEffect(() => {
    dispatch(getSession());
    if (account.login) {
      dispatch(getCuentaByLogin(account.login));
    }
    return () => {
      dispatch(resetCuenta());
    };
  }, [dispatch, account.login]);

  return { account, cuenta, loading };
};

export default useCuentaActual;
