# Inicialización del Proyecto: Proyecto_Despacho (app_contaduria)

Este documento detalla el estado actual del proyecto, su estructura general, los requisitos del sistema y las tecnologías utilizadas.

---

## 🛠️ Tecnologías y Dependencias

El proyecto está desarrollado utilizando las siguientes tecnologías principales:

* **Lenguaje de Programación**: Java 21 (LTS)
* **Gestor de Proyectos y Dependencias**: Maven (especificado en `pom.xml`)
* **Base de Datos**: SQLite
* **Driver JDBC**: `sqlite-jdbc` de `org.xerial` (Versión `3.50.1.0`)
* **Librería de Temas**: FlatLaf & FlatLaf-Extras (`3.5.2`)
* **Interfaz de Usuario (UI)**: Java Swing (AWT/Swing para entorno de escritorio)

---

## 📂 Estructura de Directorios

El código fuente sigue la convención estándar de un proyecto Maven:

```text
app_contaduria/
│
├── .idea/                      # Configuración del IDE (IntelliJ IDEA)
├── data/                       # Base de datos SQLite
│   ├── DespachoDB.db           # Base de datos principal
│   └── Despacho_DB.db          # Base de datos alternativa/respaldo
├── src/
│   └── main/
│       └── java/
│           ├── UI/             # Vistas de la interfaz gráfica Swing (.java)
│           ├── controlador/     # Lógica controladora del patrón MVC
│           ├── entidades/      # Clases POJO (modelos de datos)
│           ├── main/           # Clase de arranque (Main.java)
│           ├── persistencia/   # Acceso a datos (DAOs y conexión JDBC)
│           └── utils/          # Utilidades (validadores)
├── pom.xml                     # Configuración de dependencias Maven
├── nb-configuration.xml        # Configuración de NetBeans (si aplica)
└── nbactions.xml               # Acciones personalizadas de NetBeans/Maven
```

---

## 🚀 Cómo Ejecutar el Proyecto

### Requisitos Previos
1. Tener instalado el **JDK 8** (o superior).
2. Tener instalado **Maven**.

### Compilación y Ejecución desde Terminal

Para compilar el proyecto:
```bash
mvn clean compile
```

Para empaquetar en un archivo JAR:
```bash
mvn clean package
```

Para iniciar la aplicación usando Maven:
```bash
mvn exec:java -Dexec.mainClass="main.Main"
```

---

## 🌐 Configuración de la Base de Datos

La conexión a la base de datos está centralizada en la clase abstracta `persistencia.ConectorBD`. Utiliza una ruta relativa local a la carpeta `data/`:

* **Ruta de Conexión**: `jdbc:sqlite:data/DespachoDB.db`
* **Driver**: SQLite JDBC Driver (`sqlite-jdbc`)
