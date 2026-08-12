package persistencia;

import entidades.Pago;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PagosDAO {
    private Connection conexion = ConectorBD.getConexion();

    public PagosDAO() {
        crearTablaPagosSiNoExiste();
    }

    private void crearTablaPagosSiNoExiste() {
        String sql = "CREATE TABLE IF NOT EXISTS pagos_clientes ("
                + "id_pago INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "id_cliente INTEGER NOT NULL,"
                + "anio INTEGER NOT NULL,"
                + "mes INTEGER NOT NULL,"
                + "monto INTEGER NOT NULL,"
                + "fecha_pago TEXT NOT NULL,"
                + "FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),"
                + "UNIQUE(id_cliente, anio, mes)"
                + ");";
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear la tabla pagos_clientes: " + e.getMessage(), e);
        }
    }

    public String insertarPago(Pago pago) {
        String sql = "INSERT INTO pagos_clientes(id_cliente, anio, mes, monto, fecha_pago) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, pago.id_cliente);
            pstmt.setInt(2, pago.anio);
            pstmt.setInt(3, pago.mes);
            pstmt.setInt(4, pago.monto);
            pstmt.setString(5, pago.fecha_pago);
            pstmt.executeUpdate();
            return "correcto";
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al registrar pago en BD: " + e.getMessage(), e);
        }
    }

    public List<Pago> obtenerPagosPorCliente(int idCliente) {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT id_pago, id_cliente, anio, mes, monto, fecha_pago FROM pagos_clientes WHERE id_cliente = ? ORDER BY anio ASC, mes ASC";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idCliente);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Pago(
                        rs.getInt("id_pago"),
                        rs.getInt("id_cliente"),
                        rs.getInt("anio"),
                        rs.getInt("mes"),
                        rs.getInt("monto"),
                        rs.getString("fecha_pago")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener pagos en BD: " + e.getMessage(), e);
        }
        return lista;
    }
}
