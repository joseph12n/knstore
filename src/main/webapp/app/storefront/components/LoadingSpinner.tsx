import React from 'react';
import { Spinner } from 'react-bootstrap';

interface LoadingSpinnerProps {
  message?: string;
  fullScreen?: boolean;
}

export const LoadingSpinner = ({ message = 'Cargando...', fullScreen = false }: LoadingSpinnerProps) => {
  const content = (
    <div className="d-flex flex-column align-items-center justify-content-center gap-2 py-5">
      <Spinner animation="border" role="status" variant="dark" />
      <span className="text-muted small">{message}</span>
    </div>
  );

  if (fullScreen) {
    return (
      <div className="d-flex align-items-center justify-content-center" style={{ minHeight: '60vh' }}>
        {content}
      </div>
    );
  }

  return content;
};

export default LoadingSpinner;
