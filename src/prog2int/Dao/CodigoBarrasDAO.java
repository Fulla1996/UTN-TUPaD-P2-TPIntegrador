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
    private static final String DELETE_SQL = "UPDATE codigoBarras SET eliminado = TRUE WHERE id = ?";
    private static final String SELECT_BY_ID_SQL = "SELECT id, tipo, valor, fechaAsignacion, observaciones " +
            "FROM CodigoBarras " +
            "WHERE id = ? AND eliminado = FALSE";

    /**
     * Query para obtener todos los códigos de barras.
     * Filtra por eliminado=FALSE (solo códigos activos).
     */
    private static final String SELECT_ALL_SQL = "SELECT id, tipo, valor, fechaAsignacion, observaciones " +
            "FROM codigoBarras " +
            "WHERE eliminado = FALSE";


    private static final String SEARCH_BY_VALOR_SQL = "SELECT id, tipo, valor, fechaAsignacion, observaciones " +
            "FROM codigoBarras " +
            "WHERE eliminado = FALSE AND (valor LIKE ?)";

    public Long getMaxId() throws Exception {
        String sql = "SELECT MAX(id) AS max_id FROM codigoBarras";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                long max = rs.getLong("max_id");
                return rs.wasNull() ? null : max;
            }
        }
        return null;
    }
    
    @Override
    public void insertar(CodigoBarras cb) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            setCodigoBarrasParameters(stmt, cb);
            stmt.executeUpdate();
        }
    }

    @Override
    public void insertTx(CodigoBarras cb, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            setCodigoBarrasParameters(stmt, cb);
            stmt.executeUpdate();
        }
    }

    @Override
    public void actualizar(CodigoBarras cb) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, cb.getTipoCB().toString());
            stmt.setString(2, cb.getValor());
            stmt.setDate(3, new java.sql.Date(cb.getFecha().getTime()));
            stmt.setString(4, cb.getObservaciones());
            stmt.setLong(5, cb.getId());

            stmt.executeUpdate();
        }
    }    

    @Override
    public void eliminar(long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }    

    @Override
    public CodigoBarras getById(long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setLong(1, id);

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
        List<CodigoBarras> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {

            while (rs.next()) {
                lista.add(mapResultSetToCodigoBarras(rs));
            }
        }
        return lista;
    }
    
    public CodigoBarras mapResultSetToCodigoBarras(ResultSet rs) throws SQLException{
        CodigoBarras cb = new CodigoBarras();
        cb.setId(rs.getInt("id"));
        cb.setValor(rs.getString("valor"));
        cb.setFecha(rs.getDate("fechaAsignacion"));
        cb.setObservaciones(rs.getString("observaciones"));
        cb.setTipoCB(TipoCB.valueOf(rs.getString("tipo")));
        
        return cb;
        
    }
    
    //(id, tipo, valor, fechaAsignacion, observacion
    private void setCodigoBarrasParameters(PreparedStatement stmt, CodigoBarras cb) throws SQLException {
        stmt.setLong(1, cb.getId());
        stmt.setString(2, cb.getTipoCB().toString());
        stmt.setString(3, cb.getValor());
        stmt.setDate(4, new java.sql.Date(cb.getFecha().getTime()));
        stmt.setString(5, cb.getObservaciones());
    }

    
}
