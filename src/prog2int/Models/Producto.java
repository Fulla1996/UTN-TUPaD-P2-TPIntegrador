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
public class Producto extends Base {
    
private String nombre, marca, categoria;
private double precio, peso;
private CodigoBarras codigoBarras;
    /**
     * Constructor completo para reconstruir una Persona desde la BD.
     * Usado por PersonaDAO al mapear ResultSet.
     * El domicilio se asigna posteriormente con setDomicilio().
     */
    public Producto(int id, String nombre, String marca, String categoria, double precio, double peso, CodigoBarras cb) {
        super(id, false);
        this.nombre = nombre;
        this.marca = marca;
        this.categoria = categoria;
        this.precio = precio;
        this.peso = peso;
        this.codigoBarras = cb;
    }

    /** Constructor por defecto para crear una persona nueva sin ID. */
    public Producto() {
        super();
    }

}
