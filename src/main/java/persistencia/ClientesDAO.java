package persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import entidades.Cliente;
import entidades.Terceros;
import entidades.EFirmas;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

public class ClientesDAO {
    
    //Instancia de conexion a la base de datos directa de ConectorBD
    private Connection conexion = ConectorBD.getConexion();
    
    /**
     * Clase que agrega un registro en la tabla clientes y ademas le agrega los respectivos regimenes en 
     * la tabla regimenes-clientes
     * @param cliente Instancia de tipo Cliente que contiene los datos a insertar
     * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regesa
     * "correcto"
     */
    public String insertCliente(Cliente cliente) {
        return insertCliente(cliente, null);
    }

    public String insertCliente(Cliente cliente, EFirmas firma){
        String sql = "INSERT INTO clientes(nombre_cliente,rfc_cliente,cp_cliente,correo_cliente,m_honorarios_cliente,id_contador,id_estado) VALUES (?,?,?,?,?,?,?)";
        int id = -1;
        
        try{
            conexion.setAutoCommit(false);
            
            PreparedStatement insertC = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            insertC.setString(1, cliente.nombre);
            insertC.setString(2, cliente.rfc);
            insertC.setString(3, cliente.cp);
            insertC.setString(4, cliente.correo);
            insertC.setInt(5, cliente.honorarios);
            if (cliente.id_contador <= 0) {
                insertC.setNull(6, java.sql.Types.INTEGER);
            } else {
                insertC.setInt(6, cliente.id_contador);
            }
            insertC.setInt(7, 1);
            
            int insertado = insertC.executeUpdate();
            System.out.println("I" + insertado);
            
            if(insertado != 0){
                
                ResultSet rs = insertC.getGeneratedKeys();
                
                if(rs.next()) {
                    id = rs.getInt(1);
                    cliente.id_persona = id;
                }
                rs.close();
                System.out.println("id");
            }
            
            insertC.close();
            if(id > 0 && !cliente.idsRegimenes.isEmpty()){
                insertRegimenesClientes(id, cliente.idsRegimenes);
            }
            System.out.println("regimenes");
            
            if(id > 0 && firma != null) {
                firma.setIdCliente(id);
                EFirmasDAO eFDAO = new EFirmasDAO();
                eFDAO.insertFirmaTransaccional(firma, conexion);
                System.out.println("firma insertada transaccionalmente");
            }

            conexion.commit();
            return "correcto";
            
        }catch(SQLException e){ 
            
            try{
                if(conexion != null){
                    
                    conexion.rollback();
                    return "Transaccion revertida: " + e.getMessage();
                    
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
    
    /**
     * Funcion de apoyo para @insertCliente que ayuda en la insercion de regimenes 
     * @param id Id del cliente al cual le relacionaremos los regimenes
     * @param regimenes Variable de tipo ArrayList que contiene todos los ids de los regimenes a relacionar
     * @throws SQLException
     */
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
    
    
    public java.util.List<Cliente> getClientesLigeros() {
        String sql = "SELECT id_cliente, nombre_cliente FROM vw_clientes_activos";
        java.util.List<Cliente> lista = new java.util.ArrayList<>();
        try(PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                Cliente c = new Cliente(rs.getInt("id_cliente"));
                c.nombre = rs.getString("nombre_cliente");
                lista.add(c);
            }
        }catch(SQLException e){
            throw new RuntimeException("Error al obtener clientes ligeros: " + e.getMessage(), e);
        }
        return lista;
    }

    public Cliente getClienteById(int id) {
        String sql = "SELECT * FROM vw_clientes_activos WHERE id_cliente = ?";
        try(PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    Cliente c = new Cliente(rs.getInt("id_cliente"));
                    c.nombre = rs.getString("nombre_cliente");
                    c.rfc = rs.getString("rfc_cliente");
                    c.cp = rs.getString("cp_cliente");
                    c.correo = rs.getString("correo_cliente");
                    c.honorarios = rs.getInt("m_honorarios_cliente");
                    int idCont = rs.getInt("id_contador");
                    c.id_contador = rs.wasNull() ? 0 : idCont;
                    
                    // Cargar regimenes de este cliente
                    String sqlReg = "SELECT id_regimen FROM regimenes_clientes WHERE id_cliente = ?";
                    try(PreparedStatement psReg = conexion.prepareStatement(sqlReg)){
                        psReg.setInt(1, id);
                        try(ResultSet rsReg = psReg.executeQuery()){
                            while(rsReg.next()){
                                c.idsRegimenes.add(rsReg.getInt("id_regimen"));
                            }
                        }
                    }
                    return c;
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error al obtener cliente por ID: " + e.getMessage(), e);
        }
        return null;
    }

    public String[] getDatosSatCliente(int id) {
        String sql = "SELECT nombre_cliente, rfc_cliente, cp_cliente, regimenes_fiscales FROM vw_copiar_sat_clientes WHERE id_cliente = ?";
        try(PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return new String[]{
                        rs.getString("nombre_cliente"),
                        rs.getString("rfc_cliente"),
                        rs.getString("cp_cliente"),
                        rs.getString("regimenes_fiscales")
                    };
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error al obtener datos SAT del cliente: " + e.getMessage(), e);
        }
        return null;
    }

    public java.util.ArrayList<Cliente> getClientesDeContadorObj(int idContador) {
        java.util.ArrayList<Cliente> lista = new java.util.ArrayList<>();
        String sql = "SELECT id_cliente, nombre_cliente, rfc_cliente, m_honorarios_cliente FROM vw_clientes_activos WHERE id_contador = ?";
        try(PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1, idContador);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    Cliente c = new Cliente(rs.getInt("id_cliente"));
                    c.nombre = rs.getString("nombre_cliente");
                    c.rfc = rs.getString("rfc_cliente");
                    c.honorarios = rs.getInt("m_honorarios_cliente");
                    c.id_contador = idContador;
                    lista.add(c);
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error al obtener clientes del contador: " + e.getMessage(), e);
        }
        return lista;
    }

    public java.util.ArrayList<Cliente> getClientesSinContador() {
        java.util.ArrayList<Cliente> lista = new java.util.ArrayList<>();
        String sql = "SELECT id_cliente, nombre_cliente, rfc_cliente, m_honorarios_cliente FROM clientes WHERE id_contador IS null";
        try(PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                Cliente c = new Cliente(rs.getInt("id_cliente"));
                c.nombre = rs.getString("nombre_cliente");
                c.rfc = rs.getString("rfc_cliente");
                c.honorarios = rs.getInt("m_honorarios_cliente");
                c.id_contador = 0;
                lista.add(c);
            }
        }catch(SQLException e){
            throw new RuntimeException("Error al obtener clientes sin contador: " + e.getMessage(), e);
        }
        return lista;
    }

    public String asignarContador(int idCliente, int idContador) {
        String sql = "UPDATE clientes SET id_contador = ? WHERE id_cliente = ?";
        try(PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1, idContador);
            ps.setInt(2, idCliente);
            ps.executeUpdate();
            return "correcto";
        }catch(SQLException ex){
            return "Error al asignar contador al cliente: " + ex.getMessage();
        }
    }

