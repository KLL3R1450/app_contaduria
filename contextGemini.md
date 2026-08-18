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
* **Inserción Directa en Memoria**: En `Controlador.java`, cuando una inserción es exitosa, se añade el objeto directamente a los mapas locales (`clientes.put()`, `terceros.put()`, `contadores.put()`). Esto evita tener que volver a consultar y re-mapear toda la base de datos desde cero, mejorando drásticamente el rendimiento.

### 2. Refactorización del Manejo de Excepciones y Conectores
* **Desacoplamiento de Swing de la persistencia**: Se eliminaron todas las dependencias y llamadas a `JOptionPane` en la capa DAO. Ahora los métodos de base de datos lanzan `RuntimeException` con causas explícitas en caso de fallo crítico SQL.
* **Manejo Centralizado**: En `Controlador.cargarTodo()`, la carga de datos inicial está rodeada por un bloque `try-catch` unificado que captura cualquier error de conexión o consulta al arrancar.
* **Visibilidad del Conector**: Se modificó `ConectorBD.getConexion()` de `protected` a `public` para permitir la instanciación de transacciones y sentencias preparadas de actualización del estado directamente en las consultas bajo demanda del Controlador y las vistas auxiliares de inserción.

### 3. Corrección de Bugs Críticos de SQL y Delegación
* **`TercerosDAO.java`**:
  * Se corrigió la omisión de asignación de parámetros (`ct.setInt`) en `clienteTercero(...)` al asociar clientes con terceros.
  * Se corrigieron errores tipográficos de nombres de tablas en sentencias SQL (`regiemenes_terceros` $\to$ `regimenes_terceros`, etc.).
* **`Controlador.java`**:
  * Se corrigió la delegación en `deleteRegimen`, que erróneamente llamaba a `addRegimen(r)`.
  * Se corrigió el control de éxito de creación de declaraciones, cambiando el retorno del DAO a `"correcto"`.

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

### 6. Módulo de E-Firmas (Rama `efirmas`)
* **Semáforo Visual en Dashboard**: Se modificó `Index.java` para incorporar una columna de semáforo de vigencia en base a fechas de expiración:
  * 🟢 **Verde (Vigente)**: Expiración $\ge$ 1 año (365 días).
  * 🟡 **Amarillo (Próximo a vencer)**: Expiración entre 1 mes y 1 año (30 a 365 días).
  * 🔴 **Rojo (Vencido / Urgente)**: Expiración $<$ 1 mes.
* **Ventana de Edición (`EditarFirmaDialog.java`)**: Formulario modal rápido que valida formatos e inserta o renueva firmas directo en base de datos.
* **Interacción Rápida**:
  * Doble clic en la tabla de firmas en `Index.java` abre directamente la edición de la firma.
  * Al pulsar el botón "Editar E-Firma" (`cambiar1`) desde la vista de detalles de cliente, se despliega la pantalla modal.
  * Al consultar la firma de un cliente que no tiene una registrada, un diálogo interactivo ofrece añadirla de inmediato.
  * En la ventana de búsqueda de firmas, el botón **➕ Agregar** permite registrar una nueva si el cliente no posee una (evitando duplicados).

### 7. Copiado Rápido al Portapapeles (SAT)
* **Botón "📋 Copiar SAT"** (`BuscarPersonas.java`): Se añadió un botón para copiar rápidamente la información fiscal de la persona seleccionada al portapapeles en un formato amigable para el portal del SAT (Razón Social, RFC, Código Postal y listado de Regímenes traducidos).

### 8. Módulo de Pagos y Recibos
* **Persistencia**: Se implementó la tabla `pagos_clientes` para registrar los meses cobrados por cliente (con llaves únicas `id_cliente, anio, mes` para evitar cobros duplicados).
* **Lógica del PDF**: Integración de la biblioteca `org.apache.pdfbox` para completar de forma automatizada los campos editables (`fecha`, `cliente`, `periodos`, y `monto`) de la plantilla `recibo.pdf` en la raíz del proyecto.
* **Interfaz de Usuario**:
  * Ventana modal interactiva `UI.GenerarReciboDialog` para seleccionar los periodos pendientes de pago, deshabilitar automáticamente los meses ya pagados, ingresar conceptos adicionales/extras de cobro (declaraciones adicionales, trámites especiales) y registrar el pago en lote (un registro por mes pagado) tras confirmación interactiva.
  * Botón **💵 Generar Recibo** incorporado en el panel de acciones de `UI.BuscarPersonas` (habilitado únicamente para la búsqueda de clientes).

### 9. Mejoras, Corrección de Bugs y Visualizador de Pagos
* **Buscador Dinámico de Clientes (`VerListasContadores.java`)**: Se añadió un campo de texto `JTextField` con un `DocumentListener` para filtrar en tiempo real los clientes que pertenecen a un contador específico por su nombre.
* **Corrección de Regímenes (`CambiarRegimenes.java`)**: Se corrigió una omisión crítica en el constructor del diálogo que no asignaba el parámetro `tp` al atributo `this.tipoPersonas`, lo cual impedía persistir de forma correcta las modificaciones de regímenes tributarios de los clientes.
* **Control de Versiones y Limpieza**: Actualización de `.gitignore` para ignorar la carpeta completa `.idea/` de configuración de IntelliJ y el directorio de salida de PDFs `recibos_generados/`, desvinculando estos elementos previamente rastreados del repositorio de Git sin borrarlos localmente.
* **Módulo Ver Pagos y Deudas (`VerRecibosDialog.java` & `BuscarPersonas.java`)**:
  * Adición del botón **📄 Ver Pagos** en la ventana de búsqueda de clientes.
  * Implementación del diálogo `VerRecibosDialog` para visualizar gráficamente los 12 meses de cobro de un año seleccionado. Los pagos registrados aparecen en verde (con fecha e importe de pago), mientras que los meses pendientes/adeudos aparecen en rojo mostrando el monto a deber en base al honorario mensual del cliente. Muestra totales del año para control del despacho.
