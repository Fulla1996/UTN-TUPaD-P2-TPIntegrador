/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prog2int.Models;

import java.util.Objects;

/**
 *
 * @author Fulla
 */
public class CodigoBarras extends Base {
    
    private TipoCB tipoCB;
    private String valor, fecha, observaciones;

    private Producto producto;

    /**
     * Constructor completo para reconstruir una Persona desde la BD.
     * Usado por PersonaDAO al mapear ResultSet.
     * El domicilio se asigna posteriormente con setDomicilio().
     */
    public CodigoBarras(int id, String tipoCB,String valor, String fecha, String observaciones) {
        super(id, false);
        this.tipoCB = TipoCB.valueOf(tipoCB.toUpperCase());
        this.valor = valor;
        this.fecha = fecha;
        this.observaciones = observaciones;
        
    }

    /** Constructor por defecto para crear una persona nueva sin ID. */
    public CodigoBarras() {
        super();
    }

    /**
     * Compara dos personas por DNI (identificador único).
     * Dos personas son iguales si tienen el mismo DNI.
     * Correcto porque DNI es único en el sistema.
     */
    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Persona persona = (Persona) o;
        return Objects.equals(dni, persona.dni);
    }

    /**
     * Hash code basado en DNI.
     * Consistente con equals(): personas con mismo DNI tienen mismo hash.
     */
    
    
    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }
}
