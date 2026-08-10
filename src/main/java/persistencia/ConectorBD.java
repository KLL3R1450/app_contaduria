package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Clase que regresa la instancia de la conexion con SQLite para el manejo de los datos
 * @author Angel Osmar Aguilar Lopez
 */
public abstract class ConectorBD {
    private final static String DB_PATH = "jdbc:sqlite:data/DespachoDB.db";
      
    public static Connection getConexion(){
       
       try{
           Connection conexion = DriverManager.getConnection(DB_PATH);
           return conexion;
           
       }catch(SQLException e){
           e.printStackTrace();
       }
       
       return null;
    }
    
}
