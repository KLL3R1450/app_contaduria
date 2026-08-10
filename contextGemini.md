# Contexto Técnico para Gemini (Sesiones Futuras)

Este documento sirve como resumen técnico de la arquitectura, patrones de implementación y cambios realizados para facilitar la continuidad del desarrollo en futuras sesiones con asistentes de inteligencia artificial.

---

## 🏗️ Arquitectura General: MVC (Model-View-Controller)

La aplicación está diseñada bajo el patrón **MVC**:

* **Modelo (`entidades/`)**: Contiene las clases de dominio (`Cliente`, `Contadores`, `Declaracion`, `EFirmas`, `Personas`, `Regimenes`, `Terceros`). `Cliente` y `Terceros` heredan de la clase base abstracta `Personas`.
* **Controlador (`controlador/`)**: La lógica de negocio está centralizada en `Controlador.java` (un Singleton). Este mantiene estructuras en memoria (Maps y Lists) que funcionan como caché del sistema para evitar lecturas repetitivas al disco SQLite.
* **Persistencia (`persistencia/`)**: Clases DAO (`ClientesDAO`, `ContadoresDAO`, `DeclaracionDAO`, `EFirmasDAO`, `RegimenesDAO`, `TercerosDAO`) que se comunican con SQLite utilizando JDBC directo (`data/DespachoDB.db`).
* **Vistas (`UI/`)**: Formularios de interfaz gráfica de usuario Swing.

---

## 🛠️ Stack Tecnológico

* **Lenguaje**: Java 21 (LTS) - *Actualizado desde Java 8*
* **Gestor de dependencias**: Maven (`pom.xml`)
* **Base de datos**: SQLite (Driver `sqlite-jdbc` versión `3.50.1.0`)
* **Aspecto Visual**: FlatLaf & FlatLaf-Extras (versión `3.5.2`)

---

## 🔄 Cambios y Optimizaciones Recientes

### 1. Refactorización y Optimización de la Caché
* **Captura de IDs Generados**: Se modificaron `ClientesDAO.insertCliente`, `TercerosDAO.insertTercero` y `ContadoresDAO.insertContador` para retornar u obtener los IDs autogenerados por la base de datos tras la inserción y asignarlos directamente a las propiedades en memoria.
* **Inserción Directa en Memoria**: En `Controlador.java`, cuando una inserción es exitosa, se añade el objeto directamente a los mapas locales (`clientes.put()`, `terceros.put()`, `contadores.put()`). Esto evita tener que volver a consultar y re-mapear toda la base de datos desde cero, mejorando drásticamente el rendimiento y corrigiendo un bug donde los contadores recién creados no se guardaban en la caché.

### 2. Refactorización del Manejo de Excepciones y Conectores
* **Desacoplamiento de Swing de la persistencia**: Se eliminaron todas las dependencias y llamadas a `JOptionPane` en la capa DAO. Ahora los métodos de base de datos lanzan `RuntimeException` con causas explícitas en caso de fallo crítico SQL.
* **Manejo Centralizado**: En `Controlador.cargarTodo()`, la carga de datos inicial está rodeada por un bloque `try-catch` unificado que captura cualquier error de conexión o consulta al arrancar y muestra un solo mensaje descriptivo al usuario.
* **Visibilidad del Conector**: Se modificó `ConectorBD.getConexion()` de `protected` a `public` para permitir la instanciación de transacciones y sentencias preparadas de actualización del estado directamente en las consultas bajo demanda del Controlador.

### 3. Corrección de Bugs Críticos de SQL y Delegación
* **`TercerosDAO.java`**:
  * Se corrigió la omisión de asignación de parámetros (`ct.setInt`) en `clienteTercero(...)`, que provocaba una excepción de parámetros faltantes al intentar asociar clientes con terceros.
  * Se corrigieron errores tipográficos de nombres de tablas en sentencias SQL: `regiemenes_terceros` $\to$ `regimenes_terceros`, `regimenes_clientes` $\to$ `regimenes_terceros` (en los métodos de terceros) y `regimenes_tercero` $\to$ `regimenes_terceros`.
* **`Controlador.java`**:
  * Se corrigió la delegación en `deleteRegimen`, que erróneamente llamaba a `addRegimen(r)` en lugar de a `deleteRegimen(r.getId())`.
  * Se corrigió el control de éxito de creación de declaraciones, cambiando el retorno del DAO a `"correcto"` para sincronizar con la validación del controlador.

### 4. Rediseño Completo de la Interfaz con FlatLaf (Look and Feel)
* **Dashboard Moderno (`Index.java`)**: 
  * Reemplazo de la barra de menú antigua por un panel de navegación lateral (Sidebar) elegante.
  * Inclusión de tarjetas informativas (KPI Cards) con estadísticas en tiempo real y una tabla re-estilizada con FlatLaf para visualizar las e-firmas próximas a vencer.
  * Botón para alternar de forma animada (`FlatAnimatedLafChange`) entre temas Claro (`FlatLightLaf`) y Oscuro (`FlatDarkLaf`).
* **Unificación de Acciones (`BuscarPersonas.java`)**:
  * Se rediseñó el diálogo de búsqueda para centrar todas las operaciones de administración (Agregar, Ver Detalles, Eliminar) mediante botones de acceso rápido integrados en la zona inferior de la ventana, eliminando botones redundantes en la barra lateral del menú.
* **Corrección de Apilamiento de Diálogos**:
  * En `BuscarPersonas.java`, se sustituyó el valor `null` por el frame padre real `(java.awt.Frame) this.getParent()` al instanciar `DetallesClientes` y `DetallesTerceros`. Esto evita que las ventanas modales de detalles queden atrapadas detrás de otros diálogos o congelen la interfaz de usuario bajo el esquema decorado de FlatLaf.

### 5. Módulo de Declaraciones
* **Sincronización Bidireccional en Controlador**: Se añadieron métodos para buscar declaraciones específicas por mes/año (`obtenerOCrearDeclaracion`) que crean registros sobre la marcha si no existen, y métodos `toggleGastos`, `toggleIngresos` y `toggleDeclarado` para sincronizar con el SQLite de forma atómica y reactiva.
* **Vistas de Declaración**:
  * **`VerListasContadores.java`**: JDialog que permite elegir un Contador a través de un ComboBox, ver su lista de clientes e indagar individualmente el historial de declaraciones mensuales.
  * **`DeclaracionesContadores.java`**: Formulario interactivo que lista la cartera del contador por mes y año seleccionado, usando JTable con CheckBoxes editables que persisten inmediatamente los estados al ser tildados/destildados.
  * **Acceso desde el Sidebar**: Se integraron accesos rápidos "Listas Contadores" y "Declaraciones" en el panel lateral de `Index.java`.
