package persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import entidades.Cliente;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

public class ClientesDAO {
    private Connection conexion = ConectorBD.getConexion();
    
    public String insertCliente(Cliente cliente){
        String sql = "INSERT INTO clientes(nombre_cliente,rfc_cliente,cp_cliente,correo_cliente,m_honorarios_cliente,id_contador,id_estado) VALUES (?,?,?,?,?,?,?)";
        int id = -1;
        
        try{
            conexion.setAutoCommit(false);
            
            PreparedStatement insertC = conexion.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            insertC.setString(1, cliente.nombre);
            insertC.setString(2,cliente.rfc);
            insertC.setString(3, cliente.cp);
            insertC.setString(4, cliente.correo);
            insertC.setInt(5, cliente.honorarios);
            insertC.setInt(6,cliente.id_contador);
            insertC.setInt(7, 1);
            
            int insertado = insertC.executeUpdate();
            
            if(insertado > 0){
                
                ResultSet rs = insertC.getGeneratedKeys();
                
                if(rs.next()) id = rs.getInt("id_cliente");
                rs.close();
            }
            
            insertC.close();
            if(id > 0 && !cliente.idsRegimenes.isEmpty()){
                insertRegimenesClientes(id, cliente.idsRegimenes);
            }
            
            conexion.commit();
            return "correcto";
            
        }catch(SQLException e){ 
            
            try{
                if(conexion != null){
                    
                    conexion.rollback();
                    return "Transaccion revertida";
                    
                }
                
            }catch(SQLException rb){return "Transaccion no revertida: " + rb.getMessage();
            
            }
            
            return "Error al insertar cliente y regimenes " + e.getMessage();
        }
        
        finally{
            
           try { conexion.setAutoCommit(true); }
           
           catch(SQLException e){
               return "Error al restaurar autocommit: " + e.getMessage();
           }
           
        }
        
    }
    
    private void insertRegimenesClientes(int id, ArrayList<Integer> regimenes) throws SQLException{
        String sql = "INSERT INTO regimenes_clientes(id_cliente,id_regimen) VALUES (?,?)";
        
        try(PreparedStatement insertRC = conexion.prepareStatement(sql)){
            
            for(Integer r : regimenes){
                if(regimenYaExistente(id, r)) continue;
                    insertRC.setInt(1, id);
                    insertRC.setInt(2, r);
                    insertRC.addBatch();  
            }
            
            insertRC.executeBatch();
            insertRC.close();
        } 
    }
    
    public Map<Integer,Cliente> getClientes(){
        String sql = "SELECT * FROM clientes WHERE id_estado = 1";
        Map<Integer,Cliente> cls = new HashMap<>();


        try(PreparedStatement getC = conexion.prepareStatement(sql)){
            ResultSet clientes = getC.executeQuery();

            while(clientes.next()){
                Cliente c = new Cliente(clientes.getInt("id_cliente"));
                c.nombre = clientes.getString("nombre_cliente");
                c.rfc = clientes.getString("rfc_cliente");
                c.cp = clientes.getString("cp_cliente");
                c.correo = clientes.getString("correo_cliente");
                c.honorarios = clientes.getInt("m_honorarios_cliente");
                c.id_contador = clientes.getInt("id_contador");
                
                cls.put(c.id_persona, c);
            }
            
            getC.close();
            clientes.close();

        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Fallo al obtener clientes:" + e.getMessage());
        }

        sql = "SELECT id_cliente,id_regimen FROM regimenes_clientes";
        try(PreparedStatement getRC = conexion.prepareStatement(sql)){
            ResultSet rg = getRC.executeQuery();

            while(rg.next()){
                int idC = rg.getInt("id_cliente");

                if(cls.containsKey(idC)){
                    Cliente c = cls.get(idC);

                    c.idsRegimenes.add(rg.getInt("id_regimen"));

                }
            }
            getRC.close();
            rg.close();
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null,"Fallo al obtener regimenes: " + ex.getMessage());
        }

        return cls;
    }
    
    public String deleteRegimenCliente(int idCliente, int idRegimen){
        String sql = "DELETE FROM regimenes_clientes WHERE id_cliente = ? AND id_regimen = ?";
        
        try(PreparedStatement deleteR = conexion.prepareStatement(sql)){
            deleteR.setInt(1, idCliente);
            deleteR.setInt(2, idRegimen);
            
            deleteR.executeUpdate();
            deleteR.close();
            return "correcto";
            
        }catch(SQLException ex){
            return "Error al eliminar el regimen: " + ex.getMessage();
        }
    }
    
    private boolean regimenYaExistente(int idCliente, int idRegimen){
        String sql = "SELECT nombre_cliente FROM clientes_regimenes_view WHERE id_cliente = ? AND id_regimen = ?";
        
        try(PreparedStatement re = conexion.prepareStatement(sql)){
            re.setInt(1, idCliente);
            re.setInt(2, idRegimen);
            
            ResultSet r = re.executeQuery();
            
            if(r.next()){
                r.close();
                return true;
            }
            
        }catch(SQLException ex){
            System.err.println("Fallo al comporbar regimen existente: " + ex.getMessage());
            return true;
        }
        
        return false;
        
    }
    
    public String agregarRegimenCliente(int idCliente, int idRegimen){
        if(regimenYaExistente(idCliente, idRegimen)) return "El regimen ya existe para este cliente";
        
        String sql = "INSERT INTO regimenes_clientes(id_cliente,id_regimen) VALUES (?,?)";
        
        try(PreparedStatement ir = conexion.prepareStatement(sql)){
            
            ir.setInt(1, idCliente);
            ir.setInt(2, idRegimen);
            
            ir.executeUpdate();
            ir.close();
            
            return "correcto";
        }catch(SQLException ex){
            return "Fallo al agregar el regimen: " + ex.getMessage();
        }
        
    }
    
    
    /*    public Cliente getClienteById(int idCliente){
    try{
    String sql = "SELECT * FROM clientes WHERE id_cliente = ?";
    PreparedStatement gC = conexion.prepareStatement(sql);
    
    gC.setInt(1, idCliente);
    ResultSet rs = gC.executeQuery();
    
    if(rs.next()){
    Cliente c = new Cliente
    }
    }
    }*/
    
    public String deleteCliente(int idCliente){
        String sql = "UPDATE clientes SET id_estado = 2 WHERE id_cliente = ?";
        
        try(PreparedStatement dc = conexion.prepareStatement(sql)){
            dc.setInt(1, idCliente);
            
            dc.executeUpdate();
            dc.close();
            return "correcto";
            
        }catch(SQLException ex){
            return "Fallo al eliminar el cliente: " + ex.getMessage();
        }
    }
    
}
