package persistencia;

import entidades.Contadores;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ContadoresDAO {

    /**
     * Funcion que obtiene todos los contadores del despacho
     * @return Variable de tipo Map con los contadores y sus Ids para busqueda
     */
    public Map<Integer, Contadores> getContadores() {
        String sql = "SELECT * FROM contadores WHERE id_estado = 1";
        Map<Integer, Contadores> contadores = new HashMap<>();

        try (Connection conexion = ConectorBD.getConexion();
             PreparedStatement gc = conexion.prepareStatement(sql);
             ResultSet c = gc.executeQuery()) {

            while (c.next()) {
                Contadores co = new Contadores(
                        c.getInt("id_contador"),
                        c.getString("nombre_contador"),
                        c.getString("contacto_contador")
                );
                contadores.put(co.getId(), co);
            }

            return contadores;

        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener los contadores: " + ex.getMessage(), ex);
        }
    }

    /***
     * Funcion que inserta un registro en la tabla Contadores
     * @param conta Instancia de tipo Contadores que contiene los datos a insertar
     * @return "correcto" o mensaje de error
     */
    public String insertContador(Contadores conta) {
        String sql = "INSERT INTO contadores(nombre_contador,contacto_contador,id_estado) VALUES (?,?,?)";

        try (Connection conexion = ConectorBD.getConexion();
             PreparedStatement ic = conexion.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ic.setString(1, conta.nombre);
            ic.setString(2, conta.contacto);
            ic.setInt(3, 1);

            ic.executeUpdate();

            try (ResultSet rs = ic.getGeneratedKeys()) {
                if (rs.next()) {
                    conta.setId(rs.getInt(1));
                }
            }

            return "correcto";

        } catch (SQLException ex) {
            return "Error al ingresar el contador: " + ex.getMessage();
        }
    }

    public String insertContador(String nombre) {
        Contadores c = new Contadores(nombre, "SIN CONTACTO");
        return insertContador(c);
    }

    /**
     * Funcion que "Elimina" (da de baja) un registro en la tabla contadores
     * y libera transaccionalmente a todos sus clientes asociados dejándolos sin contador.
     * @param id_contador Variable de tipo int que contiene el id del contador a eliminar
     * @return "correcto" o mensaje de error
     */
    public String deleteContador(int id_contador) {
        String sqlRelease = "UPDATE clientes SET id_contador = NULL WHERE id_contador = ?";
        String sqlDelete = "UPDATE contadores SET id_estado = 2 WHERE id_contador = ?";

        try (Connection conexion = ConectorBD.getConexion()) {
            conexion.setAutoCommit(false);

            try (PreparedStatement psRel = conexion.prepareStatement(sqlRelease)) {
                psRel.setInt(1, id_contador);
                psRel.executeUpdate();
            }

            try (PreparedStatement psDel = conexion.prepareStatement(sqlDelete)) {
                psDel.setInt(1, id_contador);
                psDel.executeUpdate();
            }

            conexion.commit();
            return "correcto";
        } catch (SQLException ex) {
            return "Error al dar de baja el contador: " + ex.getMessage();
        }
    }

    /**
     * Funcion que permite modificar el nombre de un contador
     * @param id_contador ID del contador a modificar
     * @param nuevoNombre Nuevo nombre para el contador
     * @return "correcto" o mensaje de error
     */
    public String updateNombreContador(int id_contador, String nuevoNombre) {
        String sql = "UPDATE contadores SET nombre_contador = ? WHERE id_contador = ?";

        try (Connection conexion = ConectorBD.getConexion();
             PreparedStatement un = conexion.prepareStatement(sql)) {
            un.setString(1, nuevoNombre);
            un.setInt(2, id_contador);
            un.executeUpdate();
            return "correcto";
        } catch (SQLException ex) {
            return "Error al actualizar el nombre del contador: " + ex.getMessage();
        }
    }

    /**
     * Funcion que permite modificar un registro, solo su contacto, de la tabla contadores
     * @param contacto Variable de tipo String con el datos de contacto del contador
     * @param id_contador Variable de tipo int con el id del contador a modificar
     * @return "correcto" o mensaje de error
     */
    public String updateContactoContador(String contacto, int id_contador) {
        String sql = "UPDATE contadores SET contacto_contador = ? WHERE id_contador = ?";

        try (Connection conexion = ConectorBD.getConexion();
             PreparedStatement ucc = conexion.prepareStatement(sql)) {
            ucc.setString(1, contacto);
            ucc.setInt(2, id_contador);

            ucc.executeUpdate();
            return "correcto";

        } catch (SQLException ex) {
            return "Fallo al actualizar el contacto del contador: " + ex.getMessage();
        }
    }
}
