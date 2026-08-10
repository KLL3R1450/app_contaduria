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
            throw new RuntimeException("Error al obtener EFirmas: " + ex.getMessage(), ex);
        }
        
        return firmas;
    }
    
    /***
     * Renueva una Efirma en la base de datos
     * @param fechaExpiracion Variable de tipo String con la nueva fecha de expiracion
     * @param fechaRenovacion Variable de tipo String con la fecha de renovacion
     * @param id_cliente Variable de tipo int que contien el ide del cliente dueño de la Efirma
     * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
     * "correcto"
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
    
     
}
