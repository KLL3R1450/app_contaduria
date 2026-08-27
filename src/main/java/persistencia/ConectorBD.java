package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Clase que regresa la instancia de la conexion con MySQL para el manejo de los datos
 * @author Angel Osmar Aguilar Lopez
 */
public abstract class ConectorBD {
      
    public static Connection getConexion(){
       String dbPath = ConfigLoader.get("DB_URI");
       String user = ConfigLoader.get("DB_USER");
       String password = ConfigLoader.get("DB_PASSWORD");
       
       try{
           Connection conexion = DriverManager.getConnection(dbPath, user, password);
           return conexion;
           
       }catch(SQLException e){
           e.printStackTrace();
       }
       
       return null;
    }
    
}
