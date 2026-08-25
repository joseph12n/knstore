# Resultado CRUD en producción

Servidor: `https://app.knstore.duckdns.org`  
Plan: `../knstore-production-crud-lifecycle.jmx`  
Sufijo temporal usado: `BDFHJKLP`  
Usuarios: `jmeter_admin`, `jmeter_manager`, `jmeter_cliente_a`, `jmeter_cliente_b`, `jmeter_user`  
Contraseña usada: recibida por propiedad de JMeter, no almacenada en el plan

## Resultado

| Métrica | Resultado |
| --- | ---: |
| Solicitudes ejecutadas | 73 |
| Respuestas esperadas | 73 |
| Fallos | 0 |
| Error rate | 0% |
| Tiempo promedio | 143 ms |
| Mínimo | 77 ms |
| Máximo | 1403 ms |
| Throughput | 6.6 req/s |

## Cobertura

- Autenticación de los cinco perfiles.
- Consultas de referencias de catálogo.
- Ciclos `POST -> PUT -> DELETE` de categorías, subcategorías, IVA, marcas,
  tipos de documento, productos, precios, inventarios, imágenes, etiquetas,
  usuarios, cuentas, direcciones, carritos, ítems de carrito, pedidos, ítems de
  pedido, pagos, envíos y facturas.
- Restricción de `ROLE_MANAGER` sobre autoridades.
- Acceso propio de `jmeter_cliente_a`.
- Bloqueo de acceso cruzado de `jmeter_cliente_b`.
- Bloqueo de recursos protegidos para `jmeter_user`.

## Limpieza

El plan eliminó los registros creados durante el ciclo. Se verificó en
producción que no quedan coincidencias para `BDFHJKLP`, `JMETER CRUD` o
`jmeter_crud`.

## Archivos

- `production-crud.jtl`: resultados detallados.
- `production-crud-html/index.html`: dashboard HTML y gráficos.
