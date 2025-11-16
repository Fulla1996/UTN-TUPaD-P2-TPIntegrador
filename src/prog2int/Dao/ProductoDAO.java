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

/**
 *
 * @author Fulla
 */
public class ProductoDAO implements GenericDAO<Producto>{
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
    private static final String SELECT_ALL_SQL = "SELECT p.id, p.nombre, p.marca, p.categoria, p.precio, " +
            "p.peso, cb.valor " +
            "FROM producto p JOIN codigoBarras cb ON p.codigoBarras = cb.id " +
            "WHERE p.eliminado = FALSE";

    /**
     * Query de búsqueda por nombre o apellido con LIKE.
     * Permite búsqueda flexible: el usuario ingresa "juan" y encuentra "Juan", "Juana", etc.
     * Usa % antes y después del filtro: LIKE '%filtro%'
     * Solo personas activas (eliminado=FALSE).
     */
    private static final String SEARCH_BY_NAME_SQL = "SELECT p.id, p.nombre, p.apellido, p.dni, p.domicilio_id, " +
            "d.id AS dom_id, d.calle, d.numero " +
            "FROM personas p LEFT JOIN domicilios d ON p.domicilio_id = d.id " +
            "WHERE p.eliminado = FALSE AND (p.nombre LIKE ? OR p.apellido LIKE ?)";

    /**
     * Query de búsqueda exacta por DNI.
     * Usa comparación exacta (=) porque el DNI es único (RN-001).
     * Usado por PersonaServiceImpl.validateDniUnique() para verificar unicidad.
     * Solo personas activas (eliminado=FALSE).
     */
    private static final String SEARCH_BY_DNI_SQL = "SELECT p.id, p.nombre, p.apellido, p.dni, p.domicilio_id, " +
            "d.id AS dom_id, d.calle, d.numero " +
            "FROM personas p LEFT JOIN domicilios d ON p.domicilio_id = d.id " +
            "WHERE p.eliminado = FALSE AND p.dni = ?";

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
    public void insertar(Producto entidad) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void insertTx(Producto entidad, Connection conn) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void actualizar(Producto entidad) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void eliminar(long id) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Producto getById(long id) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return null;//mapResultSetToPersona(rs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener persona por ID: " + e.getMessage(), e);
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
            throw new Exception("Error al obtener todas las personas: " + e.getMessage(), e);
        }
        return productos;
    }
    
    private Producto mapResultSetToProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setId(rs.getInt("id"));
        producto.setNombre(rs.getString("nombre"));
        producto.setCategoria(rs.getString("categoria"));
        producto.setPeso(rs.getDouble("peso"));
        producto.setPrecio(rs.getDouble("precio"));

        // Manejo correcto de LEFT JOIN: verificar si domicilio_id es NULL
        /*int domicilioId = rs.getInt("domicilio_id");
        if (domicilioId > 0 && !rs.wasNull()) {
            Domicilio domicilio = new Domicilio();
            domicilio.setId(rs.getInt("dom_id"));
            domicilio.setCalle(rs.getString("calle"));
            domicilio.setNumero(rs.getString("numero"));
            persona.setDomicilio(domicilio);
        }*/

        return producto;
    }
}
