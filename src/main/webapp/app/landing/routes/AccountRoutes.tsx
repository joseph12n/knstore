import React from 'react';
import { Route, Routes } from 'react-router';

import AccountLayout from 'app/landing/components/AccountLayout';
import AccountPage from 'app/landing/pages/AccountPage';
import ProfilePage from 'app/landing/pages/ProfilePage';
import PasswordChangePage from 'app/landing/pages/PasswordChangePage';
import AddressesPage from 'app/landing/pages/AddressesPage';
import OrdersPage from 'app/landing/pages/OrdersPage';
import OrderDetailPage from 'app/landing/pages/OrderDetailPage';
import PaymentsPage from 'app/landing/pages/PaymentsPage';
import ShipmentsPage from 'app/landing/pages/ShipmentsPage';
import InvoicesPage from 'app/landing/pages/InvoicesPage';

const AccountRoutes = () => (
  <Routes>
    <Route path="/" element={<AccountLayout />}>
      <Route index element={<AccountPage />} />
      <Route path="perfil" element={<ProfilePage />} />
      <Route path="perfil/editar" element={<ProfilePage />} />
      <Route path="seguridad" element={<PasswordChangePage />} />
      <Route path="direcciones" element={<AddressesPage />} />
      <Route path="pedidos" element={<OrdersPage />} />
      <Route path="pedidos/:id" element={<OrderDetailPage />} />
      <Route path="pagos" element={<PaymentsPage />} />
      <Route path="envios" element={<ShipmentsPage />} />
      <Route path="facturas" element={<InvoicesPage />} />
    </Route>
  </Routes>
);

export default AccountRoutes;
