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
public abstract class RegimenesDAO {
    private static final Connection conexion = ConectorBD.getConexion();
    
    public static ArrayList<Regimenes> getRegimenes(){
        ArrayList<Regimenes> regimenes = new ArrayList<>();
        String sql = "SELECT * FROM regiemenes";
        try(PreparedStatement gr = conexion.prepareStatement(sql)){
            ResultSet getR = gr.executeQuery();
            
            while(getR.next()){
                regimenes.add(
                        new Regimenes(getR.getInt("id_regimen"),
                                getR.getString("des_regimen")));
            }
            
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null,"Error al obtener clientes: " + ex.getMessage());
        }
        
        return regimenes;
    }
}
