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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public CodigoBarras getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(CodigoBarras codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    @Override
    public String toString() {
        return "Producto{" + "nombre=" + nombre + ", marca=" + marca + ", categoria=" + categoria + ", precio=" + precio + ", peso=" + peso + ", codigoBarras=" + codigoBarras + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.codigoBarras);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Producto other = (Producto) obj;
        return Objects.equals(this.codigoBarras, other.codigoBarras);
    }

    
}
