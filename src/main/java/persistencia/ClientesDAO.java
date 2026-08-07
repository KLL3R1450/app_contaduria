package persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import entidades.Cliente;
import entidades.Terceros;
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
    
    
    /***
     * Funcion que retorna todos los clientes, con estatus de Activo, y los guarda en una varible de tipo 
     * Map para su facilidad de busqueda por ID
     * @return Un Map de tipo Integer,Cliente con todos los clientes activos
     */
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
            throw new RuntimeException("Fallo al obtener clientes de la base de datos: " + e.getMessage(), e);
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
            throw new RuntimeException("Fallo al obtener regimenes de clientes: " + ex.getMessage(), ex);
        }

        return cls;
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
    
    /**
     * Funcion que obtiene los clientes que estan relacionados con un contador en especifico
     * @param idContador Variable de tipo int que contiene el Id del contador del cual buscaremos sus clientes
     * @return Una variable de tipo ArrayList con los Ids de los clientes del contador especificado
     */
    protected ArrayList<Integer> getClientesDeContador(int idContador){
        ArrayList<Integer> listaClientes = new ArrayList<>();
        
        String sql = "SELECT id_cliente FROM clientes WHERE id_contador = ?";
        
        try(PreparedStatement lc = conexion.prepareStatement(sql)){
            
            lc.setInt(1, idContador);
            
            ResultSet rs = lc.executeQuery();
            
            while(rs.next()){
                listaClientes.add(rs.getInt("id_cliente"));
            }
            
            
        }catch(SQLException ex){
            System.err.println("Erro al obtener los clientes del contador: " + ex.getMessage());
        } 
        return listaClientes;
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
            update.setInt(5, c.id_contador);
            update.setInt(6, idCliente);
            
            update.execute();
            
            return "correcto";
            
        }catch(SQLException ex){
            return "Error al editar: " + ex.getMessage();
        }
    }
}
