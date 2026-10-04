package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Inicializador automático del esquema de Base de Datos MySQL.
 * Verifica la existencia de la base de datos, tablas, catálogos iniciales y vistas;
 * en caso de no encontrarlos, los crea y puebla automáticamente al iniciar el sistema.
 *
 * @author Osmar & Antigravity
 */
public class InicializadorBD {

    private static boolean verificado = false;

    /**
     * Asegura la creación de la base de datos, tablas, relaciones y vistas del sistema.
     */
    public static synchronized void asegurarEstructuraBD() {
        if (verificado) return;

        String rawUri = ConfigLoader.getOrDefault("DB_URI", "localhost:3306/despachito_db?useSSL=false&serverTimezone=America/Mexico_City&allowPublicKeyRetrieval=true");
        String user = ConfigLoader.getOrDefault("DB_USER", "root");
        String password = ConfigLoader.getOrDefault("DB_PASSWORD", "");

        // 1. Extraer nombre de la base de datos, servidor y parámetros
        String cleanUri = rawUri.replace("jdbc:", "").replace("mysql://", "");
        String hostPort = "localhost:3306";
        String dbName = "despacho_db";
        String params = "useSSL=false&serverTimezone=America/Mexico_City&allowPublicKeyRetrieval=true&connectTimeout=5000";

        try {
            int slashIdx = cleanUri.indexOf('/');
            if (slashIdx != -1) {
                hostPort = cleanUri.substring(0, slashIdx);
                String rest = cleanUri.substring(slashIdx + 1);
                int qIdx = rest.indexOf('?');
                if (qIdx != -1) {
                    dbName = rest.substring(0, qIdx);
                    params = rest.substring(qIdx + 1);
                } else {
                    dbName = rest;
                }
            }
        } catch (Exception e) {
            System.err.println("Aviso al parsear DB_URI: " + e.getMessage());
        }

        // 2. Conectar al servidor MySQL para crear la base de datos si no existe
        String serverUrl = "jdbc:mysql://" + hostPort + "/?" + params;
        try (Connection serverConn = DriverManager.getConnection(serverUrl, user, password);
             Statement stmt = serverConn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + dbName + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
            System.out.println("Verificación de base de datos '" + dbName + "' completada.");

        } catch (SQLException e) {
            System.err.println("Aviso al verificar/crear base de datos en el servidor: " + e.getMessage());
        }

        // 3. Conectar a la base de datos específica para crear tablas, vistas y datos iniciales
        String dbUrl = "jdbc:mysql://" + hostPort + "/" + dbName + "?" + params;
        try (Connection dbConn = DriverManager.getConnection(dbUrl, user, password);
             Statement stmt = dbConn.createStatement()) {

            crearTablas(stmt);
            sembrarCatalogos(stmt);
            crearVistas(stmt);

            verificado = true;
            System.out.println("Estructura de tablas y vistas de '" + dbName + "' verificada exitosamente.");

        } catch (SQLException e) {
            System.err.println("Error durante la inicialización de tablas/vistas: " + e.getMessage());
        }
    }

