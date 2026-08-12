import React, { useEffect, useState } from 'react';
import { Form, InputGroup } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSearch } from '@fortawesome/free-solid-svg-icons';
import { useNavigate } from 'react-router';

interface SearchBoxProps {
  initialValue?: string;
}

export const SearchBox = ({ initialValue = '' }: SearchBoxProps) => {
  const [query, setQuery] = useState(initialValue);
  const navigate = useNavigate();

  // Sincroniza el input cuando la query cambia desde fuera (navegacion /buscar?q=...).
  useEffect(() => {
    setQuery(initialValue);
  }, [initialValue]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const trimmed = query.trim();
    if (trimmed) {
      navigate(`/buscar?q=${encodeURIComponent(trimmed)}`);
    }
  };

  return (
    <Form onSubmit={handleSubmit} className="w-100">
      <InputGroup>
        <Form.Control
          type="search"
          placeholder="Buscar productos..."
          aria-label="Buscar productos"
          value={query}
          onChange={e => setQuery(e.target.value)}
        />
        <button type="submit" className="btn btn-primary" aria-label="Buscar">
          <FontAwesomeIcon icon={faSearch} />
        </button>
      </InputGroup>
    </Form>
  );
};

export default SearchBox;
