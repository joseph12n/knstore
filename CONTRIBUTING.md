# Guía de Contribución — KN-Store

¡Gracias por contribuir a KN-Store! Para mantener el historial del repositorio limpio, legible y fácil de auditar, implementamos reglas estrictas para los mensajes de commit basadas en la especificación internacional de **Conventional Commits**.

---

## Estructura de un Commit Message

Cada mensaje de commit que se envíe al repositorio debe seguir rigurosamente la siguiente estructura:

```text
<tipo>(<alcance>): <descripción corta en minúsculas y en español>
```

### 1. Tipos Permitidos (`<tipo>`)

El tipo define la intención del cambio realizado en el código:

- **feat**: Una nueva funcionalidad para el usuario (ej. un nuevo endpoint, una vista o componente).
- **fix**: Corrección de un error, bug o fallo en el sistema.
- **chore**: Tareas de mantenimiento general, actualización de dependencias, configuración de entornos o herramientas de empaquetado (configuraciones de CORS, Webpack, etc.).
- **refactor**: Cambios en el código que no corrigen errores ni añaden funcionalidades (reestructuración, optimización de algoritmos, mejora de legibilidad).
- **docs**: Cambios exclusivos en la documentación del proyecto (archivos `.md` o comentarios dentro del código).
- **style**: Cambios estéticos o de formato que no alteran el significado ni la lógica del código (espacios en blanco, indentación, punto y coma faltantes).
- **test**: Creación, modificación o corrección de pruebas unitarias o de integración.

### 2. Alcances Definidos (`<alcance>`)

El alcance ayuda a identificar de forma inmediata qué capa o módulo de la arquitectura de KN-Store se vio afectada:

- **backend**: Cambios específicos dentro de la lógica del servidor con Spring Boot (controladores, servicios, DTOs, entidades).
- **frontend**: Cambios específicos en la interfaz de usuario desarrollada con React y TypeScript.
- **config**: Modificaciones en archivos de configuración global, variables de entorno o conexiones (como la base de datos MongoDB).
- **webpack / vite**: Configuraciones de empaquetadores y herramientas de construcción del cliente.
- **docs**: Modificaciones directas en los manuales de arquitectura o archivos informativos del repositorio.

### 3. Reglas para la Descripción

- Debe redactarse en minúsculas obligatoriamente.
- Debe escribirse en español fluido y en tiempo imperativo/infinitivo (ej. _configurar_ o _permite_, no _configurado_ o _agregado_).
- No debe incluir un punto final (`.`) al terminar la línea.

---

## 📝 Ejemplos de Commits Correctos

A modo de referencia visual del estándar implementado en el flujo de trabajo, se deben seguir ejemplos como:

```text
feat(backend): permitir a CLIENTE crear su propia Cuenta con validación
chore(config): configurar CORS y URL base para producción
fix(webpack): usar URL relativa para API en producción
refactor(frontend): reorganizar storefront como landing y agregar dashboard wrapper
docs: documentar arquitectura, cambios y requerimientos del proyecto
```

---

## 🔄 Flujo de Trabajo en Git (Git Flow)

1. **Trabajar en ramas dedicadas**: Nunca realices commits directamente sobre la rama principal (`main` o `develop`). Crea una rama descriptiva para tu tarea utilizando prefijos:

- `feature/modulo-inventario`
- `bugfix/error-login`

2. **Commits atómicos**: Procura que cada commit represente un único cambio lógico y aislado. Evita subir "super-commits" con múltiples correcciones y características mezcladas.

3. **Validación local**: Antes de realizar el `git push`, asegúrate de que el backend compile correctamente sin romper la arquitectura de DTOs y que el cliente TypeScript no arroje advertencias de tipado.

4. **Pull Requests (PR)**: Al abrir un PR para integrar tus cambios, describe brevemente el impacto de tu código y solicita la revisión de al menos un compañero del equipo.
