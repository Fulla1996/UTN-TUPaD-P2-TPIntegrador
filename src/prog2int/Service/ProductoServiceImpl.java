/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prog2int.Service;

import java.util.List;
import prog2int.Dao.ProductoDAO;

/**
 *
 * @author Fulla
 */
public class ProductoServiceImpl implements GenericService{

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
    public void insertar(Object entidad) throws Exception {
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
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        return productoDAO.getById(id);
    }

    @Override
    public List getAll() throws Exception {
        return productoDAO.getAll();
    }
    
}
