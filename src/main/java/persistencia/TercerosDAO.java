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
    //Conexion con la base de datos
    private final Connection conexion = ConectorBD.getConexion();
    
    /***
     * Funcion que inserta un tercero
     * @param t Variable de tipo Terceros que contiene los datos del nuevo tercero
     * @param clientes Variable de tipo ArrayList que contiene los ids de los clientes que contienen este tercero
     * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
     * "correcto"
     */
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
                if(rs.next()) {
                    id = rs.getInt(1);
                    t.id_persona = id;
                }
                rs.close();
            }
            
            it.close();
            
            if(id > 0 && !t.idsRegimenes.isEmpty()){
                
                if(!clientes.isEmpty()){
                    String clientesTercero = relacionarClientes(id, clientes);
                
                
                    if(!"correcto".equals(clientesTercero)){
                        conexion.rollback();
                        return clientesTercero;
                    }
                }
                
                String clientesRegimenes = insertarRegimenes(id, t.idsRegimenes);
                
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
    
    /***
     * Funcion que comprueba si un tercero ya tiene un cliente relacionado
     * @param idTercero Variable de tipo int con el id de tercero a buscar
     * @param idCliente Variable de tipo int con el id del cliente a buscar
     * @return True o False dependiendo el caso
     */
    private boolean clienteTercero(int idTercero, int idCliente){
        String sql = "SELECT id_tercero FROM terceros_clientes WHERE id_cliente = ? AND id_tercero = ?";
        
        try(PreparedStatement ct = conexion.prepareStatement(sql)){
            ct.setInt(1, idCliente);
            ct.setInt(2, idTercero);
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
    
    /***
     * Funcion que relaciona clientes con un tercero en especifico
     * @param idTercero Variable de tipo int que contiene el id del tercero a relacionar
     * @param clientes Variable de tipo "ArrayList" con los ids de los clientes que tienen este tercero
     * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
     * "correcto"
     */
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
    
    /***
     * Funcion de apoyo para saber si un tercero ya tiene un regimen
     * @param idTercero Variable de tipo int que contiene el id del tercero a buscar
     * @param idRegimen Variable de tipo int que contiene el id del regimen a buscar
     * @return True o False dependiendo de resultado
     */
    private boolean terceroRegimen(int idTercero, int idRegimen){
        String sql = "SELECT id_regimen FROM regimenes_terceros WHERE id_tercero = ?";
        
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
    
    
    /***
     * Funcion de apoyo que inserta la relacion de un tercero con sus regimenes
     * @param idTercero Variable de tipo int con el id del tercero a relacionar
     * @param regimenes Variable de tipo ArrayList con los ids de los regimenes a encolar
     * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
     * "correcto"
     */
    private String insertarRegimenes(int idTercero, ArrayList<Integer> regimenes){
        String sql = "INSERT INTO regimenes_terceros VALUES (?,?)";
        
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
    
    /**
     * Funcion que "Elimina" un tercero
     * @param idTercero Variable de tipo int con el id del tercero a eliminar
     * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
     * "correcto"
     */
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
    
    public java.util.List<Terceros> getTercerosLigeros() {
        String sql = "SELECT id_tercero, nombre_tercero FROM terceros WHERE id_estado = 1";
        java.util.List<Terceros> lista = new java.util.ArrayList<>();
        try(PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                Terceros t = new Terceros(rs.getInt("id_tercero"));
                t.nombre = rs.getString("nombre_tercero");
                lista.add(t);
            }
        }catch(SQLException e){
            throw new RuntimeException("Error al obtener terceros ligeros: " + e.getMessage(), e);
        }
        return lista;
    }

    public Terceros getTerceroById(int id) {
        String sql = "SELECT * FROM terceros WHERE id_tercero = ? AND id_estado = 1";
        try(PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    Terceros t = new Terceros(rs.getInt("id_tercero"));
                    t.nombre = rs.getString("nombre_tercero");
                    t.rfc = rs.getString("rfc_tercero");
                    t.cp = rs.getString("cp_tercero");
                    t.correo = rs.getString("correo_tercero");
                    
                    // Cargar regimenes de este tercero
                    String sqlReg = "SELECT id_regimen FROM regimenes_terceros WHERE id_tercero = ?";
                    try(PreparedStatement psReg = conexion.prepareStatement(sqlReg)){
                        psReg.setInt(1, id);
                        try(ResultSet rsReg = psReg.executeQuery()){
                            while(rsReg.next()){
                                t.idsRegimenes.add(rsReg.getInt("id_regimen"));
                            }
                        }
                    }
                    return t;
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error al obtener tercero por ID: " + e.getMessage(), e);
        }
        return null;
    }

    public String[] getDatosSatTercero(int id) {
        String sql = "SELECT nombre_tercero, rfc_tercero, cp_tercero, regimenes_fiscales FROM vw_copiar_sat_terceros WHERE id_tercero = ?";
        try(PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return new String[]{
                        rs.getString("nombre_tercero"),
                        rs.getString("rfc_tercero"),
                        rs.getString("cp_tercero"),
                        rs.getString("regimenes_fiscales")
                    };
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error al obtener datos SAT del tercero: " + e.getMessage(), e);
        }
        return null;
    }

    public java.util.ArrayList<Terceros> getTercerosDeClienteObj(int idCliente) {
        java.util.ArrayList<Terceros> lista = new java.util.ArrayList<>();
        String sql = "SELECT t.id_tercero, t.nombre_tercero FROM terceros t " +
                     "JOIN terceros_clientes tc ON t.id_tercero = tc.id_tercero " +
                     "WHERE tc.id_cliente = ? AND t.id_estado = 1";
        try(PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1, idCliente);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    Terceros t = new Terceros(rs.getInt("id_tercero"));
                    t.nombre = rs.getString("nombre_tercero");
                    lista.add(t);
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error al obtener terceros del cliente: " + e.getMessage(), e);
        }
        return lista;
    }

    
    /***
     * Funcion que elimina un regimen de un tercero
     * @param idTercero Variable de tipo int con el id del tercero a eliminar el regimen
     * @param idRegimen Variable de tipo int con el id del regimen a eliminar 
     * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
     * "correcto"
     */
    public String eliminarRegimenTercero(int idTercero, int idRegimen){
        String sql = "DELETE FROM regimenes_terceros WHERE id_tercero = ? AND id_regimen = ?";
        
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
    
    /***
     * Funcion que inserta un regimen de un tercero
     * @param idTercero Variable de tipo int con el id del tercero a insertar el regimen
     * @param idRegimen Variable de tipo int con el id del regimen a insertar 
     * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
     * "correcto"
     */
    public String insertarRegimenTerero(int idTercero, int idRegimen){
        String sql = "INSERT INTO regimenes_terceros VALUES (?,?)";
        
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
    
    
   /***
    * Variable que modifica un registro en la tabla terceros
    * @param t Variable de tipo Terceros que contiene los nuevos datos 
    * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
    * "correcto"
    */
    public String updateTercero(Terceros t){
        String sql = "UPDATE terceros SET nombre_tercero = ?, rfc_tercero = ?, cp_tercero = ?, correo_tercero = ? WHERE id_tercero = ?";
        
        try(PreparedStatement ut = conexion.prepareStatement(sql)){
            ut.setString(1, t.nombre);
            ut.setString(2, t.rfc);
            ut.setString(3, t.cp);
            ut.setString(4, t.correo);
            ut.setInt(5, t.id_persona);
            
            ut.executeUpdate();
            
            return "correcto";
        }catch(SQLException ex){
            return "Error al actualizar el tercero: " + ex.getMessage();
        }
    }
}
