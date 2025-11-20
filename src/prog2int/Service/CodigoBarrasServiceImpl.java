/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prog2int.Service;

import java.sql.Connection;
import java.util.List;
import prog2int.Dao.CodigoBarrasDAO;
import prog2int.Models.CodigoBarras;
import prog2int.Models.TipoCB;

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
    
    public boolean idExists(long id) throws Exception{
        return cbDAO.idExists(id);
    }
    public CodigoBarras getByValor(String valor) throws Exception{
        return cbDAO.getByValor(valor);
    }

    @Override
    public List<CodigoBarras> getAll()  throws Exception {
        return cbDAO.getAll();
    }
    
    public void insertarTx(CodigoBarras cb, Connection conn) throws Exception{
        cbDAO.insertTx(cb, conn);
    }
    
    public boolean validarCodigo(String tipo, String valor) {

        if (valor == null || valor.isBlank()) {
            System.out.println("El valor del código de barras no puede estar vacío.");
            return false;
        }

        // Solo dígitos
        if (!valor.matches("\\d+")) {
            System.out.println("El código de barras solo puede contener números.");
            return false;
        }

        int longitud = valor.length();

        try{
        switch (TipoCB.valueOf(tipo)) {
            case EAN13:
                if (longitud != 13) {
                    System.out.println("EAN13 debe tener exactamente 13 dígitos.");
                    return false;
                }
                break;

            case EAN8:
                if (longitud != 8) {
                    System.out.println("EAN8 debe tener exactamente 8 dígitos.");
                    return false;
                }
                break;

            case UPC:
                if (longitud != 12) {
                    System.out.println("UPC debe tener exactamente 12 dígitos.");
                    return false;
                }
                break;

            default:
                System.out.println("Tipo de código de barras no reconocido.");
                return false;
        }
        
        return true;
        }
        catch(Exception e){
            System.out.println("Tipo de código de barras no reconocido.");
            return false;
        }
    }
}