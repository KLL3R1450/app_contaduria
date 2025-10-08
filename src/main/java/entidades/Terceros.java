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
    private int id_tercero;
    public String nombre;
    public String rfc;
    public String cp;
    public String correo= "";
    public ArrayList<Integer> idsRegimenes = new ArrayList<>();
    
    public Terceros(int id_tercero, String nombre, String rfc, String cp, String correo) {
        this.id_tercero = id_tercero;
        this.nombre = nombre;
        this.rfc = rfc;
        this.cp = cp;
        this.correo = correo;
    }

    public Terceros(int id_tercero, String nombre, String rfc, String cp) {
        this.id_tercero = id_tercero;
        this.nombre = nombre;
        this.rfc = rfc;
        this.cp = cp;
    }
    
    public Terceros(int id_tercero){
        this.id_tercero = id_tercero;
    }
    
    public int getId(){
        return this.id_tercero;
    }
}
