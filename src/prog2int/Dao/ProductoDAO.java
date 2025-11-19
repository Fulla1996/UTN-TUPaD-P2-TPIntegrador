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
    
    /**
     * Query de insert de producto
     */
    private static final String INSERT_SQL = "INSERT INTO producto (id, nombre, marca, categoria, precio, peso, codigoBarras) VALUES (?, ?, ?, ?, ?, ?, ?)";

    /**
    * Query de actualización de producto.
    * NO actualiza el flag eliminado (solo se modifica en soft delete).
    */
    private static final String UPDATE_SQL = "UPDATE producto SET nombre = ?, marca = ?, categoria = ?, precio = ?, peso = ? WHERE id = ?";

    /**
     * Query de soft delete.
     * Marca eliminado=TRUE sin borrar físicamente la fila.
     * Preserva integridad referencial y datos históricos.
     */
    private static final String DELETE_SQL = "UPDATE producto SET eliminado = TRUE WHERE id = ?";

    /**
     * Query para obtener productos por ID.
     * JOIN con CodigoBarras para obtener todos los datosd el codigo de barras
     * Solo retorna productos activas (eliminado=FALSE).
     *
     * Campos del ResultSet:
     * - Producto: p.id, p.nombre, p.marca, p.categoria, p.precio, p.peso
     * - CodigoBarras: cb.id, cb.tipo,cb.valor, cb.fechaAsignacion, cb.observaciones
     */
    private static final String SELECT_BY_ID_SQL = "SELECT p.id, p.nombre, p.marca, p.categoria, p.precio, " +
            "p.peso, cb.id, cb.tipo,cb.valor, cb.fechaAsignacion, cb.observaciones " +
            "FROM producto p JOIN codigoBarras cb ON p.codigoBarras = cb.id " +
            "WHERE p.id = ? AND p.eliminado = FALSE";
    
    /**
     * Query para verificar existencia de ID independientemente de si se encuentra eliminado
     */
    private static final String SELECT_ID_EXIST = "SELECT id FROM producto WHERE id = ?";

    /**
     * Query para obtener todos los productos activos.
     * JOIN con codigos de barras para cargar relaciones.
     * Filtra por eliminado=FALSE (solo productos activos).
     */
    private static final String SELECT_ALL_SQL = "SELECT p.id, p.nombre, p.marca, p.categoria, p.precio, " +
            "p.peso, cb.id, cb.tipo,cb.valor, cb.fechaAsignacion, cb.observaciones " +
            "FROM producto p JOIN codigoBarras cb ON p.codigoBarras = cb.id " +
            "WHERE p.eliminado = FALSE";

    /**
     * Query de búsqueda por nombre con LIKE.
     * Se utiliza operador LIKE para busquedas parciales.
     * Usa % antes y después del filtro: LIKE '%filtro%'
     * Solo personas activas (eliminado=FALSE).
     */
    private static final String SEARCH_BY_NAME_SQL = "SELECT p.id, p.nombre, p.marca, p.categoria, p.precio, " +
            "p.peso, cb.id, cb.tipo,cb.valor, cb.fechaAsignacion, cb.observaciones " +
            "FROM producto p JOIN codigoBarras cb ON p.codigoBarras = cb.id " +
            "WHERE p.eliminado = FALSE AND (p.nombre LIKE ?)";

    /**
     * Query de búsqueda por marca o categoria.
     * Se utiliza operador LIKE para busquedas parciales.
     * Usa % antes y después del filtro: LIKE '%filtro%'
     * Solo productos activos (eliminado=FALSE).
     */
    private static final String SEARCH_BY_BRAND_SQL = "SELECT p.id, p.nombre, p.marca, p.categoria, p.precio, " +
            "p.peso, cb.id, cb.tipo,cb.valor, cb.fechaAsignacion, cb.observaciones " +
            "FROM producto p JOIN codigoBarras cb ON p.codigoBarras = cb.id " +
            "WHERE p.eliminado = FALSE AND ((p.marca LIKE ?) OR (p.categoria LIKE ?))";

    /**
     * DAO de CodigoBarras
     * Inyectado en el constructor por si se necesita coordinar operaciones.
     */
    private final CodigoBarrasDAO codigoBarrasDAO;

    /**
     * Constructor con inyección de CodigoBarrasDAO.
     * Valida que la dependencia no sea null (fail-fast).
     *
     * @param codigoBarrasDAO DAO de CodigoBarras
     * @throws IllegalArgumentException si domicilioDAO es null
     */
    public ProductoDAO(CodigoBarrasDAO codigoBarrasDAO) {
        if (codigoBarrasDAO == null) {
            throw new IllegalArgumentException("DomicilioDAO no puede ser null");
        }
        this.codigoBarrasDAO = codigoBarrasDAO;
    }
    
    /**
     * Inserta en la base de datos el objeto producto proporcionado
     * @param prod objeto Producto a insertar
     * @throws Exception 
     */
    @Override
    public void insertar(Producto prod) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setProductoParameters(stmt, prod);
            stmt.executeUpdate();
        }
    }

    /**
     * Funcion de insert que recibe conexión externa para ser usado en transacciones
     * @param prod objeto Producto a insertar
     * @param conn conexión externa
     * @throws Exception 
     */
    @Override
    public void insertTx(Producto prod, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setProductoParameters(stmt, prod);
            stmt.executeUpdate();
        }
    }

    
    //"UPDATE producto SET nombre = ?, marca = ?, categoria = ?, precio = ?, peso = ? WHERE id = ?";
        /**
     * Actualiza en la base de datos un producto
     * @param prod objeto con datos finales a setear en la base de datos
     * @throws Exception 
     */
    @Override
    public void actualizar(Producto prod) throws Exception {
            try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, prod.getNombre());
            stmt.setString(2, prod.getMarca());
            stmt.setString(3, prod.getCategoria());
            stmt.setDouble(4, prod.getPrecio());
            stmt.setDouble(5, prod.getPeso());
            stmt.setLong(6, prod.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se pudo actualizar el Producto con ID: " + prod.getId());
            }
            
        }
    }

        /**
     * Realiza la eliminacion lógica del producto con el id proporcionado
     * A su vez elimina el CodigoBarras asociado
     * @param id identificacion del producto a eliminar
     * @throws Exception 
     */
    @Override
    public void eliminar(long id) throws Exception {
            try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setLong(1, id);
            codigoBarrasDAO.eliminar(getById(id).getCodigoBarras().getId());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No se encontró Producto con ID: " + id);
            }
        }
    }

    
    /**
     * Trae el producto correspondiente al id proporcionado
     * @param id identificacion del producto
     * @return objeto producto correspondiente al id
     * @throws Exception 
     */
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

    /**
     * Trae todos los Productos de la base de datos
     * @return Lista con Productos
     * @throws Exception 
     */
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
    
    /*SEARCH_BY_NAME_SQL = "SELECT p.id, p.nombre, p.marca, p.categoria, p.precio, " +
            "p.peso, cb.id, cb.tipo,cb.valor, cb.fechaAsignacion, cb.observacion " +
            "FROM producto p JOIN codigoBarras cb ON p.codigoBarras = cb.id " +
            "WHERE p.eliminado = FALSE AND (p.nombre LIKE ?)";*/

    /**
     * Trae la lista de productos correspondiente a la busqueda por nombre en la base de datos
     * Permite busquedas por nombre parcial
     * @param name nombre del producto a buscar
     * @return Lista de Productos encontrada
     * @throws SQLException 
     */
    public List<Producto> getListByName(String name) throws Exception{
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El filtro de búsqueda no puede estar vacío");
        }
        
        List<Producto> productos = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(SEARCH_BY_NAME_SQL)) {

            String namefilter = "%" + name + "%";
            stmt.setString(1, namefilter);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    productos.add(mapResultSetToProducto(rs));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener Producto por ID: " + e.getMessage(), e);
        }
        return productos;
    }
    
    /*SEARCH_BY_BRAND_SQL = "SELECT p.id, p.nombre, p.marca, p.categoria, p.precio, " +
            "p.peso, cb.id, cb.tipo,cb.valor, cb.fechaAsignacion, cb.observacion " +
            "FROM producto p JOIN codigoBarras cb ON p.codigoBarras = cb.id " +
            "WHERE p.eliminado = FALSE AND ((p.marca LIKE ?) OR p.categoria LIKE ?)";*/
    
    /**
     * Trae la lista de productos correspondiente a la busqueda por marca o categoria en la base de datos
     * Permite busquedas por nombre parcial
     * @param brand marca o categoria para filtrar
     * @return Lista de Productos encontrada
     * @throws SQLException 
     */
        public List<Producto> getListByBrand(String brand) throws Exception{
        if (brand == null || brand.trim().isEmpty()) {
            throw new IllegalArgumentException("El filtro de búsqueda no puede estar vacío");
        }
        
        List<Producto> productos = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(SEARCH_BY_BRAND_SQL)) {

            String brandFilter = "%" + brand + "%";
            
            stmt.setString(1, brandFilter);
            stmt.setString(2, brandFilter);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    productos.add(mapResultSetToProducto(rs));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener Producto por ID: " + e.getMessage(), e);
        }
        return productos;
    }
    
    /**
     * Consulta simplificada para verificar existencia de un ID
     * ya sea Activo o Eliminado
     * @param id a buscar en la base de datos
     * @return True = existe el id, False = no existe el id
     * @throws Exception 
     */
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
    
    //"INSERT INTO producto (id, nombre, marca, categoria, precio, peso, codigoBarras) VALUES (?, ?, ?, ?, ?, ?, ?)"
   /**
    * Funcion para PreparedStatement donde se preparan todos los datos del Producto
    * @param stmt Statement a preparar
    * @param producto Objeto producto del cual se saca la información
    * @throws SQLException 
    */
        private void setProductoParameters(PreparedStatement stmt, Producto producto) throws SQLException {
        stmt.setLong(1, producto.getId());
        stmt.setString(2, producto.getNombre());
        stmt.setString(3, producto.getMarca());
        stmt.setString(4, producto.getCategoria());
        stmt.setDouble(5, producto.getPrecio());
        stmt.setDouble(6, producto.getPeso());
        stmt.setLong(7, producto.getCodigoBarras().getId());
    }
    
    /**
     * Mapea el resultado del query dentro de un nuevo objeto Producto
     * @param rs resultado de ejecución de PreparedStatement
     * @return Producto con todos sus campos leidos del query y los datos del CodigoBarras anexo
     * @throws SQLException 
     */
    
    private Producto mapResultSetToProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setId(rs.getInt("p.id"));
        producto.setNombre(rs.getString("p.nombre"));
        producto.setMarca(rs.getString("p.marca"));
        producto.setCategoria(rs.getString("p.categoria"));
        producto.setPeso(rs.getDouble("p.peso"));
        producto.setPrecio(rs.getDouble("p.precio"));
        
        // Manejo correcto de LEFT JOIN: verificar si domicilio_id es NULL
        CodigoBarras cb = new CodigoBarras();
        cb.setId(rs.getInt("cb.id"));
        cb.setTipoCB(TipoCB.valueOf(rs.getString("cb.tipo")));
        cb.setValor(rs.getString("cb.valor"));
        cb.setFecha(rs.getDate("cb.fechaAsignacion"));
        cb.setObservaciones(rs.getString("cb.observaciones"));
        
        producto.setCodigoBarras(cb);
        
        return producto;
    }
}
