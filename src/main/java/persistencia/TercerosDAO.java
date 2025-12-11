package persistencia;

import entidades.Regimenes;
import entidades.Terceros;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;


public class TercerosDAO {
    private final Connection conexion = ConectorBD.getConexion();
    
    public String insertTercero(Terceros t, ArrayList<Integer> clientes){
        String sql = "INSERT INTO terceros(nombre_tercero,rfc_tercero,cp_tercero,correo_tercero) VALUES (?,?,?,?)";
        int id = -1;
        
        
        try{
            conexion.setAutoCommit(false);
            PreparedStatement it = conexion.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            it.setString(1, t.nombre);
            it.setString(2, t.rfc);
            it.setString(3, t.cp);
            it.setString(4, t.correo);
            int insertado = it.executeUpdate();
            
            if(insertado > 0){
                ResultSet rs = it.getGeneratedKeys();
                if(rs.next()) id = rs.getInt(1);
                rs.close();
            }
            
            it.close();
            
            if(id > 0 && !t.idsRegimenes.isEmpty()){
                
                String clientesTercero = relacionarClientes(id, clientes);
                String clientesRegimenes = insertarRegimenes(id, t.idsRegimenes);
                
                if(!"correcto".equals(clientesTercero)){
                    conexion.rollback();
                    return clientesTercero;
                }
                
                
                if(!"correcto".equals(clientesRegimenes)){
                    conexion.rollback();
                    return clientesRegimenes;
                } 
            }
            
            conexion.commit();
            
            return "correcto" ;
            
        }catch(SQLException ex){
            
            try{conexion.rollback();}
            catch(SQLException e){return "Fallo al reiniciar el rollback" + e.getMessage();}
            
            return "Fallo al insertar el tercero: " + ex.getMessage();
        }finally{
            try{ conexion.setAutoCommit(true);}
            
            catch(SQLException ex){}
        }
    }
    
    private boolean clienteTercero(int idTercero, int idCliente){
        String sql = "SELECT id_tercero FROM terceros_clientes_view WHERE id_cliente = ? AND id_tercero = ?";
        
        try(PreparedStatement ct = conexion.prepareStatement(sql)){
            ResultSet rs =  ct.executeQuery();
            
            if(rs.next()) {
                rs.close();
                ct.close();
                return true;
            }
            
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null,"Error al buscar regimen: " + ex.getMessage());
        }
        
        return false;
    }
    
    public String relacionarClientes(int idTercero, ArrayList<Integer> clientes){
        String sql = "INSERT INTO terceros_clientes VALUES (?,?)";
        
        try(PreparedStatement rc = conexion.prepareStatement(sql)){
            for(Integer i: clientes){
                if(clienteTercero(idTercero, i)) continue;
                
                rc.setInt(1, i);
                rc.setInt(2, idTercero);
                
                rc.addBatch();
            }
            
            rc.executeBatch();
            rc.close();
        }catch(SQLException ex){
            return "Error al relaciona tercero con clientes: " + ex.getMessage();
        }
        return "correcto";
    }
    
    private boolean terceroRegimen(int idTercero, int idRegimen){
        String sql = "SELECT id_regimen FROM regiemenes_terceros WHERE id_tercero = ?";
        
        try(PreparedStatement tr = conexion.prepareStatement(sql)){
            tr.setInt(1, idTercero);
            
            ResultSet rs = tr.executeQuery();
            
            while(rs.next()){
                if(rs.getInt(1) == idRegimen){
                    rs.close();
                    return true;
                }
            }
                
            
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, "Error al obtener regimen y tercero: " + ex.getMessage());
            return true;
        }
        
        return false;
    }
    
    private String insertarRegimenes(int idTercero, ArrayList<Integer> regimenes){
        String sql = "INSERT INTO regimenes_clientes VALUES (?,?)";
        
        try(PreparedStatement ir = conexion.prepareStatement(sql)){
            for(Integer r : regimenes){
                ir.setInt(1, idTercero);
                ir.setInt(2, r);
                ir.addBatch();
            }
            
            ir.executeBatch();
            ir.close();
            return "correcto";
            
        }catch(SQLException ex){
            return "Error al insertar regimenes al tercero: " + ex.getMessage();
        }
    }
    
    public String borrarTercero(int idTercero){
        String sql = "UPDATE terceros SET id_estado = 3 WHERE id_tercero = ?";
        
        try(PreparedStatement deleteT = conexion.prepareStatement(sql)){
            deleteT.setInt(1, idTercero);
            
            deleteT.executeUpdate();
            deleteT.close();
            
            return "correcto";
        }catch(SQLException ex){
            return "Tercero no pudo ser eliminado: " + ex.getMessage();
        }
    }
    
    public Map<Integer,Terceros> getTerceros(){
        Map<Integer, Terceros> listaTerceros = new HashMap<>();
                
        try{
            String sql = "SELECT * FROM terceros";
            PreparedStatement getTerceros = conexion.prepareStatement(sql);
            
            ResultSet rs = getTerceros.executeQuery();
            
            while(rs.next()){
                Terceros t = new Terceros(rs.getInt("id_tercero"));
                t.nombre = rs.getString("nombre_cliente");
                t.rfc = rs.getString("rfc_cliente");
                t.cp = rs.getString("cp_cliente");
                t.correo = rs.getString("correo_cliente");
                
                listaTerceros.put(t.id_persona,t);                
            }
            
            getTerceros.close();
            rs.close();
            
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, "Erro al obtener terceros: " +ex.getMessage());
        }
        
        try{
            String sql = "SELECT id_tercero,id_regimen FROM regimenes_terceros";
            PreparedStatement reCl = conexion.prepareStatement(sql);
            
            ResultSet rs = reCl.executeQuery();
            
            while(rs.next()){
                int id_tercero = rs.getInt("id_tercero");
                
                if(listaTerceros.containsKey(id_tercero)){
                    Terceros t = listaTerceros.get(id_tercero);
                    
                    t.idsRegimenes.add(rs.getInt("id_regimen"));
                }
            }
            
            rs.close();
            reCl.close();
            
        }catch(SQLException ex){
           JOptionPane.showMessageDialog(null,
                   "Error al obtener regimenes de los terceros" + ex.getMessage());
        }
        
        return listaTerceros;
    }
    
    public String eliminarRegimenTercero(int idTercero, int idRegimen){
        String sql = "DELETE FROM regimenes_clientes WHERE id_tercero = ? AND  id_regimen = ?";
        
        try(PreparedStatement deleteR = conexion.prepareStatement(sql)){
            deleteR.setInt(1, idTercero);
            deleteR.setInt(2, idRegimen);
            
            deleteR.executeUpdate();
            deleteR.close();
            
            return "correcto";
            
        }catch(SQLException ex){
            return "Error al eliminar el regimen: " + ex.getMessage();
        }
    }
    
    public String insertarRegimenTerero(int idTercero, int idRegimen){
        String sql = "INSERT INTO regimenes_tercero VALUES (?,?)";
        
        if(terceroRegimen(idTercero, idRegimen)) return "El tercero ya tiene ese regimen";
        
        try(PreparedStatement insertarR = conexion.prepareStatement(sql)){
            insertarR.setInt(1, idTercero);
            insertarR.setInt(2, idRegimen);
            
            insertarR.executeUpdate();
            insertarR.close();
            
            return "correcto";
        }catch(SQLException ex){
            return "Error al insertar el regimen: " + ex.getMessage();
        }
    }
    
}
