/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prog2int.Models;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Fulla
 */
public class CodigoBarras extends Base {
    
    private TipoCB tipoCB;
    private String valor, observaciones;
    private Date fecha;

    private Producto producto;

    /**
     * Constructor completo para reconstruir una Persona desde la BD.
     * Usado por PersonaDAO al mapear ResultSet.
     * El domicilio se asigna posteriormente con setDomicilio().
     */
    public CodigoBarras(long id, String tipoCB,String valor, Date fecha, String observaciones) {
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

    public TipoCB getTipoCB() {
        return tipoCB;
    }

    public void setTipoCB(TipoCB tipoCB) {
        this.tipoCB = tipoCB;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public String toString() {
        return "CodigoBarras{" + "tipoCB=" + tipoCB + ", valor=" + valor + ", fecha=" + fecha + ", observaciones=" + observaciones + ", producto=" + producto + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.tipoCB);
        hash = 79 * hash + Objects.hashCode(this.valor);
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
        final CodigoBarras other = (CodigoBarras) obj;
        if (!Objects.equals(this.valor, other.valor)) {
            return false;
        }
        return this.tipoCB == other.tipoCB;
    }

    
}
