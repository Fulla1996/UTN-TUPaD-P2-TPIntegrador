/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prog2int.Dao;

import java.sql.Connection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import prog2int.Config.DatabaseConnection;
import prog2int.Models.CodigoBarras;
import prog2int.Models.TipoCB;

/**
 *
 * @author Fulla
 */
public class CodigoBarrasDAO implements GenericDAO {

    private static final String INSERT_SQL = "INSERT INTO producto (id, nombre, marca, caategoria, precio, peso, codigoBarras) VALUES (?, ?, ?, ?, ?, ?, ?)";

    /**
    * Query de actualización de persona.
    * Actualiza nombre, apellido, dni y FK domicilio_id por id.
    * NO actualiza el flag eliminado (solo se modifica en soft delete).
    */
    private static final String UPDATE_SQL = "UPDATE personas SET nombre = ?, apellido = ?, dni = ?, domicilio_id = ? WHERE id = ?";

    /**
     * Query de soft delete.
     * Marca eliminado=TRUE sin borrar físicamente la fila.
     * Preserva integridad referencial y datos históricos.
     */
    private static final String DELETE_SQL = "UPDATE personas SET eliminado = TRUE WHERE id = ?";

    /**
     * Query para obtener persona por ID.
     * LEFT JOIN con domicilios para cargar la relación de forma eager.
     * Solo retorna personas activas (eliminado=FALSE).
     *
     * Campos del ResultSet:
     * - Persona: id, nombre, apellido, dni, domicilio_id
     * - Domicilio (puede ser NULL): dom_id, calle, numero
     */
    private static final String SELECT_BY_ID_SQL = "SELECT p.id, p.nombre, p.marca, p.categoria, p.precio, " +
            "p.peso, cb.valor " +
            "FROM producto p JOIN codigoBarras cb ON p.codigoBarras = cb.id " +
            "WHERE p.id = ? AND p.eliminado = FALSE";

    /**
     * Query para obtener todas las personas activas.
     * LEFT JOIN con domicilios para cargar relaciones.
     * Filtra por eliminado=FALSE (solo personas activas).
     */
    private static final String SELECT_ALL_SQL = "SELECT id, tipo, valor, fechaAsignacion, observaciones " +
            "FROM codigoBarras " +
            "WHERE eliminado = FALSE";

    /**
     * Query de búsqueda por nombre o apellido con LIKE.
     * Permite búsqueda flexible: el usuario ingresa "juan" y encuentra "Juan", "Juana", etc.
     * Usa % antes y después del filtro: LIKE '%filtro%'
     * Solo personas activas (eliminado=FALSE).
     */
    private static final String SEARCH_BY_VALOR_SQL = "SELECT id, tipo, valor, fechaAsignacion, observaciones " +
            "FROM codigoBarras" +
            "WHERE p.eliminado = FALSE AND (valor LIKE ?)";


    /**
     * DAO de domicilios (actualmente no usado, pero disponible para operaciones futuras).
     * Inyectado en el constructor por si se necesita coordinar operaciones.
     */
    
    
    @Override
    public void insertar(Object entidad) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void insertTx(Object entidad, Connection conn) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void actualizar(Object entidad) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void eliminar(int id) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object getById(int id) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List getAll() throws Exception {
        List<CodigoBarras> codigosBarras = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {

            while (rs.next()) {
                codigosBarras.add(mapResultSetToCodigoBarras(rs));
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener todas las personas: " + e.getMessage(), e);
        }
        return codigosBarras;
    }
    
    public CodigoBarras mapResultSetToCodigoBarras(ResultSet rs) throws SQLException{
        CodigoBarras cb = new CodigoBarras();
        cb.setId(rs.getInt("id"));
        cb.setValor(rs.getString("valor"));
        cb.setFecha(rs.getDate("fechaAsignacion"));
        cb.setObservaciones("observaciones");
        cb.setTipoCB(TipoCB.valueOf(rs.getString("tipo")));
        
        return cb;
        
    }
    
}
