package persistencia;

import entidades.EFirmas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author Osmar
 */
public class EFirmasDAO {
    //Conexion con la base de datos
    private final Connection conexion = ConectorBD.getConexion();
    
    public EFirmas getFirmaDe(int idCliente) {
        String sql = "SELECT fecha_expiracion, fecha_renovacion, id_cliente, ruta_certificado, ruta_key, contrasena FROM e_firmas WHERE id_cliente = ?";
        try(PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1, idCliente);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return new EFirmas(
                        rs.getString("fecha_expiracion"),
                        rs.getString("fecha_renovacion"),
                        rs.getInt("id_cliente"),
                        rs.getString("ruta_certificado"),
                        rs.getString("ruta_key"),
                        rs.getString("contrasena")
                    );
                }
            }
        }catch(SQLException ex){
            throw new RuntimeException("Error al obtener firma por ID de cliente: " + ex.getMessage(), ex);
        }
        return null;
    }
   

    public String renovacion(String fechaExpiracion, String fechaRenovacion, String rutaCertificado, String rutaKey, String contrasena, int id_cliente){
        String sql = "UPDATE e_firmas SET fecha_expiracion = ?, fecha_renovacion = ?, ruta_certificado = ?, ruta_key = ?, contrasena = ? WHERE id_cliente = ?";
        
        try(PreparedStatement sFE = conexion.prepareStatement(sql)){
            sFE.setString(1, fechaExpiracion);
            sFE.setString(2, fechaRenovacion);
            sFE.setString(3, rutaCertificado);
            sFE.setString(4, rutaKey);
            sFE.setString(5, contrasena);
            sFE.setInt(6, id_cliente);
            
            sFE.executeUpdate();
            sFE.close();
            return "correcto";
        }catch(SQLException ex){
            return "Error al cambiar las fechas y archivos de firma: " + ex.getMessage();
        }
    }

    public String insertFirma(EFirmas firma) {
        String sql = "INSERT INTO e_firmas(fecha_expiracion, fecha_renovacion, id_cliente, ruta_certificado, ruta_key, contrasena) VALUES (?, ?, ?, ?, ?, ?)";
        try(PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setString(1, firma.fecha_expiracion);
            ps.setString(2, firma.fecha_renovacion);
            ps.setInt(3, firma.getIdCliente());
            ps.setString(4, firma.ruta_certificado);
            ps.setString(5, firma.ruta_key);
            ps.setString(6, firma.contrasena);
            ps.executeUpdate();
            return "correcto";
        } catch(SQLException ex){
            return "Error al insertar E-Firma: " + ex.getMessage();
        }
    }

    public void insertFirmaTransaccional(EFirmas firma, Connection conn) throws SQLException {
        String sql = "INSERT INTO e_firmas(fecha_expiracion, fecha_renovacion, id_cliente, ruta_certificado, ruta_key, contrasena) VALUES (?, ?, ?, ?, ?, ?)";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, firma.fecha_expiracion);
            ps.setString(2, firma.fecha_renovacion);
            ps.setInt(3, firma.getIdCliente());
            ps.setString(4, firma.ruta_certificado);
            ps.setString(5, firma.ruta_key);
            ps.setString(6, firma.contrasena);
            ps.executeUpdate();
        }
    }

    public java.util.List<Object[]> obtenerSemaforoDashboard() {
        java.util.List<Object[]> res = new java.util.ArrayList<>();
        String sqlUrgente = "SELECT id_cliente, nombre_cliente, rfc_cliente, fecha_expiracion,  estado_alerta, dias_restantes FROM vw_semaforo_efirmas WHERE estado_alerta IN ('VENCIDA', 'URGENTE') ORDER BY dias_restantes ASC";
        String sqlProxima = "SELECT id_cliente, nombre_cliente, rfc_cliente, fecha_expiracion,  estado_alerta, dias_restantes FROM vw_semaforo_efirmas WHERE estado_alerta = 'PROXIMA' ORDER BY dias_restantes ASC";
        String sqlDefault = "SELECT id_cliente, nombre_cliente, rfc_cliente, fecha_expiracion,  estado_alerta, dias_restantes FROM vw_semaforo_efirmas ORDER BY dias_restantes ASC LIMIT 10";

        try {
            // 1. Intentar Urgentes/Vencidas
            try(PreparedStatement ps = conexion.prepareStatement(sqlUrgente); ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    res.add(new Object[]{
                        rs.getInt("id_cliente"),
                        rs.getString("nombre_cliente"),
                        rs.getString("rfc_cliente"),
                        rs.getString("fecha_expiracion"),
                        rs.getString("estado_alerta"),
                        rs.getInt("dias_restantes")
                    });
                }
            }
            // 2. Si está vacío, intentar Próximas
            if (res.isEmpty()) {
                try(PreparedStatement ps = conexion.prepareStatement(sqlProxima); ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        res.add(new Object[]{
                            rs.getInt("id_cliente"),
                            rs.getString("nombre_cliente"),
                            rs.getString("rfc_cliente"),
                            rs.getString("fecha_expiracion"),
                            rs.getString("estado_alerta"),
                            rs.getInt("dias_restantes")
                        });
                    }
                }
            }
            // 3. Si sigue vacío, por defecto 10 firmas
            if (res.isEmpty()) {
                try(PreparedStatement ps = conexion.prepareStatement(sqlDefault); ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        res.add(new Object[]{
                            rs.getInt("id_cliente"),
                            rs.getString("nombre_cliente"),
                            rs.getString("rfc_cliente"),
                            rs.getString("fecha_expiracion"),
                            rs.getString("estado_alerta"),
                            rs.getInt("dias_restantes")
                        });
                    }
                }
            }
        } catch(SQLException e) {
            throw new RuntimeException("Error al cargar semáforo de e-firmas: " + e.getMessage(), e);
        }
        return res;
    }
}
