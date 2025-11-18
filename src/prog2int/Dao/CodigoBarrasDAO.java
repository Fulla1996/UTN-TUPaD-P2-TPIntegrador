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
public class CodigoBarrasDAO implements GenericDAO<CodigoBarras> {

    private static final String INSERT_SQL = "INSERT INTO codigoBarras (id, tipo, valor, fechaAsignacion, observaciones) VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL = "UPDATE codigoBarras SET tipo = ?, valor = ?, fechaAsignacion = ?, observaciones = ? WHERE id = ?";

    /**
     * Query de soft delete.
     * Marca eliminado=TRUE sin borrar físicamente la fila.
     * Preserva integridad referencial y datos históricos.
     */
    private static final String DELETE_SQL = "UPDATE codigoBarras SET eliminado = TRUE WHERE id = ?";

    /**
     * Query para obtener codigo barras por ID.
     * Solo retorna activos (eliminado=FALSE).
     *
     * Campos del ResultSet:
     * - CodigoBarras: id, tipo, valor, fechaAsignacion, observacion
     */
    private static final String SELECT_BY_ID_SQL = "SELECT cb.id, cb.tipo, cb.valor, cb.fechaAsignacion, cb.observaciones, p.id " +
            "FROM codigoBarras cb " +
            "JOIN producto p on p.codigobarras = cb.id " +
            "WHERE cb.id = ? AND cb.eliminado = FALSE";

    private static final String SELECT_ID_EXIST = "SELECT id FROM codigoBarras WHERE id = ?";
    /**
     * Query para obtener todos los códigos de barras.
     * Filtra por eliminado=FALSE (solo códigos activos).
     */
    private static final String SELECT_ALL_SQL = "SELECT cb.id, cb.tipo, cb.valor, cb.fechaAsignacion, cb.observaciones, p.id " +
            "FROM codigoBarras cb " +
            "JOIN producto p on p.codigobarras = cb.id " +
            "WHERE cb.eliminado = FALSE";

    /**
     * Query de búsqueda por nombre o apellido con LIKE.
     * Permite búsqueda flexible: el usuario ingresa "juan" y encuentra "Juan", "Juana", etc.
     * Usa % antes y después del filtro: LIKE '%filtro%'
     * Solo personas activas (eliminado=FALSE).
     */
    private static final String SEARCH_BY_VALOR_SQL = "SELECT cb.id, cb.tipo, cb.valor, cb.fechaAsignacion, cb.observaciones, p.id " +
            "FROM codigoBarras cb " +
            "JOIN producto p on p.codigobarras = cb.id " +
            "WHERE cb.eliminado = FALSE AND (cb.valor = ?)";

    @Override
    public void insertar(CodigoBarras cb) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setCodigoBarrasParameters(stmt, cb);
            stmt.executeUpdate();
        }
    }

    @Override
    public void insertTx(CodigoBarras cb, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setCodigoBarrasParameters(stmt, cb);
            stmt.executeUpdate();
        }
    }

    //"UPDATE codigoBarras SET tipo = ?, valor = ?, fechaAsignacion = ?, observacion = ? WHERE id = ?"
    @Override
    public void actualizar(CodigoBarras cb) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, cb.getTipoCB().name());
            stmt.setString(2, cb.getValor());
            stmt.setDate(3, new java.sql.Date(cb.getFecha().getTime()));
            stmt.setString(4, cb.getObservaciones());
            stmt.setLong(5, cb.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se pudo actualizar el Codigo de Barras con ID: " + cb.getId());
            }
            
        }
    }

    //"UPDATE codigoBarras SET eliminado = TRUE WHERE id = ?"
    @Override
    public void eliminar(long id) throws Exception {
            try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No se encontró Codigo de Barras con ID: " + id);
            }
        }
    }

    @Override
    public CodigoBarras getById(long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setInt(1, (int)id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCodigoBarras(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<CodigoBarras> getAll() throws Exception {
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
    /*SEARCH_BY_VALOR_SQL = "SELECT cb.id, cb.tipo, cb.valor, cb.fechaAsignacion, cb.observaciones, p.id " +
            "FROM codigoBarras cb" +
            "JOIN producto p on p.codigobarras = cb.id" +
            "WHERE eliminado = FALSE AND (valor LIKE ?)";*/
    public CodigoBarras getByValor(String value) throws SQLException{
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(SEARCH_BY_VALOR_SQL)) {

            stmt.setString(1, value);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCodigoBarras(rs);
                }
            }
        }
        return null;
    }
    public boolean idExists(long id) throws Exception{
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ID_EXIST)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
                
            }
            } catch (SQLException e) {
                throw new Exception("Error al obtener Producto por ID: " + e.getMessage(), e);
            }
        }
    public CodigoBarras mapResultSetToCodigoBarras(ResultSet rs) throws SQLException{
        CodigoBarras cb = new CodigoBarras();
        cb.setId(rs.getInt("cb.id"));
        cb.setValor(rs.getString("cb.valor"));
        cb.setFecha(rs.getDate("cb.fechaAsignacion"));
        cb.setObservaciones(rs.getString("cb.observaciones"));
        cb.setTipoCB(TipoCB.valueOf(rs.getString("cb.tipo")));
        cb.setIdProducto(rs.getLong("p.id"));
        
        return cb;
        
    }
    
    
    /**
     * Setea los parámetros de domicilio en un PreparedStatement.
     * Método auxiliar usado por insertar() e insertTx().
     *
     * Parámetros seteados:
     * 1. calle (String)
     * 2. numero (String)
     *
     * @param stmt PreparedStatement con INSERT_SQL
     * @param domicilio Domicilio con los datos a insertar
     * @throws SQLException Si hay error al setear parámetros
     */
    
    //(id, tipo, valor, fechaAsignacion, observacion
    private void setCodigoBarrasParameters(PreparedStatement stmt, CodigoBarras cb) throws SQLException {
        stmt.setLong(1, cb.getId());
        stmt.setString(2, cb.getTipoCB().toString());
        stmt.setString(3, cb.getValor());
        stmt.setDate(4, new java.sql.Date(cb.getFecha().getTime()));
        stmt.setString(5, cb.getObservaciones());
    }

    /**
     * Obtiene el ID autogenerado por la BD después de un INSERT.
     * Asigna el ID generado al objeto domicilio.
     *
     * IMPORTANTE: Este método es crítico para mantener la consistencia:
     * - Después de insertar, el objeto domicilio debe tener su ID real de la BD
     * - PersonaServiceImpl.insertar() depende de esto para setear la FK:
     *   1. domicilioService.insertar(domicilio) → domicilio.id se setea aquí
     *   2. personaDAO.insertar(persona) → usa persona.getDomicilio().getId() para la FK
     * - Necesario para operaciones transaccionales que requieren el ID generado
     *
     * @param stmt PreparedStatement que ejecutó el INSERT con RETURN_GENERATED_KEYS
     * @param domicilio Objeto domicilio a actualizar con el ID generado
     * @throws SQLException Si no se pudo obtener el ID generado (indica problema grave)
     */

    
}
