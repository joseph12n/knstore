import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBoxOpen } from '@fortawesome/free-solid-svg-icons';

interface EmptyStateProps {
  title?: string;
  description?: string;
  action?: React.ReactNode;
}

export const EmptyState = ({
  title = 'No hay resultados',
  description = 'Prueba con otros filtros o vuelve más tarde.',
  action,
}: EmptyStateProps) => (
  <div className="text-center py-5 my-4">
    <FontAwesomeIcon icon={faBoxOpen} size="3x" className="text-muted mb-3" />
    <h3 className="h5">{title}</h3>
    <p className="text-muted">{description}</p>
    {action ? <div className="mt-3">{action}</div> : null}
  </div>
);

export default EmptyState;
