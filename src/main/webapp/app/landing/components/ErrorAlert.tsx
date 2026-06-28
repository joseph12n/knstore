import React from 'react';
import { Alert } from 'react-bootstrap';

interface ErrorAlertProps {
  message?: string | null;
  onRetry?: () => void;
}

export const ErrorAlert = ({ message, onRetry }: ErrorAlertProps) => {
  if (!message) {
    return null;
  }

  return (
    <Alert variant="danger" className="d-flex align-items-center justify-content-between">
      <span>{message}</span>
      {onRetry && (
        <button type="button" className="btn btn-sm btn-outline-danger" onClick={onRetry}>
          Reintentar
        </button>
      )}
    </Alert>
  );
};

export default ErrorAlert;
