import React from 'react';
import { Card } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUser } from '@fortawesome/free-solid-svg-icons';

import { ICuenta } from 'app/shared/model/cuenta.model';

interface AccountIdentity {
  firstName?: string;
  lastName?: string;
  email?: string;
}

interface ProfileHeaderCardProps {
  cuenta?: ICuenta;
  account: AccountIdentity;
  avatarSize?: number;
  fotoPerfil?: string;
  children?: React.ReactNode;
}

export const getNombreCompleto = (cuenta: ICuenta | undefined, account: AccountIdentity): string => {
  const nombres = [cuenta?.primerNombre, cuenta?.primerApellido].filter(Boolean).join(' ');
  return nombres || `${account.firstName || ''} ${account.lastName || ''}`.trim();
};

export const ProfileHeaderCard = ({ cuenta, account, avatarSize = 80, fotoPerfil, children }: ProfileHeaderCardProps) => (
  <Card className="text-center p-3">
    <Card.Body>
      <div
        className="rounded-circle mx-auto mb-3 d-flex align-items-center justify-content-center"
        style={{
          width: `${avatarSize}px`,
          height: `${avatarSize}px`,
          backgroundColor: 'var(--kn-color-surface)',
          color: 'var(--kn-color-text)',
          backgroundImage: fotoPerfil ? `url(${fotoPerfil})` : undefined,
          backgroundSize: 'cover',
          backgroundPosition: 'center',
        }}
      >
        {!fotoPerfil && <FontAwesomeIcon icon={faUser} size={avatarSize >= 120 ? '4x' : '2x'} />}
      </div>
      <h5 className="fw-bold mb-1">{getNombreCompleto(cuenta, account)}</h5>
      <p className="text-muted small mb-0">{account.email}</p>
      {children}
    </Card.Body>
  </Card>
);

export default ProfileHeaderCard;
