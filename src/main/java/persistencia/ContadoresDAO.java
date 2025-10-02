/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import entidades.Contadores;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ContadoresDAO {
    public Connection conexion = ConectorBD.getConexion();
    
    public ArrayList<Contadores> getContadores(){
        String sql = "SELECT * FROM contadores WHERE id_estado = 1";
        ArrayList<Contadores> contadores = new ArrayList<>();
        
        
        try(PreparedStatement gc = conexion.prepareStatement(sql)){
            ResultSet c = gc.executeQuery();
            
            while(c.next()){
                contadores.add(
                new Contadores(
                        c.getInt("id_contador"),
                        c.getString("nombre_contador"),
                        c.getString("contacto_contador")
                )
                );
            }
            
            return contadores;
            
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null,"Error al obtener los contadores: " + ex.getMessage());
            return contadores;
        }
    }
    
    public String insertContador(Contadores conta){
        String sql = "INSERT INTO contadores(nombre_contador,contacto_contador,id_estado) VALUES (?,?,?)";
        
        try(PreparedStatement ic = conexion.prepareStatement(sql)){
           ic.setString(1, conta.nombre);
           ic.setString(2, conta.contacto);
           ic.setInt(3, 1);
           
           ic.executeUpdate();
           
           return "Contador ingresado con exito";
           
        }catch(SQLException ex) {
            return "Error al ingresar el contador:" + ex.getMessage();
        }
    }
    
    public String deleteContador(int id_contador){
        String sql = "UPDATE contadores SET id_estado = 2 WHERE id_contador = ?";
        
        try(PreparedStatement dc = conexion.prepareStatement(sql)){
            dc.setInt(1, id_contador);
            dc.executeUpdate();
            
            return "Contador eliminado con exito";
        }catch(SQLException ex){
            return "Error al eliminar el contador :" + ex.getMessage();
        }
    }
    
    public String updateContactoContador(String contacto, int id_cliente){
        String sql = "UPDATE clientes SET contacto_cliente = ? WHERE id_cliente = ?";
        
        try(PreparedStatement ucc = conexion.prepareStatement(sql)){
            ucc.setString(1, contacto);
            ucc.setInt(2, id_cliente);
            
            ucc.executeUpdate();
            return "Contacto actualizado con exito";
            
        }catch(SQLException ex){
            return "Fallo al actualizar el contacto del contador: " + ex.getMessage();
        }
    }
    
}
