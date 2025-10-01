package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public abstract class ConectorBD {
    private final static String DB_PATH = "jdbc:sqlite:data/DespachoDB.db";
      
    protected static Connection getConexion(){
       
       try{
           Connection conexion = DriverManager.getConnection(DB_PATH);
           return conexion;
           
       }catch(SQLException e){
           e.printStackTrace();
       }
       
       return null;
    }
    
}
