package persistencia;

import entidades.Declaracion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;


public class DeclaracionDAO {
    private final Connection conexion = ConectorBD.getConexion();
    
    public String insertDeclaracion(int id_cliente,int anio, int mes){
        String sql = "INSERT INTO declaraciones_clientes(id_cliente,anio,mes) VALUES (?,?,?)";
        
        try(PreparedStatement iD = conexion.prepareStatement(sql)){
            iD.setInt(1, id_cliente);
            iD.setInt(2, anio);
            iD.setInt(3, mes);
            
            iD.executeUpdate();
            
            return "Declaracion iniciada con exito";
        }catch(SQLException ex){
            return "Error al crear declaracion: " +ex.getMessage();
        }
        
    }
    
    public String colocarGastos(int id_declaracion){
        String sql = "UPDATE declaraciones_clientes SET gastos = 1 WHERE id_declaracion = ?";
        
        try(PreparedStatement gastos = conexion.prepareStatement(sql)){
            gastos.setInt(1, id_declaracion);
            
            gastos.executeUpdate();
            
            return "correcto";
        }catch(SQLException ex){
            return "Error al colocar gastos: " + ex.getMessage();
        }
        
    }
    
    public String colocarIngresos(int id_declaracion){
        String sql = "UPDATE declaraciones_clientes SET ingresos = 1 WHERE id_declaracion = ?";
        
        try(PreparedStatement ingresos = conexion.prepareStatement(sql)){
            ingresos.setInt(1, id_declaracion);
            
            ingresos.executeUpdate();
            
            return "correcto";
        }catch(SQLException ex){
            return "Error al setear ingresos: " +ex.getMessage();
        }
    }
    
    public String colocarIngresosGastos(int id_declaracion){
        String sql = "UPDATE declaraciones_clientes SET ingresos = 1, gastos = 1 WHERE id_declaracion = ?";
        
        try(PreparedStatement ingresos = conexion.prepareStatement(sql)){
            ingresos.setInt(1, id_declaracion);
            
            ingresos.executeUpdate();
            
            return "correcto";
        }catch(SQLException ex){
            return "Error al setear ingresos y gastos : " +ex.getMessage();
        }
    }
    
    public String setDeclarado(int id_declaracion){
        String sql = "UPDATE declaraciones_clientes SET declarado = 1 WHERE id_declaracion = ?";
        
        try(PreparedStatement declarar = conexion.prepareStatement(sql)){
            declarar.setInt(1, id_declaracion);
            
            declarar.executeUpdate();
            
            return "correcto";
        }catch(SQLException ex){
            return "Error al actualizar el estado: " + ex.getMessage();
        }
    }
    
    public String desDeclarar(int id_declaracion){
        String sql = "UPDATE declaraciones_clientes SET declarado = 0 WHERE id_declaracion = ?";
        
        try(PreparedStatement declarar = conexion.prepareStatement(sql)){
            declarar.setInt(1, id_declaracion);
            
            declarar.executeUpdate();
            
            return "correcto";
        }catch(SQLException ex){
            return "Error al actualizar el estado: " + ex.getMessage();
        }
    }
    
    public ArrayList<Declaracion> getAllDeclaraciones(){
        String sql = "SELECT * FROM declaraciones_clientes";
        ArrayList<Declaracion> decs = new ArrayList<>();
        
        
        try(PreparedStatement gAD = conexion.prepareStatement(sql)){
            ResultSet rs = gAD.executeQuery();
            
            while(rs.next()){
                Declaracion d = new Declaracion(
                        rs.getInt("id_declaracion"),rs.getInt("id_cliente"),
                        rs.getInt("anio"),rs.getInt("mes"),rs.getInt("gastos"),
                        rs.getInt("ingresos"),rs.getInt("declarado")
                );
                decs.add(d);
            }
            
            return decs;
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null,"Error al obtener las declaraciones: " + ex.getMessage());
        }
        
        return decs;
    }
    
}
