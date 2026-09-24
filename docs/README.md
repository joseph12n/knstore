# Documentación del proyecto

Estructura de la documentación de KN-Store:

## Secciones

| Apartado                    | Contenido                                                                                                            |
| --------------------------- | -------------------------------------------------------------------------------------------------------------------- |
| `01-ANALYSIS/`              | Levantamiento de información, entrevistas, historias de usuario, BPMN, requerimientos (SRS, incluida la matriz de pendientes), costos y marco teórico. |
| `02-DESING/`                | Casos de uso, diagramas de despliegue, clases, relacional, fichas técnicas, wireframes y registro de protección de rutas por rol. |
| `03-DOCUMENTACION/`         | Documentación de gestión del proyecto: presentación (README), contexto para agentes (AGENTS), contribución (CONTRIBUTING), handoff de sesión y validación del esquema MongoDB. |
| `04-DIAGRAMA DE DESPLIEGUE/`| Diagramas de despliegue (drawio, PNG, VPP) y su README.                                                              |
| `05-MANUAL DE INSTALACION/` | Manual de instalación (PDF y DOCX).                                                                                  |
| `06-PLAN DE INSTALACION/`   | Plan de instalación (PDF y DOCX).                                                                                    |
| `07-PLAN DE CAPCITACION/`   | Manual de capacitación (PDF y DOCX).                                                                                 |
| `08-PLAN DE RESPALDO/`      | README del plan de respaldo.                                                                                         |
| `09-MANUAL TECNICO/`        | README del manual técnico.                                                                                           |
| `10-PLAN DE MIGRACION/`     | README del plan de migración.                                                                                        |
| `11-MANUAL TECNICO/`        | README del manual técnico.                                                                                           |
| `jmeter/`                   | Planes JMeter (funcional y estrés), matriz de casos, informe de pruebas unitarias, resultados JTL, informe HTML/PDF y pipeline de generación. |
| `backup/`                   | Scripts de backup y restauración de MongoDB.                                                                         |

## Convención

- Cada apartado numerado agrupa documentos de una fase o dominio; las carpetas auxiliares (`jmeter/`, `backup/`) agrupan por herramienta o función.
- `01-ANALYSIS/README.md` y `02-DESING/README.md` son los índices de sus apartados.
- `03-DOCUMENTACION/ESTADO_SESION.md` es el handoff de estado que debe leerse antes de retomar trabajo.
