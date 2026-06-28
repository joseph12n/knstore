import React from 'react';
import { Button, Form, InputGroup } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMinus, faPlus } from '@fortawesome/free-solid-svg-icons';

interface QuantitySelectorProps {
  value: number;
  min?: number;
  max?: number;
  onChange: (value: number) => void;
  size?: 'sm' | 'lg';
}

export const QuantitySelector = ({ value, min = 1, max = 99, onChange, size }: QuantitySelectorProps) => {
  const handleDecrement = () => {
    if (value > min) {
      onChange(value - 1);
    }
  };

  const handleIncrement = () => {
    if (value < max) {
      onChange(value + 1);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const parsed = parseInt(e.target.value, 10);
    if (!Number.isNaN(parsed)) {
      onChange(Math.max(min, Math.min(max, parsed)));
    }
  };

  return (
    <InputGroup size={size} style={{ width: size === 'sm' ? '110px' : '140px' }}>
      <Button variant="outline-secondary" onClick={handleDecrement} disabled={value <= min} aria-label="Disminuir cantidad">
        <FontAwesomeIcon icon={faMinus} size="xs" />
      </Button>
      <Form.Control
        type="number"
        min={min}
        max={max}
        value={value}
        onChange={handleInputChange}
        className="text-center"
        aria-label="Cantidad"
      />
      <Button variant="outline-secondary" onClick={handleIncrement} disabled={value >= max} aria-label="Aumentar cantidad">
        <FontAwesomeIcon icon={faPlus} size="xs" />
      </Button>
    </InputGroup>
  );
};

export default QuantitySelector;
