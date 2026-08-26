# KN-Store · Pruebas con Apache JMeter (Windows y Linux)

Este README explica cómo instalar y ejecutar **todas las pruebas funcionales, de estrés y rendimiento** de KN-Store de forma local — tanto en **Windows** como en **Linux** — para que cualquier integrante del equipo pueda reproducirlas en su computador.
> Todos los comandos de este documento se ejecutan **desde la raíz del repositorio** (`cd knstore`).

---

## 1. Qué contiene la carpeta

| Archivo | Descripción |
|---|---|
| `knstore_test_plan.jmx` (en `docs/`) | **Plan principal**: 88 casos funcionales (CP-001…CP-088) en secciones 01-06 por rol, más la **sección 07** de estrés y rendimiento (ES-01…ES-07). Cada caso conserva sus 3 reportes: *View Results Tree*, *Summary Report* y *Response Time Graph*. |
| `knstore_stress_plan.jmx` (en `docs/`) | Plan **solo** de estrés/rendimiento (mismos escenarios de la sección 07, útil cuando no se quiere correr la parte funcional). |
| `docs/plan_jmeter.md` | Matriz de casos de prueba (requisito, datos, resultado esperado) + tabla de escenarios de estrés y ejecuciones de referencia. |
| `docs/jmeter/resultados.jtl`, `stress/*.jtl` | Resultados de las ejecuciones de referencia (se regeneran al correr de nuevo). |
| `docs/jmeter/informe_knstore.html` / `.pdf` | Informe final autogenerado (se regenera con `generar_informe.sh`, ver sección 6). |
| `docs/jmeter/gen_informe.py`, `print_variant.py`, `pdf_build.py`, `generar_informe.sh` | Pipeline de generación del informe (opcional para quien solo ejecuta pruebas). |

**Requisitos del sistema probado:** JMeter 5.6.3 · Java 21 · MongoDB en réplica (solo backend). Los compañeros **no necesitan** el backend local si apuntan al despliegue ya existente (**configurable**, ver sección 4).

---

## 2. Instalación

### 2.1 Instalar Java 21

