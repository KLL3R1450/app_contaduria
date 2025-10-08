package persistencia;

import entidades.EFirmas;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Osmar
 */
public class EFirmasDAO {
    private final Connection conexion = ConectorBD.getConexion();
    
    public EFirmas getEfirmaDe(int id_cliente){
        String sql = "SELECT fecha_expiracionn,fecha_renovacion FROM e_firmas WHERE id_cliente = ?";
        
        try(PreparedStatement gEF = conexion.prepareStatement(sql)){
            
            gEF.setInt(1, id_cliente);
            ResultSet rs = gEF.executeQuery();
            
            if(rs.next()) {
                return new EFirmas(rs.getString("fecha_expiracion"),
           rs.getString("fecha_renovacion"),
                rs.getInt("id_cliente"));
            }
            
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null,"Error al obtener la EFirma" + ex.getMessage());
        }
        
        return null;
    }
    
    public String renovacion(String fechaExpiracion,String fechaRenovacion, int id_cliente){
        String sql = "UPDATE e_firmas SET fecha_expiracion = ?, fecha_renovacion = ? WHERE id_cliente = ?";
        
        try(PreparedStatement sFE = conexion.prepareStatement(sql)){
            sFE.setString(1, fechaExpiracion);
            sFE.setString(2, fechaRenovacion);
            sFE.setInt(3, id_cliente);
            
            sFE.executeUpdate();
            sFE.close();
            return "Fechas cambiadas con exito";
        }catch(SQLException ex){
            return "Error al cambiar las fechas: " + ex.getMessage();
        }
    }
    
}
