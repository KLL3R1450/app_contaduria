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
    private final Connection conexion = ConectorBD.getConexion();
    
    public Map<Integer,EFirmas> getAllFirmas(){
        String sql = "SELECT fecha_expiracion,fecha_renovacion,id_cliente FROM e_firmas";
        Map<Integer,EFirmas> firmas = new HashMap<>();
        
        try(PreparedStatement gAF = conexion.prepareStatement(sql); ResultSet rs = gAF.executeQuery()){
            
            while(rs.next()){
                EFirmas firma = new EFirmas(
                        rs.getString("fecha_expiracion"),
                        rs.getString("fecha_renovacion"),
                        rs.getInt("id_cliente")
                );
                firmas.put(firma.getIdCliente(), firma);
            }
             
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null,"error al obtener EFirmas: " + ex.getMessage());
        }
        
        return firmas;
    }
    
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
    
}
