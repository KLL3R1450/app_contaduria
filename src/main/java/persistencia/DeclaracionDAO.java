package persistencia;

import entidades.Declaracion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;


public class DeclaracionDAO {
    //Conexion con la Base de datos
    private final Connection conexion = ConectorBD.getConexion();
    
    /**
     * Funcion que inserta un registro de tipo Declaracion de la declaracion de los clientes
     * @param id_cliente Variable de tipo int que contiene el id del cliente a insertar su declaracion
     * @param anio Variable de tipo int que contiene el año de la declaracion
     * @param mes Variable de tipo int que contiene el mes de la declaracion
     * @return"El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
     * "correcto"
     */
    public String insertDeclaracion(int id_cliente,int anio, int mes){
        String sql = "INSERT INTO declaraciones_clientes(id_cliente,anio,mes) VALUES (?,?,?)";
        
        try(PreparedStatement iD = conexion.prepareStatement(sql)){
            iD.setInt(1, id_cliente);
            iD.setInt(2, anio);
            iD.setInt(3, mes);
            
            iD.executeUpdate();
            
            return "correcto";
        }catch(SQLException ex){
            return "Error al crear declaracion: " +ex.getMessage();
        }
        
    }
    
    /***
     * Funcion que permite colocar si ya se comprobaron los gastos de un cliente
     * @param id_declaracion Variable de tipo Int que contiene el id de la declaracion a modificar
     * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
     * "correcto"
     */
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
    
    /***
     * Funcion que permite colocar si ya se corroboraron los datos de ingresos de un cliente para un mes y año puntual
     * @param id_declaracion Variable de tipo Int con el id de la declaracion a modificar
     * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
     * "correcto"
     */
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
    
    /**
     * Funcion que Hace lo mismo que @colocarGastos y @colocarIngresos pero en una misma funcion 
     * @param id_declaracion Variable de tipo int que contiene el id de la declaracion a modificar
     * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
     * "correcto"
     */
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
    
    /**
     * Funcion que setea el estado de declarado para una declaracion
     * @param id_declaracion Variable de tipo int que contiene el id de la declaracion a modificar
     * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
     * "correcto"
     */
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
    
    /**
     * Funcion que remueve el estado de declarado "SOLO USAR EN CASOS PUNTUALES"
     * @param id_declaracion Variable de tipo int que contiene el id de la declaracion a modificar
     * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
     * "correcto"
     */
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
    
    /**
     * Obtiene todas las declaraciones que tiene la tabla declaraciones_clientes
     * @return Variable de tipo Map con los ids como clave y declaraciones 
     * @deprecated
     */
    public Map<Integer,Declaracion> getAllDeclaraciones(){
        String sql = "SELECT * FROM declaraciones_clientes";
        Map<Integer, Declaracion> decs = new HashMap<>();
        
        
        try(PreparedStatement gAD = conexion.prepareStatement(sql)){
            ResultSet rs = gAD.executeQuery();
            
            while(rs.next()){
                Declaracion d = new Declaracion(
                        rs.getInt("id_declaracion"),rs.getInt("id_cliente"),
                        rs.getInt("anio"),rs.getInt("mes"),rs.getInt("gastos"),
                        rs.getInt("ingresos"),rs.getInt("declarado")
                );
                decs.put(d.getIdDeclaracion(), d);
            }
            
            return decs;
        }catch(SQLException ex){
            throw new RuntimeException("Error al obtener las declaraciones: " + ex.getMessage(), ex);
        }
        
    }
    
}
