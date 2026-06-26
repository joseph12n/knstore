import React from 'react';
import { Badge, Card } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEdit, faMapMarkerAlt, faTrash } from '@fortawesome/free-solid-svg-icons';

import { IDireccion } from 'app/shared/model/direccion.model';

interface AddressCardProps {
  direccion: IDireccion;
  isDefault?: boolean;
  selectable?: boolean;
  selected?: boolean;
  onSelect?: (direccion: IDireccion) => void;
  onEdit?: (direccion: IDireccion) => void;
  onDelete?: (direccion: IDireccion) => void;
  onSetDefault?: (direccion: IDireccion) => void;
}

export const AddressCard = ({
  direccion,
  isDefault = false,
  selectable = false,
  selected = false,
  onSelect,
  onEdit,
  onDelete,
  onSetDefault,
}: AddressCardProps) => (
  <Card
    className={`h-100 ${selected ? 'border-primary' : ''}`}
    style={{ cursor: selectable ? 'pointer' : 'default' }}
    onClick={() => selectable && onSelect?.(direccion)}
  >
    <Card.Body>
      <div className="d-flex align-items-start gap-3">
        <FontAwesomeIcon icon={faMapMarkerAlt} className="text-muted mt-1" />
        <div className="flex-grow-1">
          <div className="d-flex align-items-center gap-2 mb-1">
            <span className="fw-semibold">{direccion.direccion}</span>
            {isDefault && <Badge bg="primary">Predeterminada</Badge>}
          </div>
          <div className="text-muted small">
            {direccion.barrio && `${direccion.barrio}, `}
            {direccion.localidad && `${direccion.localidad}, `}
            {direccion.municipio}, {direccion.departamento}
          </div>
        </div>
      </div>
      {!selectable && (
        <div className="d-flex gap-2 mt-3">
          {onEdit && (
            <button type="button" className="btn btn-sm btn-outline-secondary" onClick={() => onEdit(direccion)}>
              <FontAwesomeIcon icon={faEdit} className="me-1" />
              Editar
            </button>
          )}
          {onDelete && (
            <button type="button" className="btn btn-sm btn-outline-danger" onClick={() => onDelete(direccion)}>
              <FontAwesomeIcon icon={faTrash} className="me-1" />
              Eliminar
            </button>
          )}
          {onSetDefault && !isDefault && (
            <button type="button" className="btn btn-sm btn-outline-primary ms-auto" onClick={() => onSetDefault(direccion)}>
              Usar como predeterminada
            </button>
          )}
        </div>
      )}
    </Card.Body>
  </Card>
);

export default AddressCard;