    public String desasignarContador(int idCliente) {
        String sql = "UPDATE clientes SET id_contador = NULL WHERE id_cliente = ?";
        try(PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1, idCliente);
            ps.executeUpdate();
            return "correcto";
        }catch(SQLException ex){
            return "Error al quitar contador del cliente: " + ex.getMessage();
        }
    }


    
    /**
     * Elimina un Regimen del cliente especificado de la tabla Regimenes-Clientes
     * @param idCliente Id del cliente al que se le eliminara el regimen
     * @param idRegimen Id del Regimen a eliminar de dicho cliente
     * "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regesa
     * "correcto"
     */
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
    
    /***
     * Comprueba si un regimen ya esta relacionado con un cliente, es funcion de apoyo para la funcion
     * @see ClientesDAO.agregarRegimenCliente()
     * @param idCliente Id del cliente a comprobar su relacion con el regimen
     * @param idRegimen Id del regimen a comprobar su relacion con el cliente
     * @return Retorna un valor de tipo Boolean
     */
    private boolean regimenYaExistente(int idCliente, int idRegimen){
        String sql = "SELECT id_regimen FROM regimenes_clientes WHERE id_cliente = ?";
        
        try(PreparedStatement re = conexion.prepareStatement(sql)){
            re.setInt(1, idCliente);
            
            ResultSet r = re.executeQuery();
            
            while(r.next()){
                if(r.getInt(1) == idRegimen){
                    r.close();
                    return true;
                }
            }
            
            
        }catch(SQLException ex){
            System.err.println("Fallo al comporbar regimen existente: " + ex.getMessage());
            return true;
        }
        
        return false;
        
    }
    
    /***
     * Funcion para agregar un regimen a un cliente en especifico
     * @param idCliente Variable de tipo int que contiene el id del cliente a relacionar
     * @param idRegimen Variable de tipo int que contiene el id del regimen a relacionar
     * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
     * "correcto"
     */
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
    
    /***
     * Funcion que retorna los Ids de los terceros relacionados con un cliente especifico en la tabla 
     * terceros_clientes
     * @param idCliente Variable de tipo int que contiene el Id del cliente del cual buscaremos sus terceros
     * @return retorna una variable de tipo ArrayList con los Ids de los terceros
     * en caso de no tener se regresa un ArrayList vacio
     */
    public ArrayList<Integer>  getTercerosCliente(int idCliente){
        ArrayList<Integer> ter = new ArrayList<>();
        
        String sql = "SELECT id_tercero FROM terceros_clientes WHERE id_cliente = ?";
        
        try(PreparedStatement ts = conexion.prepareStatement(sql)){
            
            ts.setInt(1, idCliente);
            
            ResultSet rs = ts.executeQuery();
            
            while(rs.next()){
                ter.add(rs.getInt(1));
            }
            
        }catch(SQLException ex){
            System.err.println("error al obtener los terceros" + ex.getMessage());
            
        }
        
        return ter;
    }
    
    /**
     * Funcion que "Elimina" un cliente, solo le cambia el estado de Activo a Baja 
     * @param idCliente Variable de tipo int que contiene el Id del cliente a "Eliminar"
     * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
     * "correcto"
     */
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
    

    
    // Funciones nuevas
    
    /**
     * Funcion que "Modifica" los valores de un registro de cliente en especifico
     * @param c Instancia de tipo Cliente que contiene los datos nuevos 
     * @param idCliente Variable de tipo int que contiene el id del cliente a modificar
     * @return "El status devuelto por el gestor de base de datos. En caso de ser correcto solo regresa
     * "correcto"
     */
    public String updateCliente(Cliente c, int idCliente){
        String sql = "UPDATE clientes SET nombre_cliente = ?, cp_cliente = ?, correo_cliente = ?, m_honorarios_cliente = ?, id_contador = ? WHERE id_cliente = ?";
        
        try(PreparedStatement update = conexion.prepareStatement(sql)){
            
            update.setString(1, c.nombre);
            update.setString(2, c.cp);
            update.setString(3, c.correo);
            update.setInt(4, c.honorarios);
            if (c.id_contador <= 0) {
                update.setNull(5, java.sql.Types.INTEGER);
            } else {
                update.setInt(5, c.id_contador);
            }
            update.setInt(6, idCliente);
            
            update.execute();
            
            return "correcto";
            
        }catch(SQLException ex){
            return "Error al editar: " + ex.getMessage();
        }
    }
}
