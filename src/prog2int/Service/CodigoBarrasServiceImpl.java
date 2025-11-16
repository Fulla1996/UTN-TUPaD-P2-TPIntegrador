/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prog2int.Service;

import java.util.List;
import prog2int.Dao.CodigoBarrasDAO;

/**
 *
 * @author Fulla
 */
public class CodigoBarrasServiceImpl implements GenericService{

    private final CodigoBarrasDAO cbDAO;

    public CodigoBarrasServiceImpl(CodigoBarrasDAO cbDAO) {
        this.cbDAO = cbDAO;
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
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List getAll()  throws Exception {
        return cbDAO.getAll();
    }
    
}
