/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import entidades.Contadores;
import entidades.Declaracion;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

public class ContadoresDAO {
    //Conexion con la Base de datos
    public Connection conexion = ConectorBD.getConexion();
    
    /**
     * Funcion que obtiene todos los contadores del despacho
     * @return Variable de tipo Map con los contadores y sus Ids para busqueda
     */
    public Map<Integer,Contadores> getContadores(){
        String sql = "SELECT * FROM contadores WHERE id_estado = 1";
        Map<Integer,Contadores> contadores = new HashMap<>();
        
        try(PreparedStatement gc = conexion.prepareStatement(sql)){
            ResultSet c = gc.executeQuery();
            
            while(c.next()){
                Contadores co = 
                new Contadores(
                        c.getInt("id_contador"),
                        c.getString("nombre_contador"),
                        c.getString("contacto_contador")
                );
                
                
                contadores.put(co.getId(),co);
            }
            
            c.close();
            
            
            return contadores;
            
        }catch(SQLException ex){
            throw new RuntimeException("Error al obtener los contadores: " + ex.getMessage(), ex);
        }
        
    }
    
    /***
     * Funcion que inserta un registro en la tabla Contadores
     * @param conta Instancia de tipo Contadores que contiene los datos a insertar
     * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
     * "correcto"
     *  @see Contadores
     */
    public String insertContador(Contadores conta){
        String sql = "INSERT INTO contadores(nombre_contador,contacto_contador,id_estado) VALUES (?,?,?)";
        
        try(PreparedStatement ic = conexion.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)){
           ic.setString(1, conta.nombre);
           ic.setString(2, conta.contacto);
           ic.setInt(3, 1);
           
           ic.executeUpdate();
           
           try (ResultSet rs = ic.getGeneratedKeys()) {
               if (rs.next()) {
                   conta.setId(rs.getInt(1));
               }
           }
           
           return "correcto";
           
        }catch(SQLException ex) {
            return "Error al ingresar el contador:" + ex.getMessage();
        }
    }
    
    /**
     * Funcion que "Elimina" un registro en la tabla contadores
     * @param id_contador Variable de tipo int que contiene el id del contador a eliminar
     * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
     * "correcto"
     */
    public String deleteContador(int id_contador){
        String sql = "UPDATE contadores SET id_estado = 2 WHERE id_contador = ?";
        
        try(PreparedStatement dc = conexion.prepareStatement(sql)){
            dc.setInt(1, id_contador);
            dc.executeUpdate();
            
            return "correcto";
        }catch(SQLException ex){
            return "Error al eliminar el contador :" + ex.getMessage();
        }
    }
    
    /**
     * Funcion que permite modificar un registro, solo su contacto, de la tabla contadores
     * @param contacto Variable de tipo String con el datos de contacto del contador
     * @param id_contador Variable de tipo int con el id del contador a modificar
     * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
     * "correcto"
     */
    public String updateContactoContador(String contacto, int id_contador){
        String sql = "UPDATE contadores SET contacto_contador = ? WHERE id_contador = ?";
        
        try(PreparedStatement ucc = conexion.prepareStatement(sql)){
            ucc.setString(1, contacto);
            ucc.setInt(2, id_contador);
            
            ucc.executeUpdate();
            return "correcto";
            
        }catch(SQLException ex){
            return "Fallo al actualizar el contacto del contador: " + ex.getMessage();
        }
    }
    
}
