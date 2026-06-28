import React, { useEffect, useState } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { hasAnyAuthority } from 'app/shared/auth/private-route';

import { Authority } from 'app/shared/jhipster/constants';
import { login } from 'app/shared/reducers/authentication';

import LoginModal from './login-modal';

export const Login = () => {
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);

  const accountAuthorities = useAppSelector(state => state.authentication.account.authorities);

  const loginError = useAppSelector(state => state.authentication.loginError);
  const showModalLogin = useAppSelector(state => state.authentication.showModalLogin);
  const [showModal, setShowModal] = useState(showModalLogin);
  const navigate = useNavigate();
  const pageLocation = useLocation();

  useEffect(() => {
    setShowModal(true);
  }, []);

  const handleLogin = (username, password, rememberMe = false) => dispatch(login(username, password, rememberMe));

  const handleClose = () => {
    setShowModal(false);
    navigate('/');
  };

  const { from } = pageLocation.state || {};

  if (isAuthenticated) {
    if (from?.pathname && from.pathname !== '/') {
      return <Navigate to={from} replace />;
    }

    // ADMIN y MANAGER van al dashboard administrativo; ROLE_USER/CLIENTE van al storefront.
    if (hasAnyAuthority(accountAuthorities, [Authority.ADMIN, Authority.MANAGER])) {
      return <Navigate to="/admin/user-management" replace />;
    }

    return <Navigate to="/" replace />;
  }
  return <LoginModal showModal={showModal} handleLogin={handleLogin} handleClose={handleClose} loginError={loginError} />;
};

export default Login;
