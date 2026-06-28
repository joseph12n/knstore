import React from 'react';
import { Container, Row, Col } from 'react-bootstrap';
import { Outlet } from 'react-router';

import AccountSidebar from './AccountSidebar';
import AccountMobileNav from './AccountMobileNav';

export const AccountLayout = () => (
  <Container className="py-4 kn-fade-in">
    <AccountMobileNav />
    <Row className="g-4 mt-lg-0">
      <Col lg={3} className="d-none d-lg-block">
        <div className="sticky-top" style={{ top: 'calc(var(--kn-header-height) + 1rem)' }}>
          <AccountSidebar />
        </div>
      </Col>
      <Col lg={9}>
        <Outlet />
      </Col>
    </Row>
  </Container>
);

export default AccountLayout;