    private static void crearTablas(Statement stmt) throws SQLException {
        // 1. Estados
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS estados (" +
                "  id_estado INT PRIMARY KEY AUTO_INCREMENT," +
                "  nombre_estado VARCHAR(50) NOT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;"
        );

        // 2. Regímenes
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS regimenes (" +
                "  id_regimen INT PRIMARY KEY AUTO_INCREMENT," +
                "  des_regimen VARCHAR(255) NOT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;"
        );

        // 3. Contadores
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS contadores (" +
                "  id_contador INT PRIMARY KEY AUTO_INCREMENT," +
                "  nombre_contador VARCHAR(150) NOT NULL," +
                "  contacto_contador VARCHAR(100) DEFAULT 'SIN CONTACTO'," +
                "  id_estado INT DEFAULT 1," +
                "  FOREIGN KEY (id_estado) REFERENCES estados(id_estado)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;"
        );

        // 4. Clientes
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS clientes (" +
                "  id_cliente INT PRIMARY KEY AUTO_INCREMENT," +
                "  nombre_cliente VARCHAR(200) NOT NULL," +
                "  rfc_cliente VARCHAR(20) NOT NULL," +
                "  cp_cliente VARCHAR(10)," +
                "  correo_cliente VARCHAR(100)," +
                "  m_honorarios_cliente INT DEFAULT 0," +
                "  id_contador INT NULL," +
                "  id_estado INT DEFAULT 1," +
                "  INDEX idx_clientes_contador (id_contador)," +
                "  INDEX idx_clientes_estado (id_estado)," +
                "  FOREIGN KEY (id_contador) REFERENCES contadores(id_contador) ON DELETE SET NULL," +
                "  FOREIGN KEY (id_estado) REFERENCES estados(id_estado)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;"
        );

        // 5. Terceros
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS terceros (" +
                "  id_tercero INT PRIMARY KEY AUTO_INCREMENT," +
                "  nombre_tercero VARCHAR(200) NOT NULL," +
                "  rfc_tercero VARCHAR(20) NOT NULL," +
                "  cp_tercero VARCHAR(10)," +
                "  correo_tercero VARCHAR(100)," +
                "  id_estado INT DEFAULT 1," +
                "  INDEX idx_terceros_estado (id_estado)," +
                "  FOREIGN KEY (id_estado) REFERENCES estados(id_estado)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;"
        );

        // 6. Regímenes Clientes
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS regimenes_clientes (" +
                "  id_cliente INT NOT NULL," +
                "  id_regimen INT NOT NULL," +
                "  PRIMARY KEY (id_cliente, id_regimen)," +
                "  FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) ON DELETE CASCADE," +
                "  FOREIGN KEY (id_regimen) REFERENCES regimenes(id_regimen) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;"
        );

        // 7. Regímenes Terceros
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS regimenes_terceros (" +
                "  id_tercero INT NOT NULL," +
                "  id_regimen INT NOT NULL," +
                "  PRIMARY KEY (id_tercero, id_regimen)," +
                "  FOREIGN KEY (id_tercero) REFERENCES terceros(id_tercero) ON DELETE CASCADE," +
                "  FOREIGN KEY (id_regimen) REFERENCES regimenes(id_regimen) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;"
        );

        // 8. Terceros Clientes
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS terceros_clientes (" +
                "  id_cliente INT NOT NULL," +
                "  id_tercero INT NOT NULL," +
                "  PRIMARY KEY (id_cliente, id_tercero)," +
                "  FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) ON DELETE CASCADE," +
                "  FOREIGN KEY (id_tercero) REFERENCES terceros(id_tercero) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;"
        );

        // 9. E-Firmas
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS e_firmas (" +
                "  id_firma INT PRIMARY KEY AUTO_INCREMENT," +
                "  id_cliente INT NOT NULL UNIQUE," +
                "  fecha_renovacion VARCHAR(20) NOT NULL," +
                "  fecha_expiracion VARCHAR(20) NOT NULL," +
                "  ruta_certificado VARCHAR(500)," +
                "  ruta_key VARCHAR(500)," +
                "  contrasena VARCHAR(500)," +
                "  INDEX idx_efirmas_cliente (id_cliente)," +
                "  FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;"
        );

        // 10. Declaraciones Clientes
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS declaraciones_clientes (" +
                "  id_declaracion INT PRIMARY KEY AUTO_INCREMENT," +
                "  id_cliente INT NOT NULL," +
                "  anio INT NOT NULL," +
                "  mes INT NOT NULL," +
                "  gastos INT DEFAULT 0," +
                "  ingresos INT DEFAULT 0," +
                "  declarado INT DEFAULT 0," +
                "  UNIQUE KEY uq_declaracion_periodo (id_cliente, anio, mes)," +
                "  INDEX idx_declaraciones_periodo (anio, mes)," +
                "  FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;"
        );

        // 11. Pagos Clientes
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS pagos_clientes (" +
                "  id_pago INT PRIMARY KEY AUTO_INCREMENT," +
                "  id_cliente INT NOT NULL," +
                "  anio INT NOT NULL," +
                "  mes INT NOT NULL," +
                "  monto INT NOT NULL," +
                "  fecha_pago VARCHAR(30) NOT NULL," +
                "  UNIQUE KEY uq_pago_periodo (id_cliente, anio, mes)," +
                "  INDEX idx_pagos_cliente (id_cliente)," +
                "  FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;"
        );
        
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS peticiones (" +
                "  id_peticion INT PRIMARY KEY AUTO_INCREMENT," +
                "  id_cliente INT NOT NULL," +
                "  solicitud VARCHAR(255)NOT NULL," +
                "  estado VARCHAR(50) NOT NULL DEFAULT 'en proceso'," +
                "  fecha_inicial VARCHAR(50)," +
                "  fecha_final VARCHAR(50)," +
                "  tipo VARCHAR(100),"+
                "  INDEX idx_peticiones_cliente (id_cliente)," +
                "  INDEX idx_peticiones_solicitud (solicitud)," +
                "  INDEX idx_peticiones_estado (estado)," +
                "  FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) ON DELETE RESTRICT ON UPDATE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;"  
        );
    }

