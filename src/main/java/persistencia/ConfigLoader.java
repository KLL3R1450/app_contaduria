package persistencia;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Carga de variables de entorno y configuración de conexión de la base de datos
 * desde el archivo .env en la raíz del proyecto.
 * @author Antigravity
 */
public class ConfigLoader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = new FileInputStream(".env")) {
            properties.load(input);
        } catch (IOException ex) {
            // Valores por defecto si no existe el archivo .env
            properties.setProperty("DB_URI", "jdbc:mysql://localhost:3306/despacho_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
            properties.setProperty("DB_USER", "root");
            properties.setProperty("DB_PASSWORD", "");
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
