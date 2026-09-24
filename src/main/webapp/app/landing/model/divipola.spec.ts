import { describe, expect, it } from 'vitest';

import { DEPARTAMENTOS, municipiosDe, normalizar } from './divipola';

/**
 * Invariantes del dataset DIVIPOLA serializado (una línea por departamento).
 * Protegen el formato compacto que evita el falso positivo de duplicación en Sonar CPD.
 */
describe('divipola', () => {
  it('expone 33 departamentos y 578 municipios en total', () => {
    expect(DEPARTAMENTOS).toHaveLength(33);
    expect(DEPARTAMENTOS.reduce((total, departamento) => total + departamento.municipios.length, 0)).toBe(578);
  });

  it('serializa cada departamento con código de 2 dígitos, nombre y municipios bien formados', () => {
    DEPARTAMENTOS.forEach(departamento => {
      expect(departamento.codigo).toMatch(/^\d{2}$/);
      expect(departamento.nombre.length).toBeGreaterThan(0);
      departamento.municipios.forEach(municipio => {
        expect(municipio.codigo).toMatch(/^\d{5}$/);
        expect(municipio.codigo.startsWith(departamento.codigo)).toBe(true);
        expect(municipio.nombre.length).toBeGreaterThan(0);
      });
    });
  });

  it('no repite códigos de departamento ni de municipio dentro de un departamento', () => {
    const codigosDepartamento = DEPARTAMENTOS.map(departamento => departamento.codigo);
    expect(new Set(codigosDepartamento).size).toBe(codigosDepartamento.length);

    DEPARTAMENTOS.forEach(departamento => {
      const codigosMunicipio = departamento.municipios.map(municipio => municipio.codigo);
      expect(new Set(codigosMunicipio).size).toBe(codigosMunicipio.length);
    });
  });

  it('resuelve municipios por código de departamento y devuelve lista vacía si no existe', () => {
    expect(municipiosDe('05').map(municipio => municipio.nombre)).toContain('Medellín');
    expect(municipiosDe('11')).toEqual([{ codigo: '11001', nombre: 'Bogotá D.C.' }]);
    expect(municipiosDe('99')).toHaveLength(7);
    expect(municipiosDe('00')).toEqual([]);
  });

  it('normaliza mayúsculas, espacios y tildes para comparar', () => {
    expect(normalizar('  Bogotá D.C. ')).toBe('bogota d.c.');
    expect(normalizar('MEDELLÍN')).toBe(normalizar('medellin'));
  });
});
