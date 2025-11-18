/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prog2int.Service;

import java.sql.Connection;
import java.util.List;
import prog2int.Dao.CodigoBarrasDAO;
import prog2int.Models.CodigoBarras;

/**
 *
 * @author Fulla
 */
public class CodigoBarrasServiceImpl implements GenericService<CodigoBarras>{

    private final CodigoBarrasDAO codigoBarrasDAO;

    public CodigoBarrasServiceImpl() {
        this.codigoBarrasDAO = new CodigoBarrasDAO();
    }
    
        private long generarNuevoId() throws Exception {
        Long maxId = codigoBarrasDAO.getMaxId();
        return (maxId == null ? 1 : maxId + 1);
        }
        
        private void validarCodigo(CodigoBarras cb) throws Exception {

        if (cb.getValor() == null || cb.getValor().isBlank()) {
            throw new Exception("El valor del código de barras no puede estar vacío.");
        }

        // Solo dígitos
        if (!cb.getValor().matches("\\d+")) {
            throw new Exception("El código de barras solo puede contener números.");
        }

        int longitud = cb.getValor().length();

        switch (cb.getTipoCB()) {
            case EAN13:
                if (longitud != 13) {
                    throw new Exception("EAN13 debe tener exactamente 13 dígitos.");
                }
                break;

            case EAN8:
                if (longitud != 8) {
                    throw new Exception("EAN8 debe tener exactamente 8 dígitos.");
                }
                break;

            case UPC:
                if (longitud != 12) {
                    throw new Exception("UPC debe tener exactamente 12 dígitos.");
                }
                break;

            default:
                throw new Exception("Tipo de código de barras no reconocido.");
        }
    }
        public CodigoBarras getByValor(String valor) {
    return codigoBarrasDAO.getByValor(valor);
}
        
  
    @Override
    public void insertar(CodigoBarras cb) throws Exception {
        validarCodigo(cb); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        
        cb.setId(generarNuevoId());
        codigoBarrasDAO.insertar(cb);
    }
    
    public void insertarTx(CodigoBarras cb, Connection conn) throws Exception {
    codigoBarrasDAO.insertTx(cb, conn);
}


    @Override
    public void actualizar(CodigoBarras cb) throws Exception {
        if (cb.getId() <= 0) {
            throw new Exception("El ID es inválido para actualizar.");
        }

        validarCodigo(cb); // validación completa

        codigoBarrasDAO.actualizar(cb);
    }

    @Override
    public void eliminar(long id) throws Exception {
        codigoBarrasDAO.eliminar(id); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CodigoBarras getById(long id) throws Exception {
        return codigoBarrasDAO.getById(id); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<CodigoBarras> getAll()  throws Exception {
        return codigoBarrasDAO.getAll();
    }
    
}
