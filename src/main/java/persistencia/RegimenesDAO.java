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
    private static final Connection conexion = ConectorBD.getConexion();
    
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
            JOptionPane.showMessageDialog(null,"Error al obtener regimenes: " + ex.getMessage());
        }
        
        return regimenes;
    }
    
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
