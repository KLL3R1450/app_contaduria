package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Administrador de Pool de Conexiones resiliente para MySQL.
 * Provee conexiones reciclables con validación de salud (health check),
 * reconexión transparente ante microcortes de red y timeouts rápidos de 5 segundos.
 * @author Angel Osmar Aguilar Lopez & Antigravity
 */
public abstract class ConectorBD {

    private static final int MAX_POOL_SIZE = 10;
    private static final int MIN_IDLE = 2;
    private static final int TIMEOUT_SEGUNDOS = 5;
    private static final BlockingQueue<Connection> pool = new ArrayBlockingQueue<>(MAX_POOL_SIZE);
    private static boolean inicializado = false;

    static {
        inicializarPool();
    }

    private static synchronized void inicializarPool() {
        if (inicializado) return;
        try {
            DriverManager.setLoginTimeout(TIMEOUT_SEGUNDOS);
            for (int i = 0; i < MIN_IDLE; i++) {
                Connection conn = crearNuevaConexionFisica();
                if (conn != null) {
                    pool.offer(conn);
                }
            }
            inicializado = true;
            System.out.println("Pool de conexiones inicializado con " + pool.size() + " conexiones activas.");
        } catch (Exception e) {
            System.err.println("Aviso al inicializar pool de conexiones inicial: " + e.getMessage());
            inicializado = true; // Permite que se sigan creando conexiones bajo demanda
        }
    }

    private static Connection crearNuevaConexionFisica() throws SQLException {
        String dbPath = "jdbc:" + ConfigLoader.get("DB_URI");
        String user = ConfigLoader.get("DB_USER");
        String password = ConfigLoader.get("DB_PASSWORD");

        if (dbPath == null || user == null) {
            throw new SQLException("Configuración de base de datos no encontrada en .env");
        }

        // Añadir timeouts de red y reconexión si no están presentes
        if (!dbPath.contains("connectTimeout")) {
            String separator = dbPath.contains("?") ? "&" : "?";
            dbPath += separator + "connectTimeout=5000&socketTimeout=15000&autoReconnect=true";
        }

        DriverManager.setLoginTimeout(TIMEOUT_SEGUNDOS);
        return DriverManager.getConnection(dbPath, user, password);
    }

    /**
     * Obtiene una conexión activa y validada del pool.
     * Si la conexión se perdió por inactividad o corte de red, se descarta y se reconecta automáticamente.
     * Envuelve la conexión física para que al invocar close() se devuelva al pool automáticamente.
     * @return java.sql.Connection
     * @throws SQLException si la base de datos central no está disponible
     */
    public static Connection getConexion() throws SQLException {
        Connection conn;

        // Intentar reciclar una conexión viva del pool
        while ((conn = pool.poll()) != null) {
            try {
                if (!conn.isClosed() && conn.isValid(2)) {
                    return wrapConnection(conn);
                } else {
                    try { conn.close(); } catch (Exception ignored) {}
                }
            } catch (SQLException e) {
                try { conn.close(); } catch (Exception ignored) {}
            }
        }

        // Si el pool está vacío o las conexiones expiraron, crear una nueva conexión física
        Connection nueva = crearNuevaConexionFisica();
        return wrapConnection(nueva);
    }

    /**
     * Envuelve la conexión física mediante un Proxy dinámico para interceptar el método close()
     * y devolver la conexión al pool en lugar de destruirla.
     */
    private static Connection wrapConnection(final Connection physicalConnection) {
        return (Connection) java.lang.reflect.Proxy.newProxyInstance(
                ConectorBD.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                (proxy, method, args) -> {
                    if ("close".equals(method.getName())) {
                        try {
                            if (!physicalConnection.isClosed() && physicalConnection.isValid(1)) {
                                if (!physicalConnection.getAutoCommit()) {
                                    physicalConnection.setAutoCommit(true);
                                }
                                if (!pool.offer(physicalConnection)) {
                                    physicalConnection.close();
                                }
                            } else {
                                physicalConnection.close();
                            }
                        } catch (Exception e) {
                            try { physicalConnection.close(); } catch (Exception ignored) {}
                        }
                        return null;
                    }
                    if ("isClosed".equals(method.getName())) {
                        return physicalConnection.isClosed();
                    }
                    try {
                        return method.invoke(physicalConnection, args);
                    } catch (java.lang.reflect.InvocationTargetException ite) {
                        throw ite.getTargetException();
                    }
                }
        );
    }
}