    private static void sembrarCatalogos(Statement stmt) throws SQLException {
        // Sembrar Estados si está vacío
        try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM estados")) {
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate("INSERT INTO estados (id_estado, nombre_estado) VALUES (1, 'Activo'), (2, 'Inactivo'), (3, 'Eliminado')");
                System.out.println("Catálogo 'estados' inicializado.");
            }
        }

        // Sembrar Regímenes SAT si está vacío
        try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM regimenes")) {
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate(
                    "INSERT INTO regimenes (id_regimen, des_regimen) VALUES " +
                                "(601, 'GENERAL DE LEY PERSONAS MORALES'), " +
                                "(602, 'SIMPLIFICADO DE LEY PERSONAS MORALES'), " +
                                "(603, 'PERSONAS MORALES CON FINES NO LUCRATIVOS'), " +
                                "(604, 'REGIMEN DE PEQUEÑOS CONTRIBUYENTES'), " +
                                "(605, 'SUELDOS Y SALARIOS E INGRESOS ASIMILADOS A SALARIOS'), " +
                                "(606, 'ARRENDAMIENTO'), " +
                                "(607, 'ENAJENACION O ADQUISICION DE BIENES'), " +
                                "(608, 'DE LOS DEMAS INGRESOS'), " +
                                "(609, 'CONSOLIDACION'), " +
                                "(610, 'RESIDENTES EN EL EXTRANJERO SIN ESTABLECIMIENTO PERMANENTE EN MEXICO'), " +
                                "(611, 'DE INGRESOS POR DIVIDENDOS'), " +
                                "(612, 'PERSONAS FISICAS CON ACTIVIDADES EMPRESARIALES Y PROFESIONALES'), " +
                                "(613, 'INTERMEDIO DE LAS PERSONAS FISICAS CON ACTIVIDADES EMPRESARIALES'), " +
                                "(614, 'DE LOS INGRESOS POR INTERESES'), " +
                                "(615, 'DE LOS INGRESOS POR OBTENCION DE PREMIOS'), " +
                                "(616, 'SIN OBLIGACIONES FISCALES'), " +
                                "(617, 'PEMEX'), " +
                                "(618, 'SIMPLIFICADO DE LEY PERSONAS FISICAS'), " +
                                "(619, 'INGRESOS POR LA OBTENCION DE PRESTAMOS'), " +
                                "(620, 'SOCIEDADES COOPERATIVAS DE PRODUCCION QUE OPTAN POR DIFERIR SUS INGRESOS'), " +
                                "(621, 'INCORPORACION FISCAL'), " +
                                "(622, 'ACTIVIDADES AGRICOLAS, GANADERAS, SILVICOLAS Y PESQUERAS'), " +
                                "(623, 'OPCIONAL PARA GRUPOS DE SOCIEDADES'), " +
                                "(624, 'COORDINADOS'), " +
                                "(625, 'ACTIVIDADES EMPRESARIALES CON INGRESOS A TRAVES DE PLATAFORMAS TECNOLOGICAS'), " +
                                "(626, 'SIMPLIFICADO DE CONFIANZA')"
                );
                System.out.println("Catálogo 'regimenes' inicializado con regímenes SAT estándar.");
            }
        }
    }

    private static void crearVistas(Statement stmt) throws SQLException {
        // 1. Vista de Clientes Activos
        stmt.executeUpdate(
                "CREATE OR REPLACE VIEW vw_clientes_activos AS " +
                "SELECT " +
                "    c.id_cliente, " +
                "    c.nombre_cliente, " +
                "    c.rfc_cliente, " +
                "    c.cp_cliente, " +
                "    c.correo_cliente, " +
                "    c.m_honorarios_cliente, " +
                "    c.id_contador, " +
                "    COALESCE(co.nombre_contador, 'SIN CONTADOR') AS nombre_contador, " +
                "    c.id_estado " +
                "FROM clientes c " +
                "LEFT JOIN contadores co ON c.id_contador = co.id_contador " +
                "WHERE c.id_estado = 1;"
        );

        // 2. Vista para Copiar SAT (Clientes)
        stmt.executeUpdate(
                "CREATE OR REPLACE VIEW vw_copiar_sat_clientes AS " +
                "SELECT " +
                "    c.id_cliente, " +
                "    c.nombre_cliente, " +
                "    c.rfc_cliente, " +
                "    c.cp_cliente, " +
                "    COALESCE(GROUP_CONCAT(r.des_regimen SEPARATOR ', '), 'Sin Regímenes') AS regimenes_fiscales " +
                "FROM clientes c " +
                "LEFT JOIN regimenes_clientes rc ON c.id_cliente = rc.id_cliente " +
                "LEFT JOIN regimenes r ON rc.id_regimen = r.id_regimen " +
                "WHERE c.id_estado = 1 " +
                "GROUP BY c.id_cliente, c.nombre_cliente, c.rfc_cliente, c.cp_cliente;"
        );

        // 3. Vista para Copiar SAT (Terceros)
        stmt.executeUpdate(
                "CREATE OR REPLACE VIEW vw_copiar_sat_terceros AS " +
                "SELECT " +
                "    t.id_tercero, " +
                "    t.nombre_tercero, " +
                "    t.rfc_tercero, " +
                "    t.cp_tercero, " +
                "    COALESCE(GROUP_CONCAT(r.des_regimen SEPARATOR ', '), 'Sin Regímenes') AS regimenes_fiscales " +
                "FROM terceros t " +
                "LEFT JOIN regimenes_terceros rt ON t.id_tercero = rt.id_tercero " +
                "LEFT JOIN regimenes r ON rt.id_regimen = r.id_regimen " +
                "WHERE t.id_estado = 1 " +
                "GROUP BY t.id_tercero, t.nombre_tercero, t.rfc_tercero, t.cp_tercero;"
        );

        // 4. Vista de Semáforo de E-Firmas
        stmt.executeUpdate(
                "CREATE OR REPLACE VIEW vw_semaforo_efirmas AS " +
                "SELECT " +
                "    c.id_cliente, " +
                "    c.nombre_cliente, " +
                "    c.rfc_cliente, " +
                "    ef.fecha_expiracion, " +
                "    ef.fecha_renovacion, " +
                "    ef.ruta_certificado, " +
                "    ef.ruta_key, " +
                "    ef.contrasena, " +
                "    DATEDIFF(STR_TO_DATE(ef.fecha_expiracion, '%d/%m/%Y'), CURDATE()) AS dias_restantes, " +
                "    CASE " +
                "        WHEN DATEDIFF(STR_TO_DATE(ef.fecha_expiracion, '%d/%m/%Y'), CURDATE()) < 0 THEN 'VENCIDA' " +
                "        WHEN DATEDIFF(STR_TO_DATE(ef.fecha_expiracion, '%d/%m/%Y'), CURDATE()) <= 30 THEN 'URGENTE' " +
                "        WHEN DATEDIFF(STR_TO_DATE(ef.fecha_expiracion, '%d/%m/%Y'), CURDATE()) <= 365 THEN 'PROXIMA' " +
                "        ELSE 'VIGENTE' " +
                "    END AS estado_alerta " +
                "FROM clientes c " +
                "JOIN e_firmas ef ON c.id_cliente = ef.id_cliente " +
                "WHERE c.id_estado = 1;"
        );
    }
}
