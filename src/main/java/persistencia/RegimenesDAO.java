/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import entidades.Regimenes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Osmar
 */
public class RegimenesDAO {
    //Conexion con la base de datos
    private static final Connection conexion = ConectorBD.getConexion();
    
    /**
     * Obtiene todos los regimenes que se tienen en la base de datos
     * @return Un ArrayList con todos los registros
     */
    public  ArrayList<Regimenes> getRegimenes(){
        ArrayList<Regimenes> regimenes = new ArrayList<>();
        String sql = "SELECT * FROM regimenes";
        try(PreparedStatement gr = conexion.prepareStatement(sql)){
            ResultSet getR = gr.executeQuery();
            
            while(getR.next()){
                regimenes.add(
                        new Regimenes(getR.getInt("id_regimen"),
                                getR.getString("des_regimen")));
            }
            
        }catch(SQLException ex){
            throw new RuntimeException("Error al obtener regimenes: " + ex.getMessage(), ex);
        }
        
        return regimenes;
    }
    
    /**
     * Funcion que añade un regimen a la base de datos
     * @param r Variable de tipo String con el nombre del nuevo regimen
     * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
     * "correcto"
     */
    public String addRegimen(Regimenes r){
        String sql = "INSERT INTO regimenes(des_regimen) values (?)";
        
        try(PreparedStatement ar = conexion.prepareStatement(sql)){
            ar.setString(1, r.regimen);
            
            ar.execute();
            
            return "correcto";
        }catch(SQLException ex){
            return "Error al insertar el nuevo regimen: " + ex.getMessage();
        }
    }
    
    /**
     * Funcion que elimina un regimen de la base de datos
     * @param idRegimen Variable de tipo int con el id del regimen a eliminar
     * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
     * "correcto"
     */
    public String deleteRegimen(int idRegimen){
        String sql = "DELETE FROM regimenes WHERE id_regimen = ?";
        
        try(PreparedStatement dr = conexion.prepareStatement(sql)){
            
            dr.setInt(1, idRegimen);
            
            dr.execute();
            
            return "correcto";
            
        }catch(SQLException ex){
            return "Error al eliminar el regimen: " + ex.getMessage();
        }
    }
}
