import React, { useState } from 'react';
import { Form, InputGroup } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSearch } from '@fortawesome/free-solid-svg-icons';
import { useNavigate } from 'react-router';

interface SearchBoxProps {
  initialValue?: string;
  variant?: 'default' | 'compact';
}

export const SearchBox = ({ initialValue = '', variant = 'default' }: SearchBoxProps) => {
  const [query, setQuery] = useState(initialValue);
  const navigate = useNavigate();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const trimmed = query.trim();
    if (trimmed) {
      navigate(`/buscar?q=${encodeURIComponent(trimmed)}`);
    }
  };

  return (
    <Form onSubmit={handleSubmit} className={variant === 'compact' ? 'w-auto' : 'w-100'}>
      <InputGroup>
        <Form.Control
          type="search"
          placeholder="Buscar productos..."
          aria-label="Buscar productos"
          value={query}
          onChange={e => setQuery(e.target.value)}
          className={variant === 'compact' ? 'border-end-0' : ''}
        />
        <button type="submit" className="btn btn-primary" aria-label="Buscar">
          <FontAwesomeIcon icon={faSearch} />
        </button>
      </InputGroup>
    </Form>
  );
};

export default SearchBox;
