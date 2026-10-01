package utils;

import persistencia.ConfigLoader;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Gestor centralizado de rutas y carpetas del sistema.
 * Administra la raíz 'archivos/', 'FIRMAS/', subcarpetas de recibos, contadores y clientes.
 * Lee nombres configurables del archivo .env si están presentes con valores por defecto.
 *
 * @author Osmar & Antigravity
 */
public class GestorCarpetas {
    private static final String DIR_ARCHIVOS = ConfigLoader.getOrDefault("DIR_ARCHIVOS", "archivos");
    private static final String DIR_RECIBOS = ConfigLoader.getOrDefault("DIR_RECIBOS", "recibos");
    public static final String DIR_FIRMAS = ConfigLoader.getOrDefault("DIR_FIRMAS", "FIRMAS");
    public static final String SIN_CONTADOR = ConfigLoader.getOrDefault("DIR_SIN_CONTADOR", "SIN_CONTADOR");

    /**
     * Retorna y asegura la existencia de la carpeta raíz de archivos.
     */
    public static File getRutaBase() {
        File base = new File(DIR_ARCHIVOS);
        if (!base.exists()) {
            base.mkdirs();
        }
        return base;
    }

    /**
     * Retorna y asegura la existencia de la carpeta raíz de firmas electrónicas (E-Firmas).
     */
    public static File getRutaFirmas() {
        File firmas = new File(DIR_FIRMAS);
        if (!firmas.exists()) {
            firmas.mkdirs();
        }
        return firmas;
    }

    /**
     * Convierte un archivo o ruta a una ruta relativa amigable y portable (con '/')
     * respecto a la raíz de la aplicación si se encuentra dentro de ella.
     */
    public static String aRutaRelativa(File archivo) {
        if (archivo == null) {
            return "";
        }
        try {
            Path base = Paths.get("").toAbsolutePath().normalize();
            Path target = archivo.toPath().toAbsolutePath().normalize();
            if (target.startsWith(base)) {
                return base.relativize(target).toString().replace('\\', '/');
            }
        } catch (Exception ignored) {
        }
        return archivo.getPath().replace('\\', '/');
    }

    /**
     * Convierte una cadena de ruta a relativa si está dentro del directorio raíz.
     */
    public static String aRutaRelativa(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            return "";
        }
        return aRutaRelativa(new File(ruta));
    }

    /**
     * Resuelve un archivo a partir de una ruta relativa o absoluta.
     */
    public static File resolverArchivo(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            return null;
        }
        File f = new File(ruta);
        if (f.isAbsolute()) {
            return f;
        }
        return new File(Paths.get("").toAbsolutePath().toFile(), ruta);
    }

    /**
     * Retorna y asegura la existencia de la carpeta de recibos de pago.
     */
    public static File getRutaRecibos() {
        File recibos = new File(getRutaBase(), DIR_RECIBOS);
        if (!recibos.exists()) {
            recibos.mkdirs();
        }
        return recibos;
    }

    /**
     * Retorna y asegura la existencia de la carpeta de un contador específico.
     */
    public static File getRutaContador(String nombreContador) {
        String safeContador = (nombreContador == null || nombreContador.isBlank() || nombreContador.equalsIgnoreCase("SIN CONTADOR"))
                ? SIN_CONTADOR
                : sanitizar(nombreContador);
        File dir = new File(getRutaBase(), safeContador);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * Retorna y asegura la existencia de la carpeta de un cliente dentro de la carpeta de su contador.
     */
    public static File getRutaCliente(String nombreContador, String nombreCliente) {
        File dirContador = getRutaContador(nombreContador);
        String safeCliente = sanitizar(nombreCliente);
        File dirCliente = new File(dirContador, safeCliente);
        if (!dirCliente.exists()) {
            dirCliente.mkdirs();
        }
        return dirCliente;
    }

    /**
     * Sanitiza nombres de archivos y carpetas para compatibilidad con Windows y sistemas de archivos estándar.
     */
    public static String sanitizar(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "Desconocido";
        }
        // Eliminar caracteres no permitidos en Windows: \ / : * ? " < > |
        String limpio = nombre.trim().replaceAll("[\\\\/:*?\"<>|]", "_");
        while (limpio.endsWith(".") || limpio.endsWith(" ")) {
            limpio = limpio.substring(0, limpio.length() - 1);
        }
        return limpio.isEmpty() ? "Desconocido" : limpio;
    }
}
