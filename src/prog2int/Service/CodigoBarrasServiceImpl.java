/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prog2int.Service;

import java.util.List;
import prog2int.Dao.CodigoBarrasDAO;
import prog2int.Models.CodigoBarras;

/**
 *
 * @author Fulla
 */
public class CodigoBarrasServiceImpl implements GenericService<CodigoBarras>{

    private final CodigoBarrasDAO cbDAO;

    public CodigoBarrasServiceImpl(CodigoBarrasDAO cbDAO) {
        this.cbDAO = cbDAO;
    }
    
    @Override
    public void insertar(CodigoBarras entidad) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void actualizar(CodigoBarras entidad) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void eliminar(long id) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CodigoBarras getById(long id) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<CodigoBarras> getAll()  throws Exception {
        return cbDAO.getAll();
    }
    
}
