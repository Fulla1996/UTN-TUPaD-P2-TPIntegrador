/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package prog2int.Models;

/**
 *
 * @author Fulla
 */
public enum TipoCB {
    EAN13,
    EAN8,
    UPC;
    
    public static TipoCB controlString(String valor){
            return TipoCB.valueOf(valor.toUpperCase());
    }
}
