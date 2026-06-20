import React from 'react';
import { Container, Nav, Navbar, NavDropdown } from 'react-bootstrap';
import { Link } from 'react-router';

import { ICategoria } from 'app/shared/model/categoria.model';
import { ISubcategoria } from 'app/shared/model/subcategoria.model';

interface CategoryNavProps {
  categorias: ICategoria[];
  subcategorias: ISubcategoria[];
}

export const CategoryNav = ({ categorias, subcategorias }: CategoryNavProps) => {
  const categoriasList = categorias ?? [];
  const subcategoriasList = subcategorias ?? [];
  const getSubcategorias = (categoriaId?: string) => subcategoriasList.filter(s => s.categoria?.id === categoriaId);

  return (
    <Navbar expand="lg" className="py-0 border-bottom" style={{ backgroundColor: 'var(--kn-color-surface)' }}>
      <Container>
        <Navbar.Toggle aria-controls="category-nav" className="w-100 mb-2">
          <span className="small fw-semibold">Categorías</span>
        </Navbar.Toggle>
        <Navbar.Collapse id="category-nav">
          <Nav className="mx-auto gap-lg-3">
            <Nav.Link as={Link} to="/" className="fw-medium">
              Inicio
            </Nav.Link>
            {categoriasList.map(categoria => {
              const subs = getSubcategorias(categoria.id);
              if (subs.length === 0) {
                return (
                  <Nav.Link key={categoria.id} as={Link} to={`/categorias/${categoria.slug}`} className="fw-medium">
                    {categoria.nombre}
                  </Nav.Link>
                );
              }
              return (
                <NavDropdown
                  key={categoria.id}
                  title={categoria.nombre}
                  id={`cat-dropdown-${categoria.id}`}
                  className="fw-medium"
                  renderMenuOnMount
                >
                  <NavDropdown.Item as={Link} to={`/categorias/${categoria.slug}`}>
                    Ver todo {categoria.nombre}
                  </NavDropdown.Item>
                  <NavDropdown.Divider />
                  {subs.map(sub => (
                    <NavDropdown.Item key={sub.id} as={Link} to={`/categorias/${categoria.slug}/${sub.slug}`}>
                      {sub.nombre}
                    </NavDropdown.Item>
                  ))}
                </NavDropdown>
              );
            })}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default CategoryNav;
