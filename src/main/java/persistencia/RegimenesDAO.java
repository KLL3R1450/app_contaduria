package persistencia;

import entidades.Regimenes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Osmar & Antigravity
 */
public class RegimenesDAO {
    
    /**
     * Obtiene todos los regimenes que se tienen en la base de datos
     * @return Un ArrayList con todos los registros
     */
    public ArrayList<Regimenes> getRegimenes() {
        ArrayList<Regimenes> regimenes = new ArrayList<>();
        String sql = "SELECT * FROM regimenes";
        try (Connection conexion = ConectorBD.getConexion();
             PreparedStatement gr = conexion.prepareStatement(sql);
             ResultSet getR = gr.executeQuery()) {
            
            while (getR.next()) {
                regimenes.add(
                        new Regimenes(getR.getInt("id_regimen"),
                                getR.getString("des_regimen")));
            }
            
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener regimenes: " + ex.getMessage(), ex);
        }
        
        return regimenes;
    }
    
    /**
     * Funcion que añade un regimen a la base de datos
     * @param r Variable de tipo Regimenes con los datos del nuevo regimen
     * @return "correcto" o mensaje de error
     */
    public String addRegimen(Regimenes r) {
        String sql = "INSERT INTO regimenes(des_regimen) values (?)";
        
        try (Connection conexion = ConectorBD.getConexion();
             PreparedStatement ar = conexion.prepareStatement(sql)) {
            ar.setString(1, r.regimen);
            ar.execute();
            return "correcto";
        } catch (SQLException ex) {
            return "Error al insertar el nuevo regimen: " + ex.getMessage();
        }
    }
    
    /**
     * Funcion que elimina un regimen de la base de datos
     * @param idRegimen Variable de tipo int con el id del regimen a eliminar
     * @return "correcto" o mensaje de error
     */
    public String deleteRegimen(int idRegimen) {
        String sql = "DELETE FROM regimenes WHERE id_regimen = ?";
        
        try (Connection conexion = ConectorBD.getConexion();
             PreparedStatement dr = conexion.prepareStatement(sql)) {
            dr.setInt(1, idRegimen);
            dr.execute();
            return "correcto";
        } catch (SQLException ex) {
            return "Error al eliminar el regimen: " + ex.getMessage();
        }
    }
}
