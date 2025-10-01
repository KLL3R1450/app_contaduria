/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.util.ArrayList;
/**
 *
 * @author Osmar
 */
public class Terceros {
    public int id_tercero;
    public String nombre;
    public String rfc;
    public String cp;
    public String correo= "";
    public int monto;
    public ArrayList<Regimenes> regimenes = new ArrayList<>();
    
    public Terceros(int id_tercero, String nombre, String rfc, String cp, String correo, int monto) {
        this.id_tercero = id_tercero;
        this.nombre = nombre;
        this.rfc = rfc;
        this.cp = cp;
        this.correo = correo;
        this.monto = monto;
    }

    public Terceros(int id_tercero, String nombre, String rfc, String cp, int monto) {
        this.id_tercero = id_tercero;
        this.nombre = nombre;
        this.rfc = rfc;
        this.cp = cp;
        this.monto = monto;
    }
    
    
}
