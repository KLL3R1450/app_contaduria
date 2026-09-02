package persistencia;

import entidades.Terceros;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class TercerosDAO {
    
    /***
     * Funcion que inserta un tercero
     * @param t Variable de tipo Terceros que contiene los datos del nuevo tercero
     * @param clientes Variable de tipo ArrayList que contiene los ids de los clientes que contienen este tercero
     * @return "correcto" o mensaje de error
     */
    public String insertTercero(Terceros t, ArrayList<Integer> clientes) {
        String sql = "INSERT INTO terceros(nombre_tercero,rfc_tercero,cp_tercero,correo_tercero) VALUES (?,?,?,?)";
        int id = -1;
        
        try (Connection conexion = ConectorBD.getConexion()) {
            conexion.setAutoCommit(false);
            
            try (PreparedStatement it = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                it.setString(1, t.nombre);
                it.setString(2, t.rfc);
                it.setString(3, t.cp);
                it.setString(4, t.correo);
                int insertado = it.executeUpdate();
                
                if (insertado > 0) {
                    try (ResultSet rs = it.getGeneratedKeys()) {
                        if (rs.next()) {
                            id = rs.getInt(1);
                            t.id_persona = id;
                        }
                    }
                }
            }
            
            if (id > 0 && !t.idsRegimenes.isEmpty()) {
                if (!clientes.isEmpty()) {
                    String clientesTercero = relacionarClientes(conexion, id, clientes);
                    if (!"correcto".equals(clientesTercero)) {
                        conexion.rollback();
                        return clientesTercero;
                    }
                }
                
                String clientesRegimenes = insertarRegimenes(conexion, id, t.idsRegimenes);
                if (!"correcto".equals(clientesRegimenes)) {
                    conexion.rollback();
                    return clientesRegimenes;
                }
            }
            
            conexion.commit();
            return "correcto";
            
        } catch (SQLException ex) {
            return "Fallo al insertar el tercero: " + ex.getMessage();
        }
    }
    
    private boolean clienteTercero(Connection conn, int idTercero, int idCliente) {
        String sql = "SELECT id_tercero FROM terceros_clientes WHERE id_cliente = ? AND id_tercero = ?";
        try (PreparedStatement ct = conn.prepareStatement(sql)) {
            ct.setInt(1, idCliente);
            ct.setInt(2, idTercero);
            try (ResultSet rs = ct.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            return false;
        }
    }
    
    public String relacionarClientes(int idTercero, ArrayList<Integer> clientes) {
        try (Connection conn = ConectorBD.getConexion()) {
            return relacionarClientes(conn, idTercero, clientes);
        } catch (SQLException ex) {
            return "Error al conectar con la base de datos: " + ex.getMessage();
        }
    }

    private String relacionarClientes(Connection conn, int idTercero, ArrayList<Integer> clientes) {
        String sql = "INSERT INTO terceros_clientes VALUES (?,?)";
        try (PreparedStatement rc = conn.prepareStatement(sql)) {
            for (Integer i : clientes) {
                if (clienteTercero(conn, idTercero, i)) continue;
                rc.setInt(1, i);
                rc.setInt(2, idTercero);
                rc.addBatch();
            }
            rc.executeBatch();
            return "correcto";
        } catch (SQLException ex) {
            return "Error al relacionar tercero con clientes: " + ex.getMessage();
        }
    }
    
    private boolean terceroRegimen(Connection conn, int idTercero, int idRegimen) {
        String sql = "SELECT id_regimen FROM regimenes_terceros WHERE id_tercero = ?";
        try (PreparedStatement tr = conn.prepareStatement(sql)) {
            tr.setInt(1, idTercero);
            try (ResultSet rs = tr.executeQuery()) {
                while (rs.next()) {
                    if (rs.getInt(1) == idRegimen) {
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            return true;
        }
        return false;
    }
    
    private String insertarRegimenes(Connection conn, int idTercero, ArrayList<Integer> regimenes) {
        String sql = "INSERT INTO regimenes_terceros VALUES (?,?)";
        try (PreparedStatement ir = conn.prepareStatement(sql)) {
            for (Integer r : regimenes) {
                ir.setInt(1, idTercero);
                ir.setInt(2, r);
                ir.addBatch();
            }
            ir.executeBatch();
            return "correcto";
        } catch (SQLException ex) {
            return "Error al insertar regimenes al tercero: " + ex.getMessage();
        }
    }
    
    public String borrarTercero(int idTercero) {
        String sql = "UPDATE terceros SET id_estado = 3 WHERE id_tercero = ?";
        try (Connection conexion = ConectorBD.getConexion();
             PreparedStatement deleteT = conexion.prepareStatement(sql)) {
            deleteT.setInt(1, idTercero);
            deleteT.executeUpdate();
            return "correcto";
        } catch (SQLException ex) {
            return "Tercero no pudo ser eliminado: " + ex.getMessage();
        }
    }
    
    public java.util.List<Terceros> getTercerosLigeros() {
        String sql = "SELECT id_tercero, nombre_tercero FROM terceros WHERE id_estado = 1";
        java.util.List<Terceros> lista = new java.util.ArrayList<>();
        try (Connection conexion = ConectorBD.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Terceros t = new Terceros(rs.getInt("id_tercero"));
                t.nombre = rs.getString("nombre_tercero");
                lista.add(t);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener terceros ligeros: " + e.getMessage(), e);
        }
        return lista;
    }

    public Terceros getTerceroById(int id) {
        String sql = "SELECT * FROM terceros WHERE id_tercero = ? AND id_estado = 1";
        try (Connection conexion = ConectorBD.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Terceros t = new Terceros(rs.getInt("id_tercero"));
                    t.nombre = rs.getString("nombre_tercero");
                    t.rfc = rs.getString("rfc_tercero");
                    t.cp = rs.getString("cp_tercero");
                    t.correo = rs.getString("correo_tercero");
                    
                    String sqlReg = "SELECT id_regimen FROM regimenes_terceros WHERE id_tercero = ?";
                    try (PreparedStatement psReg = conexion.prepareStatement(sqlReg)) {
                        psReg.setInt(1, id);
                        try (ResultSet rsReg = psReg.executeQuery()) {
                            while (rsReg.next()) {
                                t.idsRegimenes.add(rsReg.getInt("id_regimen"));
                            }
                        }
                    }
                    return t;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener tercero por ID: " + e.getMessage(), e);
        }
        return null;
    }

    public String[] getDatosSatTercero(int id) {
        String sql = "SELECT nombre_tercero, rfc_tercero, cp_tercero, regimenes_fiscales FROM vw_copiar_sat_terceros WHERE id_tercero = ?";
        try (Connection conexion = ConectorBD.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                        rs.getString("nombre_tercero"),
                        rs.getString("rfc_tercero"),
                        rs.getString("cp_tercero"),
                        rs.getString("regimenes_fiscales")
                    };
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener datos SAT del tercero: " + e.getMessage(), e);
        }
        return null;
    }

    public java.util.ArrayList<Terceros> getTercerosDeClienteObj(int idCliente) {
        java.util.ArrayList<Terceros> lista = new java.util.ArrayList<>();
        String sql = "SELECT t.id_tercero, t.nombre_tercero FROM terceros t " +
                     "JOIN terceros_clientes tc ON t.id_tercero = tc.id_tercero " +
                     "WHERE tc.id_cliente = ? AND t.id_estado = 1";
        try (Connection conexion = ConectorBD.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Terceros t = new Terceros(rs.getInt("id_tercero"));
                    t.nombre = rs.getString("nombre_tercero");
                    lista.add(t);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener terceros del cliente: " + e.getMessage(), e);
        }
        return lista;
    }

    public String eliminarRegimenTercero(int idTercero, int idRegimen) {
        String sql = "DELETE FROM regimenes_terceros WHERE id_tercero = ? AND id_regimen = ?";
        try (Connection conexion = ConectorBD.getConexion();
             PreparedStatement deleteR = conexion.prepareStatement(sql)) {
            deleteR.setInt(1, idTercero);
            deleteR.setInt(2, idRegimen);
            deleteR.executeUpdate();
            return "correcto";
        } catch (SQLException ex) {
            return "Error al eliminar el regimen: " + ex.getMessage();
        }
    }
    
    public String insertarRegimenTerero(int idTercero, int idRegimen) {
        try (Connection conexion = ConectorBD.getConexion()) {
            if (terceroRegimen(conexion, idTercero, idRegimen)) return "El tercero ya tiene ese regimen";
            
            String sql = "INSERT INTO regimenes_terceros VALUES (?,?)";
            try (PreparedStatement insertarR = conexion.prepareStatement(sql)) {
                insertarR.setInt(1, idTercero);
                insertarR.setInt(2, idRegimen);
                insertarR.executeUpdate();
                return "correcto";
            }
        } catch (SQLException ex) {
            return "Error al insertar el regimen: " + ex.getMessage();
        }
    }
    
    public String updateTercero(Terceros t) {
        String sql = "UPDATE terceros SET nombre_tercero = ?, rfc_tercero = ?, cp_tercero = ?, correo_tercero = ? WHERE id_tercero = ?";
        try (Connection conexion = ConectorBD.getConexion();
             PreparedStatement ut = conexion.prepareStatement(sql)) {
            ut.setString(1, t.nombre);
            ut.setString(2, t.rfc);
            ut.setString(3, t.cp);
            ut.setString(4, t.correo);
            ut.setInt(5, t.id_persona);
            ut.executeUpdate();
            return "correcto";
        } catch (SQLException ex) {
            return "Error al actualizar el tercero: " + ex.getMessage();
        }
    }
}
