/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prog2int.Service;

import java.sql.Connection;
import java.util.List;
import prog2int.Config.DatabaseConnection;
import prog2int.Dao.ProductoDAO;
import prog2int.Models.CodigoBarras;
import prog2int.Models.Producto;

/**
 *
 * @author Fulla
 */
public class ProductoServiceImpl implements GenericService<Producto>{

    private final ProductoDAO productoDAO;
    private final CodigoBarrasServiceImpl codigoBarrasServiceImpl;

    public ProductoServiceImpl(ProductoDAO productoDAO, CodigoBarrasServiceImpl codigoBarrasServiceImpl) {
        if (productoDAO == null) {
            throw new IllegalArgumentException("ProductoDAO no puede ser null");
        }
        if (codigoBarrasServiceImpl == null) {
            throw new IllegalArgumentException("CodigoBarrasServiceImpl no puede ser null");
        }
        this.productoDAO = productoDAO;
        this.codigoBarrasServiceImpl = codigoBarrasServiceImpl;
    }
    
    
    @Override
    public void insertar(Producto prod) throws Exception {
        productoDAO.insertar(prod);
    }

    @Override
    public void actualizar(Producto prod) throws Exception {
        productoDAO.actualizar(prod);
    }

    @Override
    public void eliminar(long id) throws Exception {
        productoDAO.eliminar(id);
    }

    @Override
    public Producto getById(long id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        return productoDAO.getById(id);
    }

    @Override
    public List getAll() throws Exception {
        return productoDAO.getAll();
    }
    
    public List getByName(String name) throws Exception{
        return productoDAO.getListByName(name);
    }
    
    public List getByBrand(String brand) throws Exception{
        return productoDAO.getListByBrand(brand);
    }

    public CodigoBarrasServiceImpl getCodigoBarrasServiceImpl() {
        return codigoBarrasServiceImpl;
    }
    
    public void insertarTx(Producto prod, CodigoBarras cb, Connection conn){
        
    }
    
}
