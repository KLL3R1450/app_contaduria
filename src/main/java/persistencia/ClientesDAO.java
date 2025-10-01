package persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import entidades.Cliente;
import entidades.Regimenes;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientesDAO {
    private Connection conexion = ConectorBD.getConexion();
    
    public String insertCliente(Cliente cliente){
        String sql = "INSERT INTO clientes(nombre_cliente,rfc_cliente,cp_cliente,correo_cliente,m_honorarios_cliente) VALUES (?,?,?,?,?)";
        int id = -1;
        
        try{
            conexion.setAutoCommit(false);
            
            PreparedStatement insertC = conexion.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            insertC.setString(1, cliente.nombre);
            insertC.setString(2,cliente.rfc);
            insertC.setString(3, cliente.cp);
            insertC.setString(4, cliente.correo);
            insertC.setInt(5, cliente.honorarios);
            
            int insertado = insertC.executeUpdate();
            
            if(insertado > 0){
                
                ResultSet rs = insertC.getGeneratedKeys();
                
                if(rs.next()) id = rs.getInt("id_cliente");
         
            }
            
            if(id > 0 && !cliente.regimenes.isEmpty()){
                insertRegimenesClientes(id, cliente.regimenes);
            }
            
            conexion.commit();
            return "Ingreso Exitoso";
            
        }catch(SQLException e){ 
            
            try{
                if(conexion != null){
                    
                    conexion.rollback();
                    System.err.println("Transaccion revertida");
                    
                }
                
            }catch(SQLException rb){System.err.println("Transaccion no revertida: " + rb.getMessage());
            
            }
            
            return "Error al insertar cliente y regimenes " + e.getMessage();
        }
        
        finally{
            
           try { conexion.setAutoCommit(true); }
           
           catch(SQLException e){
               System.err.println("Error al restaurar autocommit: " + e.getMessage());
           }
           
        }
        
    }
    
    private void insertRegimenesClientes(int id, ArrayList<Regimenes> regimenes) throws SQLException{
        String sql = "INSERT INTO regimenes_clientes(id_cliente,id_regimen) VALUES (?,?)";
        
        try(PreparedStatement insertRC = conexion.prepareStatement(sql)){
            for(Regimenes r : regimenes){
                    insertRC.setInt(1, id);
                    insertRC.setInt(2, r.getId());
                    insertRC.addBatch();  
            }
            
            insertRC.executeBatch();
        } 
    }
    
    public ArrayList<Cliente> getClientes(){
        String sql = "SELECT * FROM clientes";
        ArrayList<Cliente> cls = new ArrayList<>();
        Map<Integer,Cliente> mapeoClientes= new HashMap<>();


        try(PreparedStatement getC = conexion.prepareStatement(sql)){
            ResultSet clientes = getC.executeQuery();

            while(clientes.next()){
                Cliente c = new Cliente(clientes.getInt("id_cliente"));
                c.nombre = clientes.getString("nombre_cliente");
                c.rfc = clientes.getString("rfc_cliente");
                c.cp = clientes.getString("cp_cliente");
                c.correo = clientes.getString("correo_cliente");
                c.honorarios = clientes.getInt("m_honorarios_cliente");

                cls.add(c);
                mapeoClientes.put(c.getId(),c);
            }


        }catch(SQLException e){
            System.err.println("Fallo al obtener clientes:" + e.getMessage());
        }

        sql = "SELECT * FROM regimenes_clientes_view";
        try(PreparedStatement getRC = conexion.prepareStatement(sql)){
            ResultSet rg = getRC.executeQuery();

            while(rg.next()){
                int idC = rg.getInt("id_cliente");

                if(mapeoClientes.containsKey(idC)){
                    Cliente c = mapeoClientes.get(idC);

                    c.regimenes.add(
                    new Regimenes(rg.getInt("id_regimen"),
                    rg.getString("des_regimen")
                    )
                    );

                }
            }

        }catch(SQLException ex){
            System.err.println("Fallo al obtener regiemenes: " + ex.getMessage());
        }

        return cls;
    }
    
}
