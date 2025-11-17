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
        if (cbDAO == null) {
            throw new IllegalArgumentException("CodigoBarrasDAO no puede ser null");
        }
        this.cbDAO = cbDAO;
    }
    
    @Override
    public void insertar(CodigoBarras cb) throws Exception {
        cbDAO.insertar(cb);
    }

    @Override
    public void actualizar(CodigoBarras cb) throws Exception {
        cbDAO.actualizar(cb);
    }

    @Override
    public void eliminar(long id) throws Exception {
        cbDAO.eliminar(id);
    }

    @Override
    public CodigoBarras getById(long id) throws Exception {
        return cbDAO.getById(id);
    }
    
    public CodigoBarras getByValor(String valor) throws Exception{
        return cbDAO.getByValor(valor);
    }

    @Override
    public List<CodigoBarras> getAll()  throws Exception {
        return cbDAO.getAll();
    }
    
}
