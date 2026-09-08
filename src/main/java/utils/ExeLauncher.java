package utils;

import javax.swing.*;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestor utilitario para el lanzamiento y control de ciclo de vida de subprogramas
 * empaquetados en archivos ejecutables (.exe).
 *
 * Características clave:
 * 1. Búsqueda dinámica de ejecutables por extensión (.exe) dentro de subcarpetas dedicadas.
 * 2. Ejecución asíncrona sin bloqueo del hilo de interfaz gráfica (Swing EDT).
 * 3. Prevención de fugas de memoria y bloqueos de búfer de E/S mediante drenado continuo en segundo plano.
 * 4. Control de instancias únicas para evitar saturación de memoria por ejecuciones duplicadas.
 * 5. Re-enfoque automático de la ventana principal al cerrarse el subprograma.
 * 6. Limpieza segura de procesos y streams al cerrar la aplicación principal.
 *
 * @author Osmar & Antigravity
 */
public class ExeLauncher {

    // Directorio base de los subprogramas
    public static final String RUTA_BASE_SUBPROGRAMAS = "subprogramas";
    public static final String CARPETA_DESCARGA_MASIVA = "subprogramas/descarga_masiva";
    public static final String CARPETA_LECTOR_XML = "subprogramas/lector_xml";

    // Mapa para rastrear subprocesos activos por identificador
    private static final Map<String, Process> procesosActivos = new ConcurrentHashMap<>();

    static {
        // Hook de apagado de la JVM para asegurar que no queden procesos huérfanos al salir de la app
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Map.Entry<String, Process> entry : procesosActivos.entrySet()) {
                Process p = entry.getValue();
                if (p != null && p.isAlive()) {
                    try {
                        p.destroyForcibly();
                    } catch (Exception ignored) {
                    }
                }
            }
        }, "ExeLauncher-ShutdownHook"));
    }

    /**
     * Lanza el subprograma de Descarga Masiva (SAT CFDI).
     * @param parentWindow Ventana principal de Swing para mensajes y foco.
     */
    public static void lanzarDescargaMasiva(Window parentWindow) {
        ejecutarSubprograma("descarga_masiva", "Descarga Masiva", CARPETA_DESCARGA_MASIVA, parentWindow);
    }

    /**
     * Lanza el subprograma de Lector XML.
     * @param parentWindow Ventana principal de Swing para mensajes y foco.
     */
    public static void lanzarLectorXml(Window parentWindow) {
        ejecutarSubprograma("lector_xml", "Lector XML", CARPETA_LECTOR_XML, parentWindow);
    }

    /**
     * Busca el primer archivo .exe en el directorio especificado y lo ejecuta en un subproceso independiente.
     *
     * @param idSubprograma Identificador único para control de concurrencia.
     * @param nombreVisible Nombre representativo para alertas visuales al usuario.
     * @param rutaCarpeta   Ruta relativa o absoluta de la carpeta que contiene el ejecutable.
     * @param parentWindow  Ventana de la aplicación que invocó el subprograma.
     */
    public static void ejecutarSubprograma(String idSubprograma, String nombreVisible, String rutaCarpeta, Window parentWindow) {
        // 1. Verificar si ya hay una instancia activa de este subprograma
        Process procesoExistente = procesosActivos.get(idSubprograma);
        if (procesoExistente != null && procesoExistente.isAlive()) {
            JOptionPane.showMessageDialog(
                    parentWindow,
                    "El subprograma \"" + nombreVisible + "\" ya se encuentra abierto y en ejecución.",
                    "Programa en Ejecución",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        // 2. Validar que exista el directorio
        File carpeta = new File(rutaCarpeta);
        if (!carpeta.exists() || !carpeta.isDirectory()) {
            // Intentar crearlo para facilitar la organización al usuario
            carpeta.mkdirs();
            JOptionPane.showMessageDialog(
                    parentWindow,
                    "No se encontró la carpeta del subprograma:\n" + carpeta.getAbsolutePath() +
                    "\n\nSe ha creado la carpeta automáticamente. Por favor coloque el ejecutable (.exe) dentro de ella.",
                    "Ejecutable No Encontrado",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 3. Buscar dinámicamente cualquier archivo con extensión .exe
        File[] ejecutables = carpeta.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".exe");
            }
        });

        if (ejecutables == null || ejecutables.length == 0) {
            JOptionPane.showMessageDialog(
                    parentWindow,
                    "No se encontró ningún archivo ejecutable (.exe) dentro de:\n" + carpeta.getAbsolutePath(),
                    "Archivo .exe no encontrado",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Se selecciona el ejecutable encontrado
        File archivoExe = ejecutables[0];

        // 4. Ejecución en un hilo en segundo plano (Worker/Daemon) para no bloquear la UI de Swing
        Thread launchThread = new Thread(() -> {
            Process proceso = null;
            try {
                ProcessBuilder pb = new ProcessBuilder(archivoExe.getAbsolutePath());
                // Establecer el directorio de trabajo en la carpeta del ejecutable (para que encuentre sus dependencias locales)
                pb.directory(carpeta);
                // Redirigir errores al stream estándar para procesar un solo canal
                pb.redirectErrorStream(true);

                proceso = pb.start();
                procesosActivos.put(idSubprograma, proceso);

                // Drenado continuo de la salida estándar en un hilo demonio para evitar que el búfer de Windows se llene y congele el proceso
                final Process procFinal = proceso;
                Thread streamDrainer = new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(procFinal.getInputStream()))) {
                        String linea;
                        while ((linea = reader.readLine()) != null) {
                            // Opcional: registrar en consola de desarrollo si es necesario
                            // System.out.println("[" + nombreVisible + "] " + linea);
                        }
                    } catch (IOException ignored) {
                    }
                }, "StreamDrainer-" + idSubprograma);
                streamDrainer.setDaemon(true);
                streamDrainer.start();

                // Esperar a que el proceso termine
                int exitCode = proceso.waitFor();

            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                            parentWindow,
                            "Error al intentar iniciar el subprograma \"" + nombreVisible + "\":\n" + ex.getMessage(),
                            "Error de Ejecución",
                            JOptionPane.ERROR_MESSAGE
                    );
                });
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } finally {
                // 5. Limpieza de recursos y memoria
                if (proceso != null) {
                    try {
                        proceso.getInputStream().close();
                    } catch (Exception ignored) {}
                    try {
                        proceso.getOutputStream().close();
                    } catch (Exception ignored) {}
                    try {
                        proceso.getErrorStream().close();
                    } catch (Exception ignored) {}
                }
                procesosActivos.remove(idSubprograma);

                // 6. Devolver el foco a la ventana principal de Swing
                if (parentWindow != null) {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            if (parentWindow.isDisplayable()) {
                                parentWindow.toFront();
                                parentWindow.requestFocus();
                                parentWindow.repaint();
                            }
                        } catch (Exception ignored) {}
                    });
                }
            }
        }, "Launcher-" + idSubprograma);

        launchThread.setDaemon(true);
        launchThread.start();
    }

    /**
     * Verifica si un subprograma específico se encuentra actualmente en ejecución.
     */
    public static boolean isSubprogramaActivo(String idSubprograma) {
        Process p = procesosActivos.get(idSubprograma);
        return p != null && p.isAlive();
    }
}