**Windows:**
1. Descargar JDK 21 de [Adoptium](https://adoptium.net/temurin/releases/?version=21) (`.msi` o `.zip`).
2. Instalar y verificar en PowerShell:
   ```powershell
   java -version
   ```

**Linux (Debian/Ubuntu):**
```bash
sudo apt install openjdk-21-jdk -y
java -version
```

### 2.2 Instalar JMeter 5.6.3

Descarga desde [https://jmeter.apache.org/download_jmeter.cgi](https://jmeter.apache.org/download_jmeter.cgi) (binario `apache-jmeter-5.6.3.tgz`/`.zip`).

**Windows:**
1. Descomprimir en `C:\tools\apache-jmeter-5.6.3`.
2. Agregar `C:\tools\apache-jmeter-5.6.3\bin` al PATH (Variables de entorno del sistema).
3. Probar:
   ```powershell
   jmeter --version
   ```

**Linux:**
```bash
tar -xzf apache-jmeter-5.6.3.tgz -C /opt
sudo ln -s /opt/apache-jmeter-5.6.3/bin/jmeter /usr/local/bin/jmeter
jmeter --version
```

### 2.3 (Opcional, recomendado) Gráficos de línea con JFreeChart

JMeter 5.6 ya no incluye JFreeChart; el listener *Response Time Graph* (de línea) necesita estos dos JAR:

- `jfreechart-1.0.19.jar` → https://repo1.maven.org/maven2/org/jfree/jfreechart/1.0.19/jfreechart-1.0.19.jar
- `jcommon-1.0.24.jar` → https://repo1.maven.org/maven2/org/jfree/jcommon/1.0.24/jcommon-1.0.24.jar

**Windows:** cópialos en `C:\tools\apache-jmeter-5.6.3\lib\` (o deja la ruta en un archivo aparte y agrega como se ve en el punto 5).
**Linux:** cópialos en `/opt/apache-jmeter-5.6.3/lib/` (si no tienes permisos de root, usa la opción `-Juser.classpath` del punto 5).

> Si no los instalas, el resto del plan funciona igual; solo el gráfico de línea mostrará el panel vacío (o fallará al dibujar).

---

## 3. Usuarios de prueba (obligatorio)

Los endpoint protegidos necesitan estos usuarios **activos** en el despliegue:

| Usuario (login) | Rol | Clave |
|---|---|---|
| `admin@knstore.com` | ADMIN | `123456` |
| `cliente01@knstore.com` | CLIENTE | `123456` |
| `borrar01@knstore.com` | CLIENTE (desechable) | `123456` |
| `jmetermanager` | MANAGER | `123456` (puede variar; ver UDV) |

El caso CP-001 genera un usuario nuevo con correo único por corrida (no hace falta crearlo). `borrar01` se consume en CP-016 (elimina su perfil): para re-ejecuciones completas, crear la cuenta de nuevo (el plan re-crea su *Cuenta* automáticamente si no existe).

---

## 4. Configurar el entorno de destino

Abrir el archivo `.jmx` en JMeter → árbol del Test Plan → **User Defined Variables**:

| Variable | Valor por defecto | Nota |
|---|---|---|
| `PROTOCOL` / `HOST` / `PORT` | `https` / `app.knstore.duckdns.org` / `443` | Punto de API. Para local: `http` / `localhost` / `8080` |
| `ADMIN_USER` / `ADMIN_PWD` | `admin@knstore.com` / `123456` | |
| `CLI_USER` / `CLI_PWD` | `cliente01@knstore.com` / `123456` | |
| `BORRAR_USER` / `BORRAR_PWD` | `borrar01@knstore.com` / `123456` | |
| `MGR_USER` / `MGR_PWD` | `jmetermanager` / `123456` | |
| `N_S1…N_S7`, `S5_DUR`, `RAMPUP`, `ES_PAGE` | — | Cargas de la sección 07 (solo plan de estrés) |

> Alternativa por línea de comandos: `-JHOST=localhost -JPORT=8080` sobreescribe cualquier variable.

---

## 5. Ejecutar las pruebas

### 5.1 Modo GUI (recomendado para ver resultados y gráficos)

1. Iniciar JMeter:
   - **Windows:** `jmeter.bat` (desde `bin/` o con el path configurado).
   - **Linux:** `jmeter`.
2. **Archivo → Abrir** → seleccionar `knstore_test_plan.jmx` (ubicado en `docs/`).
3. Elegir qué ejecutar:
   - **Una sección:** clic sobre el Thread Group (ej. `01 - PUBLICO`, `07 - ESTRES...`) → **Ctrl+R**.
   - **Toda la suite (funcional + estrés):** clic sobre `KN-Store Test Web App` → **Ctrl+R**.
4. Revisar:
   - Expande cada caso → **View Results Tree** (detalle de la llamada), **Summary Report** (métricas), **Response Time Graph** (curva).
   - En sección/grupos: *Summary Report (Seccion X)*, *Response Time Graph (Seccion X)* y *Aggregate Graph*.
5. Limpiar resultados entre ejecuciones: botón de la escoba (también **Run → Clear All**).

Si se ejecuta JMeter sin `JFreeChart` en `lib/`, agregar los JAR por línea de comandos al abrir:

```powershell
# Windows
jmeter -Juser.classpath="C:\libs\jfreechart-1.0.19.jar;C:\libs\jcommon-1.0.24.jar" -t docs\knstore_test_plan.jmx
```
```bash
# Linux
jmeter -Juser.classpath="$HOME/libs/jfreechart-1.0.19.jar:$HOME/libs/jcommon-1.0.24.jar" -t docs/knstore_test_plan.jmx
```

> Interfaz pequeña/grande en pantallas de alta resolución: arranca con `-Dsun.java2d.uiScale=1.5` (escala), p. ej. en Linux `JVM_ARGS="-Dsun.java2d.uiScale=1.5" jmeter`.

### 5.2 Modo CLI (headless, ideal para automatizar)

```bash
# Windows (PowerShell)
jmeter -n -t docs\knstore_test_plan.jmx -l docs\jmeter\resultados.jtl -e -o docs\jmeter\informe-html

# Linux
jmeter -n -t docs/knstore_test_plan.jmx -l docs/jmeter/resultados.jtl -e -o docs/jmeter/informe-html
```

- `-e -o` genera el **dashboard HTML oficial de JMeter** con todos los gráficos (percentiles, peticiones/segundo, tiempos sobre tiempo).
- Si la carpeta de resultados ya existe, borrarla antes (`rm -rf`) o usar otro nombre.

### 5.3 Personalizar las pruebas de estrés (sección 07 / `knstore_stress_plan.jmx`)

Todas las cargas son ajustables por variable UDV o `-J`:

| Variable | Significado | Default |
|---|---|---|
| `N_S1` | Hilos escenario catálogo (ES-01) | 50 |
| `N_S2` | Hilos autenticación bcrypt (ES-02) | 40 |
| `N_S3` | Hilos checkout de escritura (ES-03) | 40 |
| `N_S4` | Hilos concurrencia stock (ES-04) | 50 |
| `N_S5` | Hilos soak (ES-05) | 12 |
| `S5_DUR` | Duración del soak en segundos | 180 |
| `N_S6` / `RAMP_S6` | Spike: hilos y ramp-up (ES-06) | 100 / 2 |
| `N_S7` / `RAMP_S7` | Escalado: hilos y ramp-up (ES-07) | 150 / 120 |
| `RAMPUP` | Ramp-up común de escenarios 01-04 (s) | 10 |
| `ES_PAGE` | Página inicial del listado (URL) | 0 |

Ejemplos:

```bash
# Solo estrés con cargas de "límite"
jmeter -n -t docs/knstore_stress_plan.jmx -l resultados_stress.jtl \
       -JN_S1=150 -JN_S2=80 -JN_S3=80 -JN_S4=80 -JN_S6=150 -JN_S7=200 -JS5_DUR=600

# En Windows (PowerShell), igual pero con rutas Windows:
jmeter -n -t docs\knstore_stress_plan.jmx -l resultados_stress.jtl -JN_S1=100 -JS5_DUR=300
```

**Notas importantes de la sección 07:**
- ES-04 (concurrencia): usa un producto de prueba con stock 15; la verificación final comprueba que **no hubo sobreventa**.
- ES-06 (spike) es la prueba agresiva: 100 hilos en 2 segundos. En instancias pequeñas se observa la saturación (P95 elevado) sin necesariamente generar errores.
- Cada escenario crea sus propios datos de prueba (productos E/F). No es necesario limpiarlos entre corridas, pero se acumulan en la BD.

---

## 6. Generar el informe final (HTML/PDF)

El informe oficial se regenera con los resultados locales:

```bash
# Linux (requiere: python3 + pip install pypdf; Chromium via: npx playwright install chromium)
bash docs/jmeter/generar_informe.sh
```

```powershell
# Windows (PowerShell)
python docs\jmeter\gen_informe.py
python docs\jmeter\print_variant.py
python docs\jmeter\pdf_build.py
```

Salidas: `docs/jmeter/informe_knstore.html` y `docs/jmeter/informe_knstore.pdf`.

> Para convertir a PDF en Windows/Linux se necesita un navegador Chromium headless (se instala con `npx playwright install chromium`); si no, el HTML es suficiente.

---

## 7. Problemas comunes

| Síntoma | Causa probable | Solución |
|---|---|---|
| `415 Unsupported Media Type` en logins | Faltó el header `Content-Type: application/json` | Usar los samplers tal cual (ya lo incluyen); no modificar los *Header Manager* |
| `401` en cadena (todo el grupo ADMIN/CLIENTE falla) | Token no válido: usuario no existe/desactivado o clave incorrecta | Crear/verificar usuarios de la sección 3 y credenciales UDV |
| `400 Failed to read request` en `/api/register` | El body JSON se perdió (plan editado a mano o resguardado por GUI vieja) | Volver a abrir el `.jmx` del repositorio y/o usar `File > Reload` |
| `Results file is not empty` (CLI) | El archivo `.jtl` destino ya existe | Borrar el `.jtl` anterior o cambiar el nombre |
| Cambio de IP/dominio del despliegue | El servidor cambió de IP | Actualizar `HOST`/`PORT` en User Defined Variables |
| CP-016 falla tras una re-ejecución | El usuario `borrar01` ya no tiene Cuenta | El plan la re-crea; si el usuario fue borrado, recrearlo en la app |
| El gráfico de línea no dibuja | Falta JFreeChart | Ver sección 2.3 |

---

## 8. Checklist para una ejecución completa

1. Java 21 + JMeter 5.6.3 instalados (y `jmeter` en el PATH).
2. (Opcional) JFreeChart en `lib/` o via `-Juser.classpath`.
3. Usuarios de prueba activos (sección 3).
4. Despliegue accesible (o `HOST`/`PROTOCOL`/`PORT` apuntando al entorno propio).
5. Abrir `docs/knstore_test_plan.jmx` → seleccionar el nodo raíz → **Ctrl+R** (funcional + estrés, ~10-15 min con cargas por defecto).
6. Revisar: verificadores de muestra (VRT), Summary/RTG por caso y sección, y la verificación de stock en ES-04v.
7. (Opcional) Generar informe HTML/PDF como en la sección 6.
