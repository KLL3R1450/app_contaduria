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
    
    /**
     * Obtiene todas las Efirmas que se tengan el la base de datos
     * @return Un Map con las Efirmas 
     * @deprecated 
     */
    @Deprecated
    public Map<Integer,EFirmas> getAllFirmas(){
        String sql = "SELECT fecha_expiracion, fecha_renovacion, id_cliente, ruta_certificado, ruta_key, contrasena FROM e_firmas";
        Map<Integer,EFirmas> firmas = new HashMap<>();
        
        try(PreparedStatement gAF = conexion.prepareStatement(sql); ResultSet rs = gAF.executeQuery()){
            
            while(rs.next()){
                EFirmas firma = new EFirmas(
                        rs.getString("fecha_expiracion"),
                        rs.getString("fecha_renovacion"),
                        rs.getInt("id_cliente"),
                        rs.getString("ruta_certificado"),
                        rs.getString("ruta_key"),
                        rs.getString("contrasena")
                );
                firmas.put(firma.getIdCliente(), firma);
            }
             
        }catch(SQLException ex){
            throw new RuntimeException("Error al obtener EFirmas: " + ex.getMessage(), ex);
        }
        
        return firmas;
    }
    
    /***
     * Renueva una Efirma en la base de datos
     * @param fechaExpiracion Variable de tipo String con la nueva fecha de expiracion
     * @param fechaRenovacion Variable de tipo String con la fecha de renovacion
     * @param id_cliente Variable de tipo int que contien el ide del cliente dueño de la Efirma
     * @return El status devuelto por el gestor de base de datos. En caso de ser correcto regresa "correcto"
     */
    public String renovacion(String fechaExpiracion,String fechaRenovacion, int id_cliente){
        String sql = "UPDATE e_firmas SET fecha_expiracion = ?, fecha_renovacion = ? WHERE id_cliente = ?";
        
        try(PreparedStatement sFE = conexion.prepareStatement(sql)){
            sFE.setString(1, fechaExpiracion);
            sFE.setString(2, fechaRenovacion);
            sFE.setInt(3, id_cliente);
            
            sFE.executeUpdate();
            sFE.close();
            return "correcto";
        }catch(SQLException ex){
            return "Error al cambiar las fechas: " + ex.getMessage();
        }
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
}
