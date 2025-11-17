/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prog2int.Dao;

import prog2int.Models.Producto;
import java.sql.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import prog2int.Config.DatabaseConnection;
import prog2int.Models.CodigoBarras;
import prog2int.Models.TipoCB;

/**
 *
 * @author Fulla
 */
public class ProductoDAO implements GenericDAO<Producto>{
    private static final String INSERT_SQL = "INSERT INTO producto (id, nombre, marca, categoria, precio, peso, codigoBarras) VALUES (?, ?, ?, ?, ?, ?, ?)";

    /**
    * Query de actualización de persona.
    * Actualiza nombre, apellido, dni y FK domicilio_id por id.
    * NO actualiza el flag eliminado (solo se modifica en soft delete).
    */
    private static final String UPDATE_SQL = "UPDATE producto SET nombre = ?, marca = ?, categoria = ?, precio = ?, peso = ?, codigoBarras = ? WHERE id = ?";

    /**
     * Query de soft delete.
     * Marca eliminado=TRUE sin borrar físicamente la fila.
     * Preserva integridad referencial y datos históricos.
     */
    private static final String DELETE_SQL = "UPDATE producto SET eliminado = TRUE WHERE id = ?";

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
            "p.peso, cb.id, cb.tipo,cb.valor, cb.fechaAsignacion, cb.observacion " +
            "FROM producto p JOIN codigoBarras cb ON p.codigoBarras = cb.id " +
            "WHERE p.id = ? AND p.eliminado = FALSE";

    /**
     * Query para obtener todas las personas activas.
     * LEFT JOIN con domicilios para cargar relaciones.
     * Filtra por eliminado=FALSE (solo personas activas).
     */
    private static final String SELECT_ALL_SQL = "SELECT p.id, p.nombre, p.marca, p.categoria, p.precio, " +
            "p.peso, cb.id, cb.tipo,cb.valor, cb.fechaAsignacion, cb.observacion " +
            "FROM producto p JOIN codigoBarras cb ON p.codigoBarras = cb.id " +
            "WHERE p.eliminado = FALSE";

    /**
     * Query de búsqueda por nombre o apellido con LIKE.
     * Permite búsqueda flexible: el usuario ingresa "juan" y encuentra "Juan", "Juana", etc.
     * Usa % antes y después del filtro: LIKE '%filtro%'
     * Solo personas activas (eliminado=FALSE).
     */
    private static final String SEARCH_BY_NAME_SQL = "SELECT p.id, p.nombre, p.marca, p.categoria, p.precio, " +
            "p.peso, cb.id, cb.tipo,cb.valor, cb.fechaAsignacion, cb.observacion " +
            "FROM producto p JOIN codigoBarras cb ON p.codigoBarras = cb.id " +
            "WHERE p.eliminado = FALSE AND (p.nombre LIKE ?)";

    /**
     * Query de búsqueda exacta por DNI.
     * Usa comparación exacta (=) porque el DNI es único (RN-001).
     * Usado por PersonaServiceImpl.validateDniUnique() para verificar unicidad.
     * Solo personas activas (eliminado=FALSE).
     */
    private static final String SEARCH_BY_MARCA_SQL = "SELECT p.id, p.nombre, p.marca, p.categoria, p.precio, " +
            "p.peso, cb.id, cb.tipo,cb.valor, cb.fechaAsignacion, cb.observacion " +
            "FROM producto p JOIN codigoBarras cb ON p.codigoBarras = cb.id " +
            "WHERE p.eliminado = FALSE AND (p.marca LIKE ?)";

    /**
     * DAO de domicilios (actualmente no usado, pero disponible para operaciones futuras).
     * Inyectado en el constructor por si se necesita coordinar operaciones.
     */
    private final CodigoBarrasDAO codigoBarrasDAO;

    /**
     * Constructor con inyección de DomicilioDAO.
     * Valida que la dependencia no sea null (fail-fast).
     *
     * @param domicilioDAO DAO de domicilios
     * @throws IllegalArgumentException si domicilioDAO es null
     */
    public ProductoDAO(CodigoBarrasDAO codigoBarrasDAO) {
        if (codigoBarrasDAO == null) {
            throw new IllegalArgumentException("DomicilioDAO no puede ser null");
        }
        this.codigoBarrasDAO = codigoBarrasDAO;
    }
    @Override
    public void insertar(Producto prod) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setProductoParameters(stmt, prod);
            stmt.executeUpdate();
        }
    }

    @Override
    public void insertTx(Producto prod, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setProductoParameters(stmt, prod);
            stmt.executeUpdate();
        }
    }

    //"UPDATE producto SET nombre = ?, marca = ?, categoria = ?, precio = ?, peso = ?, codigoBarras = ? WHERE id = ?";
    @Override
    public void actualizar(Producto prod) throws Exception {
            try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, prod.getNombre());
            stmt.setString(2, prod.getMarca());
            stmt.setString(3, prod.getCategoria());
            stmt.setDouble(4, prod.getPrecio());
            stmt.setDouble(5, prod.getPeso());
            stmt.setLong(6, prod.getCodigoBarras().getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se pudo actualizar el Codigo de Barras con ID: " + prod.getId());
            }
            
        }
    }

    @Override
    public void eliminar(long id) throws Exception {
            try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No se encontró Producto con ID: " + id);
            }
        }
    }

    @Override
    public Producto getById(long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProducto(rs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener Producto por ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List getAll() throws Exception {
        List<Producto> productos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {

            while (rs.next()) {
                productos.add(mapResultSetToProducto(rs));
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener todos los productos: " + e.getMessage(), e);
        }
        return productos;
    }
    //"INSERT INTO producto (id, nombre, marca, categoria, precio, peso, codigoBarras) VALUES (?, ?, ?, ?, ?, ?, ?)"
    private void setProductoParameters(PreparedStatement stmt, Producto producto) throws SQLException {
        stmt.setLong(1, producto.getId());
        stmt.setString(2, producto.getNombre());
        stmt.setString(3, producto.getMarca());
        stmt.setString(4, producto.getCategoria());
        stmt.setDouble(5, producto.getPrecio());
        stmt.setDouble(6, producto.getPeso());
        stmt.setLong(7, producto.getCodigoBarras().getId());
    }
    
    private Producto mapResultSetToProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setId(rs.getInt("id"));
        producto.setNombre(rs.getString("nombre"));
        producto.setCategoria(rs.getString("categoria"));
        producto.setPeso(rs.getDouble("peso"));
        producto.setPrecio(rs.getDouble("precio"));
        
        // Manejo correcto de LEFT JOIN: verificar si domicilio_id es NULL
        CodigoBarras cb = new CodigoBarras();
        cb.setId(rs.getInt("cb.id"));
        cb.setTipoCB(TipoCB.valueOf(rs.getString("cb.tipo")));
        cb.setValor(rs.getString("cb.valor"));
        cb.setFecha(rs.getDate("cb.fechaAsignacion"));
        cb.setObservaciones(rs.getString("cb.observacion"));
        
        producto.setCodigoBarras(cb);
        
        return producto;
    }
}
